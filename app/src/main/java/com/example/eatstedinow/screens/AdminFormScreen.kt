package com.example.eatstedinow.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.eatstedinow.ui.theme.OrangePrimary
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFormScreen(
    foodId: String?, // Null = Tambah Baru, Ada Isi = Edit
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val isEditMode = foodId != null

    // State Form
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Makanan") }

    // State Hidden (Untuk menyimpan data rating lama saat edit)
    var currentRating by remember { mutableStateOf(0.0) }
    var currentRatingCount by remember { mutableStateOf(0) }

    var isLoading by remember { mutableStateOf(false) }
    val categories = listOf("Makanan", "Minuman", "Es Krim", "Snack")

    // Load Data jika Edit Mode
    LaunchedEffect(foodId) {
        if (foodId != null) {
            db.collection("menus").document(foodId).get().addOnSuccessListener { doc ->
                if (doc.exists()) {
                    name = doc.getString("name") ?: ""
                    price = (doc.getLong("price") ?: 0).toString()
                    stock = (doc.getLong("stock") ?: 0).toString()
                    description = doc.getString("description") ?: ""
                    imageUrl = doc.getString("imageUrl") ?: ""
                    category = doc.getString("category") ?: "Makanan"

                    // Simpan rating lama agar tidak kereset jadi 0 saat update
                    currentRating = doc.getDouble("rating") ?: 0.0
                    currentRatingCount = (doc.getLong("ratingCount") ?: 0).toInt()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Menu" else "Tambah Menu") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = {
                            isLoading = true
                            db.collection("menus").document(foodId!!).delete()
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Dihapus!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                        }) {
                            Icon(Icons.Default.Delete, "Hapus", tint = Color.Red)
                        }
                    }
                }
            )
        }
    ) { p ->
        Column(modifier = Modifier.padding(p).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Makanan") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Harga") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Stok") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("Kategori:", fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                categories.forEach { cat ->
                    FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = OrangePrimary, selectedLabelColor = Color.White))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Deskripsi") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("URL Gambar") }, placeholder = { Text("https://...") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (name.isNotEmpty() && price.isNotEmpty()) {
                        isLoading = true
                        val data = hashMapOf(
                            "name" to name,
                            "price" to (price.toIntOrNull() ?: 0),
                            "stock" to (stock.toIntOrNull() ?: 0),
                            "description" to description,
                            "category" to category,
                            "imageUrl" to imageUrl.ifEmpty { "https://via.placeholder.com/150" },

                            // Gunakan nilai lama jika edit, atau 0 jika baru
                            "rating" to if (isEditMode) currentRating else 0.0,
                            "ratingCount" to if (isEditMode) currentRatingCount else 0
                        )

                        if (isEditMode) {
                            db.collection("menus").document(foodId!!).update(data as Map<String, Any>)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Update Berhasil!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                        } else {
                            db.collection("menus").add(data)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Tersimpan!", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                        }
                    } else { Toast.makeText(context, "Lengkapi Data!", Toast.LENGTH_SHORT).show() }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White) else Text(if (isEditMode) "UPDATE DATA" else "SIMPAN DATA")
            }
        }
    }
}