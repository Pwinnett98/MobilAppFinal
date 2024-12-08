package com.example.moviebuffs.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

@Serializable
data class Movies(
    val title: String,
    val poster: String,
    @StringRes val titleResourceId: Int,
    @StringRes val subtitleResourceId: Int,
    val description: String,
    @DrawableRes val imageResourceId: Int,
    @DrawableRes val movieImageBanner: Int,
    @StringRes val movieDetails: Int
)