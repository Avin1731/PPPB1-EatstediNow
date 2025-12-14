package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Import data dari Model
import com.example.eatstedinow.model.dummyFoods
// Import Warna dari Tema (Pastikan nama variabelnya sesuai file Color.kt kamu)
import com.example.eatstedinow.ui.theme.OrangePrimary

// Warna khusus halaman ini (jika belum ada di Color.kt)
private val MenuBgColor = Color(0xFFFAFAFA)
private val SelectedTabColor = Color.Black
private val UnselectedTabColor = Color.Gray

@Composable
fun MenuScreen(
    onFoodClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit // Navigasi balik ke Home
) {
    var isDineIn by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Makanan", "Minuman", "Es Krim", "Snack")

    Scaffold(
        bottomBar = {
            // Kita pasang Bottom Bar lagi agar konsisten
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 10.dp
            ) {
                // 1. Home
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    selected = false,
                    onClick = onHomeClick
                )
                // 2. Menu (Aktif)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MenuBook, "Menu") },
                    label = { Text("Menu", fontSize = 10.sp) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)),
                    onClick = {}
                )
                // --- TOMBOL SEARCH DIHAPUS ---

                // 3. Order
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, "Cart") },
                    label = { Text("Order", fontSize = 10.sp) },
                    selected = false,
                    onClick = onCartClick
                )
                // 4. Profile
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, "Profile") },
                    label = { Text("Profile", fontSize = 10.sp) },
                    selected = false,
                    onClick = onProfileClick
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MenuBgColor)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // 1. Judul Halaman
            Text("Menu", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Toggle Dine In / Take Away (Reuse komponen yang sama)
            DineInTakeAwayToggle(isDineIn) { isDineIn = it }
            
            Spacer(modifier = Modifier.height(24.dp))

            // 3. Tab Kategori (Scroll Samping)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { selectedCategory = category }
                    ) {
                        Text(
                            text = category,
                            fontSize = 16.sp,
                            fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedCategory == category) SelectedTabColor else UnselectedTabColor
                        )
                        if (selectedCategory == category) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(OrangePrimary, CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Grid Makanan (2 Kolom)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 2 Kolom
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Tampilkan semua dummyFoods
                items(dummyFoods) { food ->
                    // Kita pakai FoodCardVertical yang sudah kamu punya di HomeScreen
                    // Kalau merah, pastikan HomeScreen dan MenuScreen satu package.
                    FoodCardVertical(food = food, onClick = { onFoodClick(food.id) })
                }
                // Tambahan spacer bawah biar list paling bawah gak ketutup navigasi
                item { Spacer(modifier = Modifier.height(80.dp)) }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}