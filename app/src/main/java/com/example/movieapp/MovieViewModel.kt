package com.example.movieapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel(application: Application) : AndroidViewModel(application) {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    val searchQuery = MutableStateFlow("")

    private val _visibleMovies = MutableStateFlow<List<Movie>>(emptyList())
    val visibleMovies: StateFlow<List<Movie>> = _visibleMovies

    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    val favorites: StateFlow<Set<Int>> = _favorites

    private val apiKey = "f96958f3c5cc57e2d4e19ee574667d96"
    private val sharedPrefs = application.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private var totalLoaded = 10

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore

    val _noMoreItems = MutableStateFlow(false)
    val noMoreItems: StateFlow<Boolean> = _noMoreItems

    private var currentPage = 1
    private var totalPages = 1


    init {
        fetchMovies()
        loadFavorites()
        viewModelScope.launch {
            searchQuery.collect {
                filterVisible()
            }
        }
    }

    fun fetchMovies() {
        viewModelScope.launch {
            try {
                currentPage = 1
                _isLoadingMore.value = true
                val response = ApiClient.movieService.getPopularMovies(apiKey, currentPage)
                _movies.value = response.results
                _visibleMovies.value = response.results
                totalPages = response.total_pages
            } catch (e: Exception) {
                // handle error
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            if (_isLoadingMore.value || currentPage >= totalPages) return@launch

            _isLoadingMore.value = true
            currentPage += 1

            try {
                val response = ApiClient.movieService.getPopularMovies(apiKey, currentPage)
                _movies.value = _movies.value + response.results
                _visibleMovies.value = _visibleMovies.value + response.results
            } catch (e: Exception) {
                currentPage -= 1 // rollback page if failed
            } finally {
                _isLoadingMore.value = false
            }
        }
    }


    fun filterVisible() {
        _visibleMovies.value = _movies.value
            .filter { it.title.contains(searchQuery.value, ignoreCase = true) }
            .take(totalLoaded)
    }

    fun loadMore() {
        viewModelScope.launch {
            _isLoadingMore.value = true
            delay(800)
            val current = _visibleMovies.value.size
            val next = _movies.value.drop(current).take(10)

            if (next.isEmpty()) {
                _noMoreItems.value = true
            } else {
                _visibleMovies.value = _visibleMovies.value + next
            }

            _isLoadingMore.value = false
        }
    }

    fun resetNoMoreItems() {
        _noMoreItems.value = false
    }


    fun toggleFavorite(movieId: Int) {
        val updated = _favorites.value.toMutableSet()
        if (updated.contains(movieId)) updated.remove(movieId) else updated.add(movieId)
        _favorites.value = updated
        sharedPrefs.edit().putStringSet("favorites", updated.map { it.toString() }.toSet()).apply()
    }

    private fun loadFavorites() {
        val stored = sharedPrefs.getStringSet("favorites", emptySet()) ?: emptySet()
        _favorites.value = stored.mapNotNull { it.toIntOrNull() }.toSet()
    }

    suspend fun getTrailerUrlFor(movieId: Int): String {
        return try {
            val response: VideoResponse = ApiClient.movieService.getMovieVideos(movieId, apiKey)
            val video = response.results.firstOrNull {
                it.site.equals("YouTube", ignoreCase = true) &&
                        it.type.equals("Trailer", ignoreCase = true)
            }
            "https://www.youtube.com/embed/${video?.key ?: ""}"
        } catch (e: Exception) {
            ""
        }
    }

    suspend fun getVideoKey(
        movieId: Int
    ): String {
        return try {
            val response: VideoResponse = ApiClient.movieService.getMovieVideos(movieId, apiKey)
            val video = response.results.firstOrNull {
                it.site.equals("YouTube", ignoreCase = true) &&
                        it.type.equals("Trailer", ignoreCase = true)
            }
            video?.key ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
