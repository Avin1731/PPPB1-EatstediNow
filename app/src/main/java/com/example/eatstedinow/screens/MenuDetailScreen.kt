package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.eatstedinow.model.FoodItem

@Composable
fun MenuDetailScreen(food: FoodItem, onBack: () -> Unit, onAddToCart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
            AsyncImage(
                model = food.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Tombol Back dengan background transparan agar terlihat
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
                    .align(Alignment.TopStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(food.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Rp ${food.price}", fontSize = 20.sp, color = Color(0xFFFF8C00), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Rating: ${food.rating} / 5.0", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(food.description, lineHeight = 22.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Tambah Pesanan")
            }
        }
    }
}