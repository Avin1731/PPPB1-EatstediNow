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
import com.example.eatstedinow.screens.*
import com.example.eatstedinow.ui.theme.EatsTediNowTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            EatsTediNowTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
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
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        startDestination = if (auth.currentUser != null) Routes.HOME else Routes.ONBOARDING
    }

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
    } else {
        NavHost(navController = navController, startDestination = startDestination!!) {
            // AUTH
            composable(Routes.ONBOARDING) { OnboardingScreen({ navController.navigate(Routes.LOGIN) }, { navController.navigate(Routes.REGISTER) }) }
            composable(Routes.LOGIN) { LoginScreen({ navController.popBackStack() }, { navController.navigate(Routes.HOME) { popUpTo(Routes.ONBOARDING) { inclusive = true } } }, { navController.navigate(Routes.REGISTER) }) }
            composable(Routes.REGISTER) { RegisterScreen({ navController.popBackStack() }, { navController.navigate(Routes.LOGIN) }) }

            // UTAMA
            composable(Routes.HOME) {
                HomeScreen(
                    onFoodClick = { id -> navController.navigate("menu_detail/$id") },
                    onCartClick = { navController.navigate(Routes.ORDER) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onMenuClick = { category -> navController.navigate("menu?category=$category") }
                )
            }

            // MENU
            composable(
                route = Routes.MENU,
                arguments = listOf(navArgument("category") { type = NavType.StringType; defaultValue = "Semua"; nullable = true })
            ) { entry ->
                MenuScreen(
                    initialCategory = entry.arguments?.getString("category") ?: "Semua",
                    onFoodClick = { id -> navController.navigate("menu_detail/$id") },
                    onCartClick = { navController.navigate(Routes.ORDER) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) },
                    onHomeClick = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } }
                )
            }

            // MENU DETAIL
            composable(
                route = Routes.MENU_DETAIL,
                arguments = listOf(navArgument("foodId") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("foodId")
                if (id != null) {
                    MenuDetailScreen(foodId = id, onBack = { navController.popBackStack() }, onAddToCart = { navController.navigate(Routes.ORDER) })
                }
            }

            // ORDER (Menyambung ke History jika sukses)
            composable(Routes.ORDER) {
                OrderScreen(
                    onBack = { navController.popBackStack() },
                    onProcess = { navController.navigate(Routes.HISTORY) }, // SUKSES -> KE HISTORY
                    onHomeClick = { navController.navigate(Routes.HOME) },
                    onMenuClick = { navController.navigate("menu?category=Semua") },
                    onProfileClick = { navController.navigate(Routes.PROFILE) }
                )
            }

            // PROFILE (Menyambung ke History & Admin)
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = { auth.signOut(); navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } } },
                    onHomeClick = { navController.navigate(Routes.HOME) },
                    onMenuClick = { navController.navigate("menu?category=Semua") },
                    onCartClick = { navController.navigate(Routes.ORDER) },
                    onAdminClick = { navController.navigate(Routes.ADMIN_HOME) },
                    onHistoryClick = { navController.navigate(Routes.HISTORY) } // KLIK HISTORY
                )
            }

            // LAYAR HISTORY BARU
            composable(Routes.HISTORY) {
                HistoryScreen(onBack = { navController.popBackStack() })
            }

            // ADMIN
            composable(Routes.ADMIN_HOME) {
                AdminMenuListScreen(
                    onAddClick = { navController.navigate("admin_form") },
                    onEditClick = { id -> navController.navigate("admin_form?foodId=$id") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.ADMIN_FORM,
                arguments = listOf(navArgument("foodId") { type = NavType.StringType; nullable = true })
            ) { entry ->
                AdminFormScreen(foodId = entry.arguments?.getString("foodId"), onBack = { navController.popBackStack() })
            }
        }
    }
}