package com.example.moviebuffs.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebuffs.network.MovieApi
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface MovieUiState {
    data class Success(val movies: String) : MovieUiState
    object Error : MovieUiState
    object Loading : MovieUiState
}

class MovieViewModel : ViewModel() {
    var movieUiState: MovieUiState by mutableStateOf(MovieUiState.Loading)

        private set
    init {
        getMovies()
    }

    fun getMovies() {
        viewModelScope.launch {
            movieUiState = try {
                MovieUiState.Success(MovieApi.retrofitService.getMovies().toString())
            } catch (e: IOException) {
                MovieUiState.Error
            }
        }
    }
}