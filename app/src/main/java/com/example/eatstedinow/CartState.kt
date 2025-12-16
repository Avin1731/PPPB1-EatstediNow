package com.example.eatstedinow

import androidx.compose.runtime.mutableStateListOf
import com.example.eatstedinow.model.FoodItem

// Model khusus Cart
data class CartItem(
    val id: String,
    val name: String,
    val price: Int,
    val imageUrl: String,
    var quantity: Int
)

// Global State untuk Keranjang (Agar bisa diakses dari Detail & Order)
object CartState {
    private val _items = mutableStateListOf<CartItem>()
    val items: List<CartItem> get() = _items

    fun addItem(food: FoodItem) {
        val existingItem = _items.find { it.id == food.id }
        if (existingItem != null) {
            val index = _items.indexOf(existingItem)
            _items[index] = existingItem.copy(quantity = existingItem.quantity + 1)
        } else {
            _items.add(
                CartItem(
                    id = food.id,
                    name = food.name,
                    price = food.price,
                    imageUrl = food.imageUrl,
                    quantity = 1
                )
            )
        }
    }

    fun removeItem(item: CartItem) {
        val existingItem = _items.find { it.id == item.id }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                val index = _items.indexOf(existingItem)
                _items[index] = existingItem.copy(quantity = existingItem.quantity - 1)
            } else {
                _items.remove(existingItem)
            }
        }
    }

    fun addItemCart(item: CartItem) {
        val index = _items.indexOf(item)
        if (index != -1) {
            _items[index] = item.copy(quantity = item.quantity + 1)
        }
    }

    fun clear() {
        _items.clear()
    }
}