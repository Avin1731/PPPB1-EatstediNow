package com.example.eatstedinow

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatstedinow.R // Pastikan R di-import untuk akses gambar

// 1. Kita buat Data Model sederhana khusus untuk Cart
data class CartItem(
    val id: Int,
    val name: String,
    val variant: String,
    val price: Int,
    var quantity: Int // var karena jumlahnya bisa berubah
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    onBack: () -> Unit,
    onProcess: () -> Unit
) {
    // 2. STATE: Ini adalah "Otak" halaman ini.
    // Kita pakai 'remember' agar data tidak hilang saat layar discroll/direfresh
    val cartItems = remember {
        mutableStateListOf(
            // Data Dummy Awal (Nanti bisa diganti database)
            CartItem(1, "Cireng Isi", "Abon/Ayam/Keju", 3000, 1),
            // Coba tambahkan item lain untuk tes:
            CartItem(2, "Es Teh Manis", "Dingin", 4000, 2)
        )
    }

    // 3. LOGIC: Rumus Matematika Otomatis
    // 'derivedStateOf' memastikan hitungan ini update tiap kali 'cartItems' berubah
    val subtotal by remember {
        derivedStateOf { cartItems.sumOf { it.price * it.quantity } }
    }
    val tax = 500
    val totalToPay by remember {
        derivedStateOf { subtotal + tax }
    }
    val totalItems by remember {
        derivedStateOf { cartItems.sumOf { it.quantity } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order#AQ2140", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFFFF8C00))
                    }
                }
            )
        },
        bottomBar = {
            // Tombol Proses di Bawah
            Button(
                onClick = onProcess,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                Text(
                    text = "Proses Order ($totalItems item) - Rp $totalToPay",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        // Konten Utama
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Bagian Info Meja
            item {
                TableInfoCard()
                Spacer(modifier = Modifier.height(24.dp))
                Text("Pesanan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Bagian List Makanan (Looping otomatis sesuai jumlah item)
            items(cartItems) { item ->
                CartItemCard(
                    item = item,
                    onAdd = {
                        // Logic Tambah
                        val index = cartItems.indexOf(item)
                        cartItems[index] = item.copy(quantity = item.quantity + 1)
                    },
                    onRemove = {
                        // Logic Kurang (Cek agar tidak minus)
                        if (item.quantity > 0) {
                            val index = cartItems.indexOf(item)
                            cartItems[index] = item.copy(quantity = item.quantity - 1)
                        }
                    }
                )
            }

            // Bagian Ringkasan Harga
            item {
                Spacer(modifier = Modifier.height(24.dp))
                PaymentSummary(subtotal, tax, totalToPay)
                Spacer(modifier = Modifier.height(100.dp)) // Jarak agar tidak tertutup tombol bawah
            }
        }
    }
}

// --- Komponen-komponen Kecil (UI Components) ---

@Composable
fun TableInfoCard() {
    Card(
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Meja 05", fontWeight = FontWeight.SemiBold)
            Button(
                onClick = { /* Todo: Ganti Meja */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008000)),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Ganti Meja", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar (Placeholder Kotak Abu-abu jika belum ada gambar)
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                // Jika sudah ada gambar di res/drawable, uncomment baris bawah:
                // Image(painter = painterResource(id = R.drawable.ic_launcher_background), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.variant, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Rp ${item.price}", fontWeight = FontWeight.Bold, color = Color.Black)
            }

            // Tombol Plus Minus
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRemove, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Remove, contentDescription = "Kurang", tint = Color.Gray)
                }
                Text(
                    text = "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                IconButton(onClick = onAdd, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", tint = Color(0xFFFF8C00))
                }
            }
        }
    }
}

@Composable
fun PaymentSummary(subtotal: Int, tax: Int, total: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Subtotal", color = Color.Gray)
            Text("Rp $subtotal", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Pajak", color = Color.Gray)
            Text("Rp $tax", fontWeight = FontWeight.Bold)
        }
        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray)
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Rp $total", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFFF8C00))
        }
    }
}