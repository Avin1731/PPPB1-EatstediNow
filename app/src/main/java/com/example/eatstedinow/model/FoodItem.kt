package com.example.eatstedinow.model

data class FoodItem(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val rating: Double
)