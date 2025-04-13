package com.example.movieapp

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") {
                        SplashScreen {
                            if (isConnected()) {
                                navController.navigate("home") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            } else {
                                navController.navigate("offline") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }
                    }

                    composable("offline") {
                        NoInternetScreen {
                            if (isConnected()) {
                                navController.navigate("splash") {
                                    popUpTo("offline") { inclusive = true }
                                }
                            }
                        }
                    }

                    composable("home") {
                        MovieHomeScreen(navController)
                    }

                    composable("favorites") {
                        FavoriteMoviesScreen(navController)
                    }

                    composable(
                        "trailer/{videoId}",
                        arguments = listOf(navArgument("videoId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                        YouTubePlayerComposable(
                            activity = this@MainActivity,
                            videoId = videoId
                        )
                    }
                }
            }
        }
    }

    private fun isConnected(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
