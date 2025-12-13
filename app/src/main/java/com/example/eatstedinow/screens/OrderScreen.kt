package com.example.eatstedinow.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(onBack: () -> Unit, onProcess: () -> Unit) {
    var quantity by remember { mutableIntStateOf(1) }
    val pricePerItem = 3000
    val tax = 500
    val total = (pricePerItem * quantity) + tax

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order#AQ2140") },
                navigationIcon = {
                    IconButton(onClick = {
                        println("DEBUG: Back button in OrderScreen clicked")
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    println("DEBUG: Proses Order button clicked")
                    onProcess()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8C00)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Proses Order - Rp $total")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Info Meja
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Meja 05", fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { println("DEBUG: Ganti Meja clicked") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008000)),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Ganti Meja", fontSize = 10.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Pesanan", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Item Pesanan
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Gray, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Cireng Isi", fontWeight = FontWeight.Bold)
                        Text("Abon/Ayam", fontSize = 12.sp, color = Color.Gray)
                        Text("Rp $pricePerItem", fontWeight = FontWeight.Bold)
                    }
                    // Counter
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Text("-")
                        }
                        Text("$quantity")
                        IconButton(onClick = { quantity++ }) {
                            Text("+")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Rincian
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Subtotal")
                Text("Rp ${pricePerItem * quantity}")
            }
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pajak")
                Text("Rp $tax")
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text("Rp $total", fontWeight = FontWeight.Bold)
            }
        }
    }
}