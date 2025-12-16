package com.example.eatstedinow.model

val dummyFoods = listOf(
    // Urutan Parameter: (id, name, description, price, imageUrl, rating, ratingCount, category, stock)

    // ===== Snack =====
    FoodItem("S1", "Cireng", "Cireng renyah isi bumbu.", 2500, "https://via.placeholder.com/150", 0.0, 0, "Snack", 50),
    FoodItem("S2", "Tahu Bakso", "Tahu isi bakso sapi.", 3000, "https://via.placeholder.com/150", 0.0, 0, "Snack", 40),
    FoodItem("S3", "Pisang Goreng", "Pisang goreng manis.", 4000, "https://via.placeholder.com/150", 0.0, 0, "Snack", 30),
    FoodItem("S4", "Risoles", "Risoles mayo ayam.", 3500, "https://via.placeholder.com/150", 0.0, 0, "Snack", 20),
    FoodItem("S5", "Bakwan", "Bakwan sayur crispy.", 2000, "https://via.placeholder.com/150", 0.0, 0, "Snack", 50),

    // ===== Minuman =====
    FoodItem("M1", "Es Teh Manis", "Teh manis segar.", 3000, "https://via.placeholder.com/150", 0.0, 0, "Minuman", 100),
    FoodItem("M2", "Es Jeruk", "Jeruk peras asli.", 4000, "https://via.placeholder.com/150", 0.0, 0, "Minuman", 80),
    FoodItem("M3", "Kopi Hitam", "Kopi panas nikmat.", 5000, "https://via.placeholder.com/150", 0.0, 0, "Minuman", 50),
    FoodItem("M4", "Teh Tarik", "Teh susu creamy.", 6000, "https://via.placeholder.com/150", 0.0, 0, "Minuman", 40),
    FoodItem("M5", "Susu Coklat", "Susu coklat dingin.", 5000, "https://via.placeholder.com/150", 0.0, 0, "Minuman", 60),

    // ===== Makanan =====
    FoodItem("F1", "Nasi Goreng", "Nasi goreng spesial telur.", 12000, "https://via.placeholder.com/150", 0.0, 0, "Makanan", 25),
    FoodItem("F2", "Mie Goreng", "Mie goreng pedas.", 10000, "https://via.placeholder.com/150", 0.0, 0, "Makanan", 25),
    FoodItem("F3", "Ayam Geprek", "Ayam crispy sambal.", 15000, "https://via.placeholder.com/150", 0.0, 0, "Makanan", 30),
    FoodItem("F4", "Seblak", "Seblak kerupuk pedas.", 12000, "https://via.placeholder.com/150", 0.0, 0, "Makanan", 20),
    FoodItem("F5", "Bakso", "Bakso kuah gurih.", 13000, "https://via.placeholder.com/150", 0.0, 0, "Makanan", 35),

    // ===== Es Krim =====
    FoodItem("E1", "Es Krim Coklat", "Es krim coklat manis.", 5000, "https://via.placeholder.com/150", 0.0, 0, "Es Krim", 15),
    FoodItem("E2", "Es Krim Vanila", "Vanila lembut.", 5000, "https://via.placeholder.com/150", 0.0, 0, "Es Krim", 15),
    FoodItem("E3", "Es Krim Stroberi", "Segar rasa stroberi.", 5500, "https://via.placeholder.com/150", 0.0, 0, "Es Krim", 15),
    FoodItem("E4", "Es Krim Matcha", "Matcha creamy.", 6000, "https://via.placeholder.com/150", 0.0, 0, "Es Krim", 10),
    FoodItem("E5", "Es Krim Cookies", "Cookies & cream.", 6000, "https://via.placeholder.com/150", 0.0, 0, "Es Krim", 10)
)