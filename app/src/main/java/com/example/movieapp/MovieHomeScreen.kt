package com.example.movieapp

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieHomeScreen(navController: NavController, movieViewModel: MovieViewModel = viewModel()) {
    val context = LocalContext.current
    val visibleMovies by movieViewModel.visibleMovies.collectAsState()
    val favorites by movieViewModel.favorites.collectAsState()
    val isRefreshing = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val isLoadingMore by movieViewModel.isLoadingMore.collectAsState()
    val noMoreItems by movieViewModel.noMoreItems.collectAsState()
    val loadError by movieViewModel.loadError.collectAsState()
    val query by movieViewModel.searchQuery.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current



    LaunchedEffect(noMoreItems) {
        if (noMoreItems) {
            Toast.makeText(context, "No more items to load", Toast.LENGTH_SHORT).show()
            movieViewModel.resetNoMoreItems()
        }
    }

    LaunchedEffect(loadError) {
        loadError?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            movieViewModel.clearLoadError()
        }
    }


    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF8B5CF6), Color(0xFFEC4899)
                            )
                        )
                    )
            ) {
                TopAppBar(title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Movies And More",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }, actions = {
                    IconButton(onClick = { navController.navigate("favorites") }) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Favorites",
                            tint = Color.White
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, titleContentColor = Color.White
                )
                )
            }
        }, containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFFEC4899), Color(0xFF8B5CF6))
                    )
                )
                .padding(8.dp)
        ) {
            AnimatedVisibility(visible = true, enter = fadeIn()) {
                OutlinedTextField(value = query,
                    onValueChange = { movieViewModel.searchQuery.value = it },
                    placeholder = { Text("Search movies...") },
                    singleLine = true,
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = {
                                movieViewModel.searchQuery.value = ""
                                keyboardController?.hide()
                            }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear Search",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.medium
                        ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.6f)
                    )
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing.value), onRefresh = {
                    isRefreshing.value = true
                    movieViewModel.fetchMovies()
                    isRefreshing.value = false
                }, modifier = Modifier.weight(1f)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(visibleMovies) { _, movie ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    coroutineScope.launch {
                                        val videoId = movieViewModel.getVideoKey(movie.id)
                                        if (videoId.isNotBlank()) {
                                            navController.navigate("trailer/$videoId")
                                        } else {
                                            Toast
                                                .makeText(
                                                    context,
                                                    "Unable to load video. Check your connection.",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                        }
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
                                    Button(onClick = {
                                        coroutineScope.launch {
                                            val videoId = movieViewModel.getVideoKey(movie.id)
                                            if (videoId.isNotBlank()) {
                                                navController.navigate("trailer/$videoId")
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Trailer not available for this movie",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
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
                    }

                    if (visibleMovies.size >= 10) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoadingMore) {
                                    CircularProgressIndicator()
                                } else {
                                    Button(onClick = { movieViewModel.loadNextPage(context) }) {
                                        Text("Load More")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
