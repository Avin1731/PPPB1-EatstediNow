package com.example.eatstedinow.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import com.example.eatstedinow.model.dummyFoods

@Composable
fun HomeScreen(
    onFoodClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { /* Stay on Home */ }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.ShoppingCart, null) },
                    label = { Text("Order") },
                    selected = false,
                    onClick = onCartClick
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = onProfileClick
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).padding(16.dp)
        ) {
            item {
                Text("Halo, Mahasiswa!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Mau makan apa hari ini?", color = Color.Gray)
                Spacer(modifier = Modifier.height(20.dp))
                Text("Menu Kantin", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(10.dp))
            }
            items(dummyFoods) { food ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { onFoodClick(food.id) },
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = food.imageUrl,
                            contentDescription = null,
                            modifier = Modifier.size(70.dp).clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Rp ${food.price}", color = Color(0xFF008000), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}