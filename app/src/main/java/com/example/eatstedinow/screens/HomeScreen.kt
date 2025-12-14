package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.model.dummyFoods

import com.example.eatstedinow.ui.theme.OrangePrimary

@Composable
fun HomeScreen(
    onFoodClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    // Kita gunakan warna OrangePrimary dari import. 
    // Untuk warna pelengkap yang mungkin belum ada di Color.kt, kita define aman di sini atau kamu pindahkan ke Color.kt
    val lightOrangeColor = Color(0xFFFFF3E0) 
    val grayTextColor = Color(0xFF9E9E9E)
    val backgroundColor = Color(0xFFFAFAFA)

    var isDineIn by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 10.dp
            ) {
                // 1. Home (Aktif)
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, "Home") },
                    label = { Text("Home", fontSize = 10.sp) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)),
                    onClick = {}
                )
                // 2. Menu
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MenuBook, "Menu") },
                    label = { Text("Menu", fontSize = 10.sp) },
                    selected = false,
                    onClick = onMenuClick // Pastikan ini nyambung ke parameter onMenuClick
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HomeHeader(grayTextColor)
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                DineInTakeAwayToggle(isDineIn) { isDineIn = it }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Text("Kategori", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                CategorySection(lightOrangeColor)
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                VoucherBanner(lightOrangeColor)
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Text("Sedang Populer", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(dummyFoods) { food ->
                        FoodCardVertical(food = food, onClick = { onFoodClick(food.id) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Text("Sedang Promo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(dummyFoods.reversed()) { food ->
                        FoodCardVertical(food = food, onClick = { onFoodClick(food.id) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// --- SUB-COMPONENTS ---

@Composable
fun HomeHeader(grayColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = "https://ui-avatars.com/api/?name=Tito+Alla&background=FF8C00&color=fff",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Selamat pagi!", fontSize = 12.sp, color = grayColor)
                Text("Tito Alla", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.LightGray, CircleShape)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Notifications, "Notif", tint = Color.Black)
        }
    }
}

@Composable
fun DineInTakeAwayToggle(isDineIn: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(50.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight()
                .background(if (isDineIn) Color.White else Color.Transparent, RoundedCornerShape(50.dp))
                .clickable { onToggle(true) },
            contentAlignment = Alignment.Center
        ) { Text("Dine In", fontWeight = if (isDineIn) FontWeight.Bold else FontWeight.Normal) }
        
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight()
                .background(if (!isDineIn) Color.White else Color.Transparent, RoundedCornerShape(50.dp))
                .clickable { onToggle(false) },
            contentAlignment = Alignment.Center
        ) { Text("Take Away", fontWeight = if (!isDineIn) FontWeight.Bold else FontWeight.Normal) }
    }
}

@Composable
fun CategorySection(lightOrange: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        CategoryItem(Icons.Default.Percent, "Promo", Color(0xFFE8F5E9), Color(0xFF2E7D32))
        // Pakai OrangePrimary di sini
        CategoryItem(Icons.Default.LunchDining, "Makanan", lightOrange, OrangePrimary)
        CategoryItem(Icons.Default.LocalDrink, "Minuman", Color(0xFFE3F2FD), Color(0xFF1565C0))
        CategoryItem(Icons.Default.Icecream, "Es Krim", Color(0xFFFFEBEE), Color(0xFFC62828))
    }
}

@Composable
fun CategoryItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, bgColor: Color, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(65.dp).background(bgColor, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp)) }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun VoucherBanner(lightOrange: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = lightOrange),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Voucher Khusus Pengguna Baru", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Dapatkan diskon spesial sekarang!", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) { Text("Pakai", fontSize = 12.sp) }
            }
            Icon(Icons.Default.ConfirmationNumber, null, tint = OrangePrimary, modifier = Modifier.size(60.dp).graphicsLayer(rotationZ = -15f))
        }
    }
}

@Composable
fun FoodCardVertical(food: FoodItem, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.width(160.dp).clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(contentAlignment = Alignment.TopEnd) {
                AsyncImage(
                    model = food.imageUrl, contentDescription = null,
                    modifier = Modifier.size(136.dp).clip(CircleShape).align(Alignment.Center),
                    contentScale = ContentScale.Crop
                )
                Icon(Icons.Default.LocalFireDepartment, null, tint = Color(0xFFFF5722), modifier = Modifier.offset(x = 4.dp, y = (-4).dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(food.name, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Rp${food.price}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFFEEEEEE), RoundedCornerShape(12.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                    Text("-", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
                    Text("0", fontSize = 12.sp)
                    Text("+", fontSize = 12.sp, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }
        }
    }
}