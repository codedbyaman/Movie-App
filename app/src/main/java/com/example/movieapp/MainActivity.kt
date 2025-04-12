package com.example.movieapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.movieapp.ui.WebViewPlayer


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "splash") {

                    // 🚀 Splash screen destination
                    composable("splash") {
                        SplashScreen {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }

                    // 🏠 Main movie list screen
                    composable("home") {
                        MovieHomeScreen(navController)
                    }

                    // 🎥 In-app video trailer screen
                    composable(
                        route = "trailer/{url}",
                        arguments = listOf(navArgument("url") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val videoUrl = backStackEntry.arguments?.getString("url") ?: ""
                        WebViewPlayer(videoUrl)
                    }
                }
            }
        }
    }
}
