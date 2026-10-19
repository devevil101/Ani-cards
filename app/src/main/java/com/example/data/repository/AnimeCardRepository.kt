package com.example.data.repository

import com.example.data.api.GeminiLoreService
import com.example.data.api.JikanApiService
import com.example.data.model.AnimeCard
import com.example.data.model.AnimeSeries
import com.example.data.preset.CuratedPresets
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AnimeCardRepository {
    private val jikanApi by lazy { JikanApiService.create() }
    private val geminiService by lazy { GeminiLoreService() }

    // Currently selected active battle deck (3 active heroes)
    private val _activeDeck = MutableStateFlow<List<AnimeCard>>(CuratedPresets.dragonBallDeck)
    val activeDeck: StateFlow<List<AnimeCard>> = _activeDeck.asStateFlow()

    // Current inspected/generated anime deck
    private val _currentUniverseDeck = MutableStateFlow<List<AnimeCard>>(CuratedPresets.dragonBallDeck)
    val currentUniverseDeck: StateFlow<List<AnimeCard>> = _currentUniverseDeck.asStateFlow()

    private val _currentUniverseTitle = MutableStateFlow("Dragon Ball Z")
    val currentUniverseTitle: StateFlow<String> = _currentUniverseTitle.asStateFlow()

    suspend fun searchAnime(query: String): List<AnimeSeries> {
        if (query.isBlank()) {
            return CuratedPresets.trendingAnime
        }
        return try {
            val response = jikanApi.searchAnime(query = query, limit = 10)
            response.data?.map { dto ->
                AnimeSeries(
                    id = dto.malId,
                    title = dto.title,
                    imageUrl = dto.images?.jpg?.largeImageUrl
                        ?: dto.images?.jpg?.imageUrl
                        ?: "https://cdn.myanimelist.net/images/anime/1607/117271.jpg",
                    synopsis = dto.synopsis ?: "No description available.",
                    score = dto.score ?: 0.0,
                    genres = dto.genres?.mapNotNull { it.name } ?: emptyList(),
                    year = dto.year,
                    episodes = dto.episodes
                )
            } ?: CuratedPresets.trendingAnime.filter { it.title.contains(query, ignoreCase = true) }
        } catch (_: Exception) {
            CuratedPresets.trendingAnime.filter { it.title.contains(query, ignoreCase = true) }
        }
    }

    suspend fun loadDeckForAnime(series: AnimeSeries): List<AnimeCard> {
        _currentUniverseTitle.value = series.title
        return try {
            val charResponse = jikanApi.getAnimeCharacters(series.id)
            val characters = charResponse.data?.mapNotNull { entry ->
                val char = entry.character ?: return@mapNotNull null
                val img = char.images?.jpg?.imageUrl
                Pair(char.name, img)
            }?.take(6) ?: emptyList()

            val generatedCards = if (characters.isNotEmpty()) {
                geminiService.enhanceDeckWithAi(series.title, characters)
            } else {
                CuratedPresets.getPresetDeck(series.title)
            }

            _currentUniverseDeck.value = generatedCards
            generatedCards
        } catch (_: Exception) {
            val fallback = CuratedPresets.getPresetDeck(series.title)
            _currentUniverseDeck.value = fallback
            fallback
        }
    }

    fun setActiveBattleDeck(cards: List<AnimeCard>) {
        _activeDeck.value = cards.take(3)
    }

    fun setCardInActiveDeck(slotIndex: Int, newCard: AnimeCard) {
        val current = _activeDeck.value.toMutableList()
        if (slotIndex in current.indices) {
            current[slotIndex] = newCard
            _activeDeck.value = current
        } else if (current.size < 3) {
            current.add(newCard)
            _activeDeck.value = current
        }
    }
}
