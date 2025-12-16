package com.example.eatstedinow.model

import com.google.firebase.Timestamp

data class OrderHistory(
    val id: String = "",
    val userId: String = "",
    val items: List<OrderItem> = emptyList(),
    val total: Int = 0,
    val date: Timestamp = Timestamp.now(),
    val isRated: Boolean = false // Penanda untuk Dot Merah
)

data class OrderItem(
    val foodId: String = "",
    val name: String = "",
    val price: Int = 0,
    val quantity: Int = 0,
    val imageUrl: String = ""
)