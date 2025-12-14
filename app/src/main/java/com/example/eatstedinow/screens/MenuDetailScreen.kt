package com.example.eatstedinow.screens

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Import Model dan Warna
import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.ui.theme.OrangePrimary

@Composable
fun MenuDetailScreen(
    food: FoodItem,
    onBack: () -> Unit,
    onAddToCart: () -> Unit
) {
    Scaffold(
        // Tombol "Tambah ke Keranjang" yang melayang di bawah
        bottomBar = {
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(25.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Tambah ke Keranjang",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    ) { paddingValues ->
        // Background utama (Kuning tipis/Pattern di atas)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFFFF8E1)) // Warna latar belakang atas (krem cerah)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. Header: Tombol Back
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .size(40.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
            }

            // 2. Gambar Makanan Besar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp), // Tinggi area gambar
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = food.imageUrl,
                    contentDescription = food.name,
                    modifier = Modifier
                        .size(240.dp) // Ukuran gambar
                        .clip(CircleShape)
                        // Efek border putih tebal seperti piring
                        .border(4.dp, Color.White, CircleShape), 
                    contentScale = ContentScale.Crop
                )
            }

            // 3. Panel Putih Melengkung (Detail Info)
            Surface(
                modifier = Modifier.fillMaxSize(),
                // Lengkungan hanya di pojok kiri atas & kanan atas
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    // Judul & Harga
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = food.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "RP ${food.price}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangePrimary
                        )
                    }

                    // Subtitle (Varian Rasa - Dummy text sesuai gambar)
                    Text(
                        text = "Keju/Abon/Pedas", 
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Rating Pill (Tombol Kecil Abu-abu)
                    Row(
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star, 
                            contentDescription = null, 
                            tint = Color(0xFFFFC107), // Warna emas
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${food.rating}", 
                            fontWeight = FontWeight.Bold, 
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Deskripsi Panjang
                    Text(
                        text = food.description,
                        fontSize = 14.sp,
                        color = Color(0xFF616161), // Abu-abu gelap agar mudah dibaca
                        lineHeight = 22.sp // Jarak antar baris biar rapi
                    )
                    
                    // Spacer tambahan agar teks paling bawah tidak tertutup tombol
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}