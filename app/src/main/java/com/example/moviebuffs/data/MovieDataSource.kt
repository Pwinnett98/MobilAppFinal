package com.example.moviebuffs.data

import com.example.moviebuffs.R
import com.example.moviebuffs.network.Movies

object MovieDataSource {
    val defaultMovie = getMovieData()[0]

    fun getMovieData(): List<Movies> {
        return listOf(
        )
    }
}