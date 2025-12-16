package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.eatstedinow.CartState
import com.example.eatstedinow.CartItem
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBack: () -> Unit,
    onProcess: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid

    // Global State Cart
    val cartItems = CartState.items
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val tax = if (cartItems.isNotEmpty()) 500 else 0
    val total = subtotal + tax

    // State Dialog Rating
    var showRatingDialog by remember { mutableStateOf(false) }
    var currentOrderId by remember { mutableStateOf("") }

    // --- DIALOG RATING ---
    if (showRatingDialog) {
        RatingDialog(
            onRate = { stars ->
                if (currentOrderId.isNotEmpty()) {
                    // 1. Tandai sudah dinilai
                    db.collection("orders").document(currentOrderId).update("isRated", true)
                    // 2. Update Rata-rata Makanan
                    cartItems.forEach { item -> updateFoodRating(db, item.id, stars) }
                    Toast.makeText(context, "Terima kasih!", Toast.LENGTH_SHORT).show()
                }
                showRatingDialog = false
                CartState.clear()
                onProcess() // Pindah ke History
            },
            onLater = {
                Toast.makeText(context, "Bisa dinilai nanti di Profile (Dot Merah)", Toast.LENGTH_SHORT).show()
                showRatingDialog = false
                CartState.clear()
                onProcess()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Keranjang", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = OrangePrimary) } }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = false, onClick = onHomeClick)
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = false, onClick = onMenuClick)
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Order") }, label = { Text("Order", fontSize = 10.sp) }, selected = true, colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)), onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile", fontSize = 10.sp) }, selected = false, onClick = onProfileClick)
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            if (cartItems.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Keranjang Kosong", color = Color.Gray, fontSize = 18.sp)
                    }
                }
            } else {
                item { Spacer(modifier = Modifier.height(8.dp)); TableInfoCard(); Spacer(modifier = Modifier.height(24.dp)) }
                item { Text("Pesanan", fontWeight = FontWeight.Bold, fontSize = 18.sp); Spacer(modifier = Modifier.height(16.dp)) }

                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        onAdd = { CartState.addItemCart(item) },
                        onRemove = { CartState.removeItem(item) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    PaymentSummarySection(subtotal, tax, total)
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            if (userId == null) {
                                Toast.makeText(context, "Login dulu!", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            // TRANSAKSI FIREBASE
                            val newOrderRef = db.collection("orders").document()

                            db.runTransaction { transaction ->
                                // 1. Cek & Kurangi Stok
                                cartItems.forEach { cartItem ->
                                    if(cartItem.id.length > 5) { // Skip dummy id pendek
                                        val foodRef = db.collection("menus").document(cartItem.id)
                                        val snapshot = transaction.get(foodRef)
                                        val currentStock = snapshot.getLong("stock") ?: 0
                                        if (currentStock >= cartItem.quantity) {
                                            transaction.update(foodRef, "stock", currentStock - cartItem.quantity)
                                        } else {
                                            throw Exception("Stok ${cartItem.name} habis!")
                                        }
                                    }
                                }

                                // 2. Simpan Order
                                val orderData = hashMapOf(
                                    "userId" to userId,
                                    "total" to total,
                                    "date" to FieldValue.serverTimestamp(),
                                    "isRated" to false,
                                    "items" to cartItems.map {
                                        mapOf("foodId" to it.id, "name" to it.name, "price" to it.price, "quantity" to it.quantity, "imageUrl" to it.imageUrl)
                                    }
                                )
                                transaction.set(newOrderRef, orderData)
                            }.addOnSuccessListener {
                                currentOrderId = newOrderRef.id
                                showRatingDialog = true
                            }.addOnFailureListener {
                                Toast.makeText(context, "Gagal: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(25.dp)
                    ) { Text("Bayar & Proses", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// --- LOGIC MATEMATIKA RATING ---
fun updateFoodRating(db: FirebaseFirestore, foodId: String, newRating: Int) {
    if (foodId.length < 5) return
    val ref = db.collection("menus").document(foodId)
    db.runTransaction { transaction ->
        val snapshot = transaction.get(ref)
        if (snapshot.exists()) {
            val currentRating = snapshot.getDouble("rating") ?: 0.0
            val currentCount = snapshot.getLong("ratingCount") ?: 0L

            // Rumus Rata-rata
            val totalScore = (currentRating * currentCount) + newRating
            val newCount = currentCount + 1
            val finalRating = totalScore / newCount

            transaction.update(ref, "rating", finalRating)
            transaction.update(ref, "ratingCount", newCount)
        }
    }
}

// --- KOMPONEN DIALOG ---
@Composable
fun RatingDialog(onRate: (Int) -> Unit, onLater: () -> Unit) {
    var selectedStars by remember { mutableStateOf(0) }
    Dialog(onDismissRequest = {}) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Pembelian Berhasil!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("Beri nilai sekarang?", color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Row {
                    for (i in 1..5) {
                        Icon(if (i <= selectedStars) Icons.Filled.Star else Icons.Outlined.Star, "Star", tint = Color(0xFFFFC107), modifier = Modifier.size(40.dp).clickable { selectedStars = i })
                    }
                }
                Spacer(Modifier.height(24.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onLater) { Text("Nanti Saja", color = Color.Gray) }
                    Button(onClick = { if (selectedStars > 0) onRate(selectedStars) }, enabled = selectedStars > 0, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) { Text("Kirim") }
                }
            }
        }
    }
}

// --- COMPONENT PENDUKUNG (Card Item Tanpa Variant) ---
@Composable
fun TableInfoCard() {
    Card(shape = RoundedCornerShape(50.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray, RoundedCornerShape(50.dp))) {
        Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.TableRestaurant, null, tint = Color.Black); Spacer(modifier = Modifier.width(12.dp)); Text("Meja 05", fontWeight = FontWeight.SemiBold, fontSize = 16.sp) }
            Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), contentPadding = PaddingValues(horizontal = 16.dp), modifier = Modifier.height(36.dp)) { Text("Ganti", fontSize = 12.sp) }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = item.imageUrl, contentDescription = null, modifier = Modifier.size(70.dp).clip(RoundedCornerShape(35.dp)).background(Color.LightGray))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                // VARIANT DIHAPUS SUPAYA TIDAK ERROR
                Text("Original", fontSize = 12.sp, color = OrangePrimary)
                Text("RP ${item.price}", fontWeight = FontWeight.Bold)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) { Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
                Text("${item.quantity}", fontWeight = FontWeight.Bold)
                IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) { Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
            }
        }
    }
}

@Composable
fun PaymentSummarySection(subtotal: Int, tax: Int, total: Int) {
    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp)).padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Subtotal", color = Color.Gray); Text("RP $subtotal", color = Color.Gray) }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Pajak", color = Color.Gray); Text("RP $tax", color = Color.Gray) }
        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) { Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp); Text("RP $total", fontWeight = FontWeight.Bold, fontSize = 16.sp) }
    }
}