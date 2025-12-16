package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatstedinow.model.OrderHistory
import com.example.eatstedinow.model.OrderItem
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    var orderList by remember { mutableStateOf<List<OrderHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // State Rating Susulan
    var showRatingDialog by remember { mutableStateOf(false) }
    var selectedOrderToRate by remember { mutableStateOf<OrderHistory?>(null) }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("orders")
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, _ ->
                    if (snapshot != null) {
                        orderList = snapshot.documents.mapNotNull { doc ->
                            val itemsList = (doc.get("items") as? List<Map<String, Any>>)?.map {
                                OrderItem(
                                    foodId = it["foodId"] as? String ?: "",
                                    name = it["name"] as? String ?: "",
                                    price = (it["price"] as? Long)?.toInt() ?: 0,
                                    quantity = (it["quantity"] as? Long)?.toInt() ?: 0,
                                    imageUrl = it["imageUrl"] as? String ?: ""
                                )
                            } ?: emptyList()
                            OrderHistory(
                                id = doc.id,
                                userId = doc.getString("userId") ?: "",
                                total = doc.getLong("total")?.toInt() ?: 0,
                                isRated = doc.getBoolean("isRated") ?: false,
                                items = itemsList
                            )
                        }
                        isLoading = false
                    }
                }
        }
    }

    if (showRatingDialog && selectedOrderToRate != null) {
        RatingDialog(
            onRate = { stars ->
                val orderId = selectedOrderToRate!!.id
                db.collection("orders").document(orderId).update("isRated", true)
                selectedOrderToRate!!.items.forEach { updateFoodRating(db, it.foodId, stars) }
                Toast.makeText(context, "Terima kasih!", Toast.LENGTH_SHORT).show()
                showRatingDialog = false
            },
            onLater = {
                // Jika user tekan "Tidak Menilai" di history, kita anggap selesai
                // Agar dot merah hilang.
                val orderId = selectedOrderToRate!!.id
                db.collection("orders").document(orderId).update("isRated", true)
                Toast.makeText(context, "Baik, tidak dinilai.", Toast.LENGTH_SHORT).show()
                showRatingDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Riwayat Pembelian") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } })
        }
    ) { p ->
        if (isLoading) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) }
        else if (orderList.isEmpty()) Box(Modifier.fillMaxSize().padding(p), contentAlignment = Alignment.Center) { Text("Belum ada riwayat") }
        else {
            LazyColumn(modifier = Modifier.padding(p).padding(16.dp)) {
                items(orderList) { order ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Order #${order.id.takeLast(6).uppercase()}", fontWeight = FontWeight.Bold)
                                if (!order.isRated) Box(Modifier.size(10.dp).background(Color.Red, androidx.compose.foundation.shape.CircleShape))
                                else Text("Selesai", color = OrangePrimary, fontSize = 12.sp)
                            }
                            Spacer(Modifier.height(8.dp))
                            order.items.forEach { Text("${it.quantity}x ${it.name}", fontSize = 14.sp, color = Color.Gray) }
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("Total: Rp${order.total}", fontWeight = FontWeight.Bold)
                                if (!order.isRated) {
                                    Button(onClick = { selectedOrderToRate = order; showRatingDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary), modifier = Modifier.height(36.dp)) { Text("Beri Nilai / Tutup", fontSize = 12.sp) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}