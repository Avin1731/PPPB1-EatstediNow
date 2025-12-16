package com.example.eatstedinow.model

data class FoodItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val rating: Double,
    val ratingCount: Int = 0, // Field Baru: Jumlah Penilai
    val category: String = "Makanan",
    val stock: Int = 10
)