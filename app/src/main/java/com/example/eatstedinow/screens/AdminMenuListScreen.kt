package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.eatstedinow.model.FoodItem
import com.example.eatstedinow.model.dummyFoods
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuListScreen(
    onAddClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var menuList by remember { mutableStateOf<List<FoodItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    // Load Data Realtime
    LaunchedEffect(Unit) {
        db.collection("menus").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                menuList = snapshot.documents.mapNotNull { doc ->
                    try {
                        FoodItem(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            description = "",
                            price = doc.getLong("price")?.toInt() ?: 0,
                            imageUrl = "",
                            rating = doc.getDouble("rating") ?: 0.0,
                            ratingCount = doc.getLong("ratingCount")?.toInt() ?: 0,
                            category = doc.getString("category") ?: "",
                            stock = doc.getLong("stock")?.toInt() ?: 0
                        )
                    } catch (e: Exception) { null }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Stok & Menu") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    // 1. TOMBOL UPLOAD DUMMY (AWAN)
                    IconButton(onClick = {
                        isLoading = true
                        val batch = db.batch()

                        // Hapus data lama dulu (opsional, biar bersih)
                        // Tapi di sini kita langsung timpa/tambah baru

                        dummyFoods.forEach { food ->
                            val docRef = db.collection("menus").document() // ID Baru Otomatis
                            val data = hashMapOf(
                                "name" to food.name,
                                "description" to food.description,
                                "price" to food.price,
                                "imageUrl" to food.imageUrl,
                                "category" to food.category,
                                "stock" to food.stock,
                                // UPDATE PENTING: Masukkan Rating default
                                "rating" to 0.0,
                                "ratingCount" to 0
                            )
                            batch.set(docRef, data)
                        }

                        batch.commit()
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Sukses Upload ${dummyFoods.size} Data Baru!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }) {
                        Icon(Icons.Default.CloudUpload, "Upload Dummy", tint = OrangePrimary)
                    }

                    // 2. TOMBOL HAPUS SEMUA (RESET DATABASE)
                    IconButton(onClick = {
                        isLoading = true
                        db.collection("menus").get().addOnSuccessListener { snapshot ->
                            val batch = db.batch()
                            for (document in snapshot.documents) {
                                batch.delete(document.reference)
                            }
                            batch.commit().addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Database Bersih!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }) {
                        Icon(Icons.Default.DeleteForever, "Hapus Semua", tint = Color.Red)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick, containerColor = OrangePrimary) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }
    ) { p ->
        Box(modifier = Modifier.padding(p).fillMaxSize()) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                if (menuList.isEmpty() && !isLoading) {
                    item {
                        Text("Data Kosong. Klik ikon Awan untuk isi data.", color = Color.Gray, modifier = Modifier.padding(16.dp))
                    }
                }

                items(menuList) { food ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(food.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("${food.category} • Stok: ${food.stock}", color = if (food.stock < 5) Color.Red else Color.Gray)
                                Text("⭐ ${food.rating} (${food.ratingCount})", fontSize = 12.sp, color = Color.Gray)
                                Text("Rp${food.price}", color = OrangePrimary, fontWeight = FontWeight.Bold)
                            }
                            IconButton(onClick = { onEditClick(food.id) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                            }
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = OrangePrimary
                )
            }
        }
    }
}