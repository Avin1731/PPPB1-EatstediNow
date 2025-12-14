package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Import Model dan Warna
import com.example.eatstedinow.ui.theme.OrangePrimary

// Dummy Data Class khusus untuk Cart
data class CartItem(
    val id: Int,
    val name: String,
    val variant: String,
    val price: Int,
    val imageUrl: String,
    var quantity: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBack: () -> Unit,
    onProcess: () -> Unit,
    // Callback Navigasi Bottom Bar
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // STATE: Daftar belanjaan (Dummy)
    val cartItems = remember {
        mutableStateListOf(
            CartItem(1, "Cireng Isi", "Abon/Ayam/Keju", 3000, "https://ui-avatars.com/api/?name=Cireng", 1),
            CartItem(2, "Tahu Bakso", "Pedas", 2500, "https://ui-avatars.com/api/?name=Tahu", 2)
        )
    }

    // LOGIC: Hitung Total Otomatis
    val subtotal = cartItems.sumOf { it.price * it.quantity }
    val tax = 500
    val total = subtotal + tax
    val totalItems = cartItems.sumOf { it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order#AQ2140", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OrangePrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    selected = false,
                    onClick = onHomeClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MenuBook, "Menu") },
                    label = { Text("Menu", fontSize = 10.sp) },
                    selected = false,
                    onClick = onMenuClick
                )
                // Tombol Order (Aktif)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, "Order") },
                    label = { Text("Order", fontSize = 10.sp) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)),
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    selected = false,
                    onClick = onProfileClick
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // 1. Info Meja
            item {
                Spacer(modifier = Modifier.height(8.dp))
                TableInfoCard()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. List Pesanan
            item {
                Text("Pesanan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(cartItems) { item ->
                CartItemCard(
                    item = item,
                    onAdd = {
                        val index = cartItems.indexOf(item)
                        cartItems[index] = item.copy(quantity = item.quantity + 1)
                    },
                    onRemove = {
                        if (item.quantity > 0) {
                            val index = cartItems.indexOf(item)
                            cartItems[index] = item.copy(quantity = item.quantity - 1)
                        }
                    }
                )
            }

            // 3. Ringkasan Pembayaran
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Total Pesanan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                PaymentSummarySection(subtotal, tax, total)
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 4. Tombol Proses Order
            item {
                Button(
                    onClick = onProcess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Proses Order ($totalItems item)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun TableInfoCard() {
    Card(
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth().border(1.dp, Color.LightGray, RoundedCornerShape(50.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TableRestaurant, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Meja 05", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
            Button(
                onClick = { /* Logic Ganti Meja */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)), // Warna Hijau
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Ganti Meja", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, onAdd: () -> Unit, onRemove: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl, contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(35.dp)).background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.variant, fontSize = 12.sp, color = OrangePrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("RP ${item.price}", fontWeight = FontWeight.Bold)
            }
            // Counter Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            ) {
                IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                    Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold)
                IconButton(onClick = onAdd, modifier = Modifier.size(32.dp)) {
                    Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
fun PaymentSummarySection(subtotal: Int, tax: Int, total: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.Gray)
            Text("RP $subtotal", color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Pajak", color = Color.Gray)
            Text("RP $tax", color = Color.Gray)
        }
        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("RP $total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}