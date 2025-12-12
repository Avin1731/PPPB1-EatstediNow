package com.example.eatstedinow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eatstedinow.model.dummyFoods
import com.example.eatstedinow.screens.*
import com.example.eatstedinow.ui.theme.EatsTediNowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EatsTediNowTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        // 1. LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            })
        }

        // 2. HOME
        composable(Routes.HOME) {
            HomeScreen(
                onFoodClick = { foodId ->
                    navController.navigate("menu_detail/$foodId")
                },
                onCartClick = { navController.navigate(Routes.ORDER) },
                onProfileClick = { navController.navigate(Routes.PROFILE) }
            )
        }

        // 3. DETAIL MENU (Fixed Null Safety)
        composable(
            route = Routes.MENU_DETAIL,
            arguments = listOf(navArgument("foodId") { type = NavType.IntType })
        ) { backStackEntry ->
            val foodId = backStackEntry.arguments?.getInt("foodId")
            val food = dummyFoods.find { it.id == foodId }

            if (food != null) {
                MenuDetailScreen(
                    food = food,
                    onBack = { navController.popBackStack() },
                    onAddToCart = { navController.navigate(Routes.ORDER) }
                )
            } else {
                // Fallback jika item tidak ditemukan (mencegah layar putih)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Menu tidak ditemukan")
                }
            }
        }

        // 4. ORDER
        composable(Routes.ORDER) {
            OrderScreen(
                onBack = { navController.popBackStack() },
                onProcess = { /* Logic Bayar */ }
            )
        }

        // 5. PROFILE
        composable(Routes.PROFILE) {
            ProfileScreen(onLogout = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) { inclusive = true }
                }
            })
        }
    }
}