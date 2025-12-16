package com.example.eatstedinow.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.model.dummyFoods
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.firestore.FirebaseFirestore

private val MenuBgColor = Color(0xFFFAFAFA)
private val SelectedTabColor = Color.Black
private val UnselectedTabColor = Color.Gray

@Composable
fun MenuScreen(
    initialCategory: String = "Semua",
    onFoodClick: (String) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    var isDineIn by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    // UPDATE: DAFTAR KATEGORI LENGKAP
    val categories = listOf("Semua", "Makanan", "Minuman", "Es Krim", "Snack")

    var menuList by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

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

    val sourceData = if (!isLoading && menuList.isEmpty()) dummyFoods else menuList
    val filteredMenu = if (selectedCategory == "Semua") sourceData else sourceData.filter { it.category.equals(selectedCategory, ignoreCase = true) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = false, onClick = onHomeClick)
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = true, colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)), onClick = {})
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Cart") }, label = { Text("Order", fontSize = 10.sp) }, selected = false, onClick = onCartClick)
                NavigationBarItem(icon = { Icon(Icons.Default.Person, "Profile") }, label = { Text("Profile", fontSize = 10.sp) }, selected = false, onClick = onProfileClick)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().background(MenuBgColor).padding(paddingValues).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Menu", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            DineInTakeAwayToggle(isDineIn) { isDineIn = it }
            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.fillMaxWidth()) {
                items(categories) { category ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { selectedCategory = category }) {
                        Text(text = category, fontSize = 16.sp, fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Normal, color = if (selectedCategory == category) SelectedTabColor else UnselectedTabColor)
                        if (selectedCategory == category) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.size(6.dp).background(OrangePrimary, CircleShape))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading && menuList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = OrangePrimary) }
            } else {
                LazyVerticalGrid(columns = GridCells.Fixed(2), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()) {
                    items(filteredMenu) { food -> FoodCardVertical(food = food, onClick = { onFoodClick(food.id) }) }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}