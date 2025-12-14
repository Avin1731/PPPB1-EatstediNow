package com.example.eatstedinow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.eatstedinow.model.dummyFoods
import com.example.eatstedinow.screens.*
import com.example.eatstedinow.ui.theme.EatsTediNowTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Pasang Splash Screen API sebelum super.onCreate
        // Ini akan otomatis menghandle transisi dari Theme.App.Starting ke Theme aplikasi
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            EatsTediNowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()

    // State untuk menyimpan halaman awal
    var startDestination by remember { mutableStateOf<String?>(null) }

    // Cek status login
    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            startDestination = Routes.HOME
        } else {
            startDestination = Routes.ONBOARDING
        }
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        NavHost(navController = navController, startDestination = startDestination!!) {

            // 1. ONBOARDING
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onLoginClick = { navController.navigate(Routes.LOGIN) },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) }
                )
            }

            // 2. LOGIN
            composable(Routes.LOGIN) {
                LoginScreen(
                    onBackClick = { navController.popBackStack() },
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) }
                )
            }

            // 3. REGISTER
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onBackClick = { navController.popBackStack() },
                    onRegisterSuccess = { navController.navigate(Routes.LOGIN) }
                )
            }

            // 4. HOME SCREEN (BAGIAN YANG ERROR TADI)
            composable(Routes.HOME) {
                HomeScreen(
                    onFoodClick = { foodId ->
                        navController.navigate("menu_detail/$foodId")
                    },
                    onCartClick = {
                        navController.navigate(Routes.ORDER)
                    },
                    onProfileClick = {
                        navController.navigate(Routes.PROFILE)
                    },
                    // --- INI YANG KURANG TADI ---
                    onMenuClick = {
                        navController.navigate(Routes.MENU)
                    }
                )
            }

            // 5. MENU SCREEN (HALAMAN BARU)
            composable(Routes.MENU) {
                MenuScreen(
                    onFoodClick = { foodId ->
                        navController.navigate("menu_detail/$foodId")
                    },
                    onCartClick = { navController.navigate(Routes.ORDER) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onHomeClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    }
                )
            }

            // 6. MENU DETAIL
            composable(
                route = Routes.MENU_DETAIL,
                arguments = listOf(navArgument("foodId") { type = NavType.IntType })
            ) { backStackEntry ->
                val foodId = backStackEntry.arguments?.getInt("foodId")
                val food = dummyFoods.find { it.id == foodId }

                if (food != null) {
                    MenuDetailScreen(
                        food = food,
                        onBack = { navController.popBackStack() }, // Fungsi tombol Back
                        onAddToCart = { 
                            // Pindah ke halaman Order/Keranjang
                            navController.navigate(Routes.ORDER) 
                        }
                    )
                }
            }

            // 7. ORDER SCREEN
            composable(Routes.ORDER) {
                OrderScreen(
                    onBack = { navController.popBackStack() },
                    onProcess = { /* Nanti lanjut ke pembayaran */ },
                    // Tambahan Navigasi Bottom Bar:
                    onHomeClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onMenuClick = {
                        navController.navigate(Routes.MENU)
                    },
                    onProfileClick = {
                        navController.navigate(Routes.PROFILE)
                    }
                )
            }

            // 8. PROFILE SCREEN
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = {
                        auth.signOut()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    // Navigasi Bottom Bar
                    onHomeClick = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onMenuClick = {
                        navController.navigate(Routes.MENU)
                    },
                    onCartClick = {
                        navController.navigate(Routes.ORDER)
                    }
                )
            }
        }
    }
}