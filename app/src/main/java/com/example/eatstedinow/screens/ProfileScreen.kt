package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onHomeClick: () -> Unit,
    onMenuClick: () -> Unit,
    onCartClick: () -> Unit,
    onAdminClick: () -> Unit,
    onHistoryClick: () -> Unit // Callback History
) {
    val user = FirebaseAuth.getInstance().currentUser
    val userName = user?.displayName ?: "Pengguna"
    val photoUrl = user?.photoUrl?.toString() ?: "https://ui-avatars.com/api/?name=${userName}&background=FF8C00&color=fff"

    // STATE NOTIFIKASI
    var pendingRatingCount by remember { mutableStateOf(0) }
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            FirebaseFirestore.getInstance().collection("orders")
                .whereEqualTo("userId", uid).whereEqualTo("isRated", false)
                .addSnapshotListener { s, _ -> pendingRatingCount = s?.size() ?: 0 }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 10.dp) {
                NavigationBarItem(icon = { Icon(Icons.Default.Home, "Home") }, label = { Text("Home", fontSize = 10.sp) }, selected = false, onClick = onHomeClick)
                NavigationBarItem(icon = { Icon(Icons.Default.MenuBook, "Menu") }, label = { Text("Menu", fontSize = 10.sp) }, selected = false, onClick = onMenuClick)
                NavigationBarItem(icon = { Icon(Icons.Default.ShoppingCart, "Cart") }, label = { Text("Order", fontSize = 10.sp) }, selected = false, onClick = onCartClick)
                NavigationBarItem(
                    icon = {
                        Box {
                            Icon(Icons.Default.Person, "Profile")
                            if(pendingRatingCount > 0) Box(Modifier.size(8.dp).background(Color.Red, CircleShape).align(Alignment.TopEnd))
                        }
                    },
                    label = { Text("Profile", fontSize = 10.sp) },
                    selected = true,
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = OrangePrimary, indicatorColor = Color(0xFFFFF3E0)),
                    onClick = {}
                )
            }
        }
    ) { p ->
        Column(Modifier.fillMaxSize().padding(p).padding(horizontal = 24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(32.dp))
            Text("My Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(32.dp))
            Box(Modifier.size(120.dp).border(3.dp, OrangePrimary, CircleShape).padding(4.dp)) {
                AsyncImage(model = photoUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(CircleShape), contentScale = ContentScale.Crop)
            }
            Spacer(Modifier.height(16.dp))
            Text(userName, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(40.dp))

            SectionTitle("Account")
            ProfileMenuItem("Informasi Pribadi", onClick = {})
            ProfileMenuItem("Riwayat Pembelian", onClick = onHistoryClick, hasNotification = pendingRatingCount > 0)

            Spacer(Modifier.height(24.dp))
            SectionTitle("System")
            TextButton(onClick = onLogout, contentPadding = PaddingValues(0.dp), modifier = Modifier.align(Alignment.Start)) { Text("Log Out", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(40.dp))
            Button(onClick = onAdminClick, colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray), modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.AdminPanelSettings, null, tint = Color.White); Spacer(Modifier.width(8.dp)); Text("Mode Admin")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(text = title, fontSize = 14.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
}

@Composable
fun ProfileMenuItem(text: String, onClick: () -> Unit, hasNotification: Boolean = false) {
    Row(Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text, fontSize = 16.sp)
            if (hasNotification) { Spacer(Modifier.width(8.dp)); Box(Modifier.size(8.dp).background(Color.Red, CircleShape)) }
        }
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Black)
    }
}