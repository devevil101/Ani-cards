package com.example.data.model

data class AnimeSeries(
    val id: Int,
    val title: String,
    val imageUrl: String,
    val synopsis: String = "",
    val score: Double = 0.0,
    val genres: List<String> = emptyList(),
    val year: Int? = null,
    val episodes: Int? = null
)
