package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eatstedinow.CartState
import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.model.dummyFoods
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MenuDetailScreen(
    foodId: String,
    onBack: () -> Unit,
    onAddToCart: () -> Unit
) {
    val context = LocalContext.current
    var food by remember { mutableStateOf(dummyFoods.find { it.id == foodId }) }
    var isLoading by remember { mutableStateOf(food == null) }

    LaunchedEffect(foodId) {
        if (food == null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("menus").document(foodId).get()
                .addOnSuccessListener { doc ->
                    if (doc.exists()) {
                        food = FoodItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getLong("price")?.toInt() ?: 0,
                            imageUrl = doc.getString("imageUrl") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            stock = doc.getLong("stock")?.toInt() ?: 0
                        )
                    }
                    isLoading = false
                }
                .addOnFailureListener { isLoading = false }
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) }
    } else if (food != null) {
        val item = food!!
        Scaffold(
            bottomBar = {
                Button(
                    onClick = {
                        // MASUKKAN KE GLOBAL CART STATE
                        CartState.addItem(item)
                        Toast.makeText(context, "${item.name} masuk keranjang", Toast.LENGTH_SHORT).show()
                        onAddToCart() // Navigasi ke Order
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Tambah ke Keranjang", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8E1)).padding(paddingValues).verticalScroll(rememberScrollState())
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp)).size(40.dp)) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.Black)
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
                    AsyncImage(model = item.imageUrl, contentDescription = item.name, modifier = Modifier.size(240.dp).clip(CircleShape).border(4.dp, Color.White, CircleShape), contentScale = ContentScale.Crop)
                }
                Surface(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp), color = Color.White, shadowElevation = 4.dp) {
                    Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(item.name, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("RP ${item.price}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = OrangePrimary)
                        }
                        Text("Stok Tersedia: ${item.stock}", fontSize = 14.sp, color = if(item.stock>0) Color.Gray else Color.Red, modifier = Modifier.padding(top = 4.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp)).padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${item.rating}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(item.description, fontSize = 14.sp, color = Color(0xFF616161), lineHeight = 22.sp)
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Item tidak ditemukan") }
    }
}