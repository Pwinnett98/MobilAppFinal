package com.example.moviebuffs.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviebuffs.data.MovieDataSource
import com.example.moviebuffs.network.MovieApi
import com.example.moviebuffs.network.Movies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface MovieUiState {
    data class Success(val movies: List<Movies>) : MovieUiState
    object Error : MovieUiState
    object Loading : MovieUiState
}

class MovieViewModel : ViewModel() {
    var movieUiState: MovieUiState by mutableStateOf(MovieUiState.Loading)

        private set
    init {
        getMovies()
    }

    private val _uiState = MutableStateFlow(
        movieUiState(
            movieList = MovieDataSource.getMovieData(),
            currentMovie = MovieDataSource.getMovieData().getOrElse(0) {
                MovieDataSource.defaultMovie
            }
        )
    )

    val uiState: StateFlow<movieUiState> = _uiState

    fun updateCurrentMovie(selectedMovie: Movies) {
        _uiState.update {
            it.copy(currentMovie = selectedMovie)
        }
    }

    fun navigateToListPage() {
        _uiState.update {
            it.copy(isShowingListPage = true)
        }
    }


    fun navigateToDetailPage() {
        _uiState.update {
            it.copy(isShowingListPage = false)
        }
    }

    fun getMovies() {
        viewModelScope.launch {
            movieUiState = try {
                MovieUiState.Success(MovieApi.retrofitService.getMovies())
            } catch (e: IOException) {
                MovieUiState.Error
            }
        }
    }
}