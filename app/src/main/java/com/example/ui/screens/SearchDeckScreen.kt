package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AnimeCard
import com.example.data.model.AnimeSeries
import com.example.data.preset.CuratedPresets
import com.example.data.repository.AnimeCardRepository
import com.example.ui.components.AnimeCardView
import com.example.ui.components.CardDetailDialog
import com.example.ui.theme.AnimeCardBg
import com.example.ui.theme.AnimeDarkBg
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.SolarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun SearchDeckScreen(
    repository: AnimeCardRepository,
    onNavigateToBattle: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AnimeSeries>>(CuratedPresets.trendingAnime) }
    var isSearching by remember { mutableStateOf(false) }
    var isGeneratingDeck by remember { mutableStateOf(false) }
    var selectedInspectCard by remember { mutableStateOf<AnimeCard?>(null) }
    var showEquipSuccessMessage by remember { mutableStateOf(false) }

    val currentUniverseDeck by repository.currentUniverseDeck.collectAsState()
    val currentUniverseTitle by repository.currentUniverseTitle.collectAsState()
    val activeDeck by repository.activeDeck.collectAsState()

    val scope = rememberCoroutineScope()

    val trendingTags = listOf(
        "Dragon Ball", "Jujutsu Kaisen", "One Piece", "Naruto",
        "Bleach", "Solo Leveling", "Demon Slayer", "Attack on Titan"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeDarkBg)
            .padding(16.dp)
            .testTag("search_deck_screen")
    ) {
        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ANIME DECK FORGE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Search any anime to extract & forge character cards",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CyberCyan.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Powered",
                        tint = CyberCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "AI Stats",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CyberCyan
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                scope.launch {
                    isSearching = true
                    searchResults = repository.searchAnime(it)
                    isSearching = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("anime_search_input"),
            placeholder = { Text("Search any anime (e.g. Hunter x Hunter, Bleach)", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CyberCyan
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        searchQuery = ""
                        searchResults = CuratedPresets.trendingAnime
                    }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextSecondary
                        )
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = AnimeSurface,
                unfocusedContainerColor = AnimeSurface,
                focusedBorderColor = CyberCyan,
                unfocusedBorderColor = AnimeCardBg,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = CyberCyan
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Trending Quick Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            trendingTags.forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (searchQuery == tag) NeonCrimson else AnimeCardBg,
                    border = BorderStroke(0.8.dp, if (searchQuery == tag) NeonCrimson else AnimeSurface),
                    modifier = Modifier
                        .clickable {
                            searchQuery = tag
                            scope.launch {
                                isSearching = true
                                searchResults = repository.searchAnime(tag)
                                isSearching = false
                            }
                        }
                        .testTag("trending_tag_${tag.replace(" ", "_")}")
                ) {
                    Text(
                        text = tag,
                        color = if (searchQuery == tag) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Content: Anime Carousel / Results & Deck View
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Anime Results Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (searchQuery.isBlank()) "POPULAR ANIME UNIVERSES" else "SEARCH RESULTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = CyberCyan,
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(searchResults) { anime ->
                        AnimeSeriesCard(
                            anime = anime,
                            isSelected = anime.title.equals(currentUniverseTitle, ignoreCase = true),
                            onClick = {
                                scope.launch {
                                    isGeneratingDeck = true
                                    repository.loadDeckForAnime(anime)
                                    isGeneratingDeck = false
                                }
                            }
                        )
                    }
                }
            }

            // Section 2: Current Universe Deck Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$currentUniverseTitle DECK".uppercase(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = SolarGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${currentUniverseDeck.size} Characters with authentic anime abilities",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    if (isGeneratingDeck) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = SolarGold,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Forging stats...", fontSize = 10.sp, color = SolarGold)
                        }
                    } else {
                        Button(
                            onClick = {
                                repository.setActiveBattleDeck(currentUniverseDeck)
                                showEquipSuccessMessage = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("equip_universe_deck_button")
                        ) {
                            Text("Equip Team", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (showEquipSuccessMessage) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF064E3B),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Equipped",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Equipped 3 heroes from $currentUniverseTitle into your active battle lineup!",
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            // Grid of Cards in current universe
            items(currentUniverseDeck.chunked(2)) { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (card in pair) {
                        AnimeCardView(
                            card = card,
                            modifier = Modifier.weight(1f),
                            isSelected = activeDeck.any { it.name == card.name },
                            onClick = { selectedInspectCard = card }
                        )
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Modal Card Detail Dialog
    selectedInspectCard?.let { card ->
        CardDetailDialog(
            card = card,
            onDismiss = { selectedInspectCard = null },
            onAssignToSlot = { slot ->
                repository.setCardInActiveDeck(slot, card)
                showEquipSuccessMessage = true
            }
        )
    }
}

@Composable
fun AnimeSeriesCard(
    anime: AnimeSeries,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("anime_poster_${anime.title.replace(" ", "_")}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) SolarGold else AnimeCardBg
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                AsyncImage(
                    model = anime.imageUrl,
                    contentDescription = anime.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (anime.score > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.75f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Score",
                                tint = SolarGold,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "%.1f".format(anime.score),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = anime.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isSelected) "Active Universe" else "Tap to Forge",
                    fontSize = 9.sp,
                    color = if (isSelected) SolarGold else CyberCyan
                )
            }
        }
    }
}
