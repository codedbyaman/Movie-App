package com.example.movietrailerapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movietrailerapp.model.Movie
import com.example.movietrailerapp.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovieViewModel : ViewModel() {
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val apiKey = "YOUR API"

    init {
        fetchMovies()
    }

    fun fetchMovies() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = ApiClient.movieService.getPopularMovies(apiKey)
                _movies.value = response.results
            } catch (e: Exception) {
                _error.value = "Failed to load movies. Please try again."
            } finally {
                _loading.value = false
            }
        }
    }
}
