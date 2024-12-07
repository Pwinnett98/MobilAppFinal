package com.example.moviebuffs.network

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Movies(
    val id: String,
    @SerialName(value = "img_src")
    val imgSrc: String
)