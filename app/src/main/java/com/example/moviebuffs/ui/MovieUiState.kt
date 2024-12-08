package com.example.moviebuffs.ui

import com.example.moviebuffs.data.MovieDataSource
import com.example.moviebuffs.network.Movies

data class movieUiState(
    val movieList: List<Movies> = emptyList(),
    val currentMovie: Movies = MovieDataSource.defaultMovie,
    val isShowingListPage: Boolean = true
)