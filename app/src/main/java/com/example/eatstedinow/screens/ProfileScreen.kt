package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
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

// Import Warna Tema
import com.example.eatstedinow.ui.theme.OrangePrimary

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    // Navigasi Bottom Bar
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 10.dp
            ) {
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
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, "Cart") },
                    label = { Text("Order", fontSize = 10.sp) },
                    selected = false,
                    onClick = onCartClick
                )
                // Profile (Aktif)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)),
                    onClick = {}
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            
            // 1. Judul Halaman
            Text("My Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(32.dp))

            // 2. Foto Profil Besar dengan Border Oranye
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .border(3.dp, OrangePrimary, CircleShape)
                    .padding(4.dp) // Jarak antara border dan foto
            ) {
                AsyncImage(
                    model = "https://ui-avatars.com/api/?name=Tito+Alla&background=FF8C00&color=fff&size=128",
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Nama User
            Text("Tito Alla", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(40.dp))

            // 3. Menu Section: Account
            SectionTitle("Account")
            ProfileMenuItem("Informasi Pribadi", onClick = {})
            ProfileMenuItem("Riwayat Pembelian", onClick = {})
            
            Spacer(modifier = Modifier.height(24.dp))

            // 4. Menu Section: System
            SectionTitle("System")
            
            // App Version Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("App Version", fontSize = 16.sp)
                Text("1.01", fontSize = 16.sp, color = Color.Gray)
            }
            
            // Logout Button (Text Only)
            TextButton(
                onClick = onLogout,
                contentPadding = PaddingValues(0.dp), // Hapus padding default agar rata kiri
                modifier = Modifier.align(Alignment.Start) // Rata kiri
            ) {
                Text("Log Out", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    )
}

@Composable
fun ProfileMenuItem(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon User/History bisa ditambahkan di kiri jika mau (sesuai gambar ada icon kecil)
        // Di sini saya buat text-nya saja dulu agar simpel
        Text(text, fontSize = 16.sp)
        
        Icon(
            Icons.Default.KeyboardArrowRight, 
            contentDescription = null, 
            tint = Color.Black
        )
    }
}