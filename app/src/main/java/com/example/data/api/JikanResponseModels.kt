package com.example.data.api

import com.squareup.moshi.Json

data class JikanAnimeSearchResponse(
    @Json(name = "data") val data: List<JikanAnimeDto>? = null
)

data class JikanAnimeDto(
    @Json(name = "mal_id") val malId: Int,
    @Json(name = "title") val title: String,
    @Json(name = "synopsis") val synopsis: String? = null,
    @Json(name = "score") val score: Double? = null,
    @Json(name = "episodes") val episodes: Int? = null,
    @Json(name = "year") val year: Int? = null,
    @Json(name = "images") val images: JikanImagesDto? = null,
    @Json(name = "genres") val genres: List<JikanGenreDto>? = null
)

data class JikanImagesDto(
    @Json(name = "jpg") val jpg: JikanImageFormatDto? = null
)

data class JikanImageFormatDto(
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "large_image_url") val largeImageUrl: String? = null
)

data class JikanGenreDto(
    @Json(name = "mal_id") val malId: Int? = null,
    @Json(name = "name") val name: String? = null
)

data class JikanCharactersResponse(
    @Json(name = "data") val data: List<JikanCharacterEntryDto>? = null
)

data class JikanCharacterEntryDto(
    @Json(name = "character") val character: JikanCharacterDto? = null,
    @Json(name = "role") val role: String? = null
)

data class JikanCharacterDto(
    @Json(name = "mal_id") val malId: Int,
    @Json(name = "name") val name: String,
    @Json(name = "images") val images: JikanImagesDto? = null
)
