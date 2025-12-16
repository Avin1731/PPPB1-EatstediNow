package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    onFoodClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMenuClick: (String) -> Unit
) {
    val lightOrangeColor = Color(0xFFFFF3E0)
    val grayTextColor = Color(0xFF9E9E9E)
    val backgroundColor = Color(0xFFFAFAFA)
    var isDineIn by remember { mutableStateOf(true) }

    var menuList by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // FETCH DATA
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("menus").addSnapshotListener { snapshot, e ->
            if (e != null) { isLoading = false; return@addSnapshotListener }
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    try {
                        FoodItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = doc.getString("description") ?: "",
                            price = doc.getLong("price")?.toInt() ?: 0,
                            imageUrl = doc.getString("imageUrl") ?: "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            category = doc.getString("category") ?: "Makanan",
                            stock = doc.getLong("stock")?.toInt() ?: 0
                        )
                    } catch (e: Exception) { null }
                }
                menuList = items
                isLoading = false
            }
        }
    }

    val displayList = if (!isLoading && menuList.isEmpty()) dummyFoods else menuList

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = true, colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = lightOrangeColor), onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = false, onClick = { onMenuClick("Semua") })
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Cart") }, label = { Text("Order", fontSize = 10.sp) }, selected = false, onClick = onCartClick)
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile", fontSize = 10.sp) }, selected = false, onClick = onProfileClick)
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().background(backgroundColor).padding(paddingValues).padding(horizontal = 16.dp)) {
            // HEADER DINAMIS
            item { Spacer(modifier = Modifier.height(16.dp)); HomeHeader(grayTextColor); Spacer(modifier = Modifier.height(24.dp)) }

            item { DineInTakeAwayToggle(isDineIn) { isDineIn = it }; Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text("Kategori", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                CategorySection(lightOrangeColor, onCategoryClick = { cat -> onMenuClick(cat) }) // LIST KATEGORI SUDAH LENGKAP DI BAWAH
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { VoucherBanner(lightOrangeColor); Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text("Sedang Populer", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                if (isLoading && menuList.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) }
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(displayList) { food -> FoodCardVertical(food = food, onClick = { onFoodClick(food.id) }) }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text("Sedang Promo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                if (!isLoading) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(displayList.reversed()) { food -> FoodCardVertical(food = food, onClick = { onFoodClick(food.id) }) }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

// --- SUB COMPONENTS ---

@Composable
fun HomeHeader(grayColor: Color) {
    // AMBIL USER DINAMIS DI HOME JUGA
    val user = FirebaseAuth.getInstance().currentUser
    val name = user?.displayName ?: "User EatsTedi"
    val photoUrl = user?.photoUrl?.toString() ?: "https://ui-avatars.com/api/?name=$name&background=FF8C00&color=fff"

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.size(50.dp).clip(CircleShape).border(1.dp, Color.Gray, CircleShape), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(12.dp))
            Column { Text("Selamat datang kembali!", fontSize = 12.sp, color = grayColor); Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
        Box(modifier = Modifier.size(40.dp).border(1.dp, Color.LightGray, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Notifications, null, tint = Color.Black) }
    }
}

@Composable
fun CategorySection(lightOrange: Color, onCategoryClick: (String) -> Unit) {
    // UPDATE: MENAMBAHKAN SNACK & MEMBUATNYA SCROLLABLE (LazyRow)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp), // Jarak antar item
        modifier = Modifier.fillMaxWidth()
    ) {
        item { CategoryItem(Icons.Default.Percent, "Promo", Color(0xFFE8F5E9), Color(0xFF2E7D32)) { /* Promo */ } }
        item { CategoryItem(Icons.Default.LunchDining, "Makanan", lightOrange, OrangePrimary) { onCategoryClick("Makanan") } }
        item { CategoryItem(Icons.Default.LocalDrink, "Minuman", Color(0xFFE3F2FD), Color(0xFF1565C0)) { onCategoryClick("Minuman") } }
        item { CategoryItem(Icons.Default.Cookie, "Snack", Color(0xFFFFF3E0), Color(0xFFEF6C00)) { onCategoryClick("Snack") } } // SNACK ADDED
        item { CategoryItem(Icons.Default.Icecream, "Es Krim", Color(0xFFFFEBEE), Color(0xFFC62828)) { onCategoryClick("Es Krim") } }
    }
}

// ... Sisanya (DineIn, Voucher, FoodCard, CategoryItem) SAMA SEPERTI KODE SEBELUMNYA ...
// Pastikan CategoryItem tidak dihapus
@Composable
fun CategoryItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, bgColor: Color, iconColor: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(65.dp).background(bgColor, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = iconColor, modifier = Modifier.size(32.dp)) }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DineInTakeAwayToggle(isDineIn: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(50.dp).background(Color(0xFFF5F5F5), RoundedCornerShape(50.dp)).padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (isDineIn) Color.White else Color.Transparent, RoundedCornerShape(50.dp)).clickable { onToggle(true) }, contentAlignment = Alignment.Center) { Text("Dine In", fontWeight = if (isDineIn) FontWeight.Bold else FontWeight.Normal) }
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (!isDineIn) Color.White else Color.Transparent, RoundedCornerShape(50.dp)).clickable { onToggle(false) }, contentAlignment = Alignment.Center) { Text("Take Away", fontWeight = if (!isDineIn) FontWeight.Bold else FontWeight.Normal) }
    }
}

@Composable
fun VoucherBanner(lightOrange: Color) {
    Card(colors = CardDefaults.cardColors(containerColor = lightOrange), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text("Voucher Khusus", fontWeight = FontWeight.Bold, fontSize = 14.sp); Text("Dapatkan diskon!", fontSize = 11.sp, color = Color.Gray); Spacer(modifier = Modifier.height(8.dp)); Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp), modifier = Modifier.height(32.dp)) { Text("Pakai", fontSize = 12.sp) } }
            Icon(Icons.Default.ConfirmationNumber, null, tint = OrangePrimary, modifier = Modifier.size(60.dp).graphicsLayer(rotationZ = -15f))
        }
    }
}

@Composable
fun FoodCardVertical(food: FoodItem, onClick: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), shape = RoundedCornerShape(16.dp), modifier = Modifier.width(160.dp).clickable { onClick() }) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(contentAlignment = Alignment.TopEnd) {
                AsyncImage(model = food.imageUrl, contentDescription = null, modifier = Modifier.size(136.dp).clip(CircleShape).align(Alignment.Center), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.align(Alignment.BottomStart).background(Color.Black.copy(0.6f), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp)) {
                    Text("Stok: ${food.stock}", color = Color.White, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(food.name, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Rp${food.price}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
        }
    }
}