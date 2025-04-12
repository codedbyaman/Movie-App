package com.example.movieapp

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.movieapp.MovieViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieHomeScreen(navController: NavController, movieViewModel: MovieViewModel = viewModel()) {
    val searchQuery by movieViewModel.searchQuery.collectAsState()
    val visibleMovies by movieViewModel.visibleMovies.collectAsState()
    val favorites by movieViewModel.favorites.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                )
            )
            .padding(8.dp)
    ) {
        AnimatedVisibility(visible = true, enter = fadeIn()) {
            Text(
                "🎬 Movies And More",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        AnimatedVisibility(visible = true, enter = fadeIn()) {
            TextField(
                value = searchQuery,
                onValueChange = { movieViewModel.searchQuery.value = it },
                placeholder = { Text("Search movies...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.White)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing),
            onRefresh = {
                isRefreshing = true
                movieViewModel.fetchMovies()
                isRefreshing = false
            },
            modifier = Modifier.weight(1f)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(visibleMovies) { index, movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                coroutineScope.launch {
                                    val videoUrl = movieViewModel.getTrailerUrlFor(movie.id)
                                    navController.navigate("trailer/${Uri.encode(videoUrl)}")
                                }
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C))
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter("https://image.tmdb.org/t/p/w500${movie.poster_path}"),
                                contentDescription = movie.title,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = movie.title,
                                color = Color.White,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = {
                                    coroutineScope.launch {
                                        val videoUrl = movieViewModel.getTrailerUrlFor(movie.id)
                                        navController.navigate("trailer/${Uri.encode(videoUrl)}")
                                    }
                                }) {
                                    Text("Watch", color = Color.White)
                                }
                                IconButton(onClick = {
                                    movieViewModel.toggleFavorite(movie.id)
                                }) {
                                    Icon(
                                        imageVector = if (favorites.contains(movie.id)) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (favorites.contains(movie.id)) Color.Red else Color.White
                                    )
                                }
                            }
                        }
                    }

                    if (index == visibleMovies.lastIndex) {
                        LaunchedEffect(Unit) {
                            movieViewModel.loadMore()
                        }
                    }
                }
            }
        }
    }
}
