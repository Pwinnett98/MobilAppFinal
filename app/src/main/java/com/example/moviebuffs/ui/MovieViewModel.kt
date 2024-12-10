package com.example.moviebuffs.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebuffs.network.MovieApi
import com.example.moviebuffs.network.Movies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface MovieUiState {
    data class Success(val movies: List<Movies>) : MovieUiState
    object Error : MovieUiState
    object Loading : MovieUiState
}

data class NavigationState(
    val currentMovie: Movies?,
    val isShowingListPage: Boolean = true
)

class MovieViewModel : ViewModel() {
    var MovieUiState: MovieUiState by mutableStateOf(MovieUiState.Loading)
        private set

    private val _navigationState = MutableStateFlow(
        NavigationState(
            currentMovie = null,
            isShowingListPage = true
        )
    )

    val navigationState: StateFlow<NavigationState> = _navigationState


    init {
        getMovies()
    }


    fun getMovies() {
        viewModelScope.launch {
            MovieUiState = try {
                MovieUiState.Success(MovieApi.retrofitService.getMovies())
            } catch (e: IOException) {
                MovieUiState.Error
            }
        }
    }


    fun updateCurrentMovie(currentMovie: Movies) {
        _navigationState.update {
            it.copy(currentMovie = currentMovie)
        }
    }

    fun navigateToListPage() {
        _navigationState.update {
            it.copy(isShowingListPage = true)
        }
    }

    fun navigateToDetailPage() {
        _navigationState.update {
            it.copy(isShowingListPage = false)
        }
    }
}