package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardEntry
import com.example.data.model.MatchHistoryItem
import com.example.data.model.RankTier
import com.example.data.preset.CuratedPresets
import com.example.data.repository.AnimeCardRepository
import com.example.data.repository.BattleRepository
import com.example.data.repository.MatchmakingStatus
import com.example.data.repository.RankedRepository
import com.example.ui.theme.AnimeCardBg
import com.example.ui.theme.AnimeDarkBg
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StatHp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun OnlineRankedScreen(
    rankedRepo: RankedRepository,
    cardRepo: AnimeCardRepository,
    battleRepo: BattleRepository,
    onNavigateToBattle: () -> Unit
) {
    val profile by rankedRepo.profile.collectAsState()
    val isRankedMode by rankedRepo.isRankedMode.collectAsState()
    val matchmakingStatus by rankedRepo.matchmakingStatus.collectAsState()
    val foundOpponent by rankedRepo.foundOpponent.collectAsState()
    val history by rankedRepo.history.collectAsState()
    val activeDeck by cardRepo.activeDeck.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "radar_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeDarkBg)
            .padding(16.dp)
            .testTag("online_ranked_screen")
    ) {
        // Header Row: Title & Ranked Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ONLINE ARENA",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (isRankedMode) "Competitive Rank Points (RP) Active" else "Casual Practice Matches",
                    fontSize = 11.sp,
                    color = if (isRankedMode) SolarGold else CyberCyan
                )
            }

            // Ranked Mode Toggle with Label
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AnimeSurface,
                border = BorderStroke(1.dp, if (isRankedMode) SolarGold else CyberCyan)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isRankedMode) "RANKED" else "CASUAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isRankedMode) SolarGold else CyberCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isRankedMode,
                        onCheckedChange = { rankedRepo.toggleRankedMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SolarGold,
                            checkedTrackColor = SolarGold.copy(alpha = 0.3f),
                            uncheckedThumbColor = CyberCyan,
                            uncheckedTrackColor = AnimeCardBg
                        ),
                        modifier = Modifier.testTag("ranked_mode_toggle")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // PLAYER PROFILE & TIER CARD
        PlayerProfileBanner(profile = profile, isRankedMode = isRankedMode)

        Spacer(modifier = Modifier.height(14.dp))

        // MATCHMAKING ACTION CARD / RADAR
        MatchmakingActionCard(
            matchmakingStatus = matchmakingStatus,
            isRankedMode = isRankedMode,
            radarScale = radarPulse,
            foundOpponent = foundOpponent,
            onStartQueue = {
                scope.launch {
                    rankedRepo.startMatchmaking { opponent ->
                        val opponentUniverseDeck = CuratedPresets.getPresetDeck(opponent.animeDeckTitle)
                        battleRepo.startBattle(
                            playerTeam = activeDeck,
                            opponentTeam = opponentUniverseDeck,
                            isRanked = isRankedMode,
                            opponentName = opponent.name
                        )
                        onNavigateToBattle()
                    }
                }
            },
            onCancelQueue = { rankedRepo.cancelMatchmaking() }
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tabs: Leaderboard vs Match History
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = AnimeSurface,
            contentColor = CyberCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = if (selectedTab == 0) SolarGold else CyberCyan
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = "Leaderboard", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LEADERBOARD", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_leaderboard")
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.History, contentDescription = "History", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("MATCH HISTORY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                },
                modifier = Modifier.testTag("tab_history")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            if (selectedTab == 0) {
                LeaderboardListView(entries = rankedRepo.leaderboard)
            } else {
                MatchHistoryListView(history = history)
            }
        }
    }
}

@Composable
fun PlayerProfileBanner(profile: com.example.data.model.RankProfile, isRankedMode: Boolean) {
    val tierColor = Color(profile.tier.colorHex)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.5.dp, tierColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = tierColor.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, tierColor),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Tier Badge",
                                tint = tierColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = profile.playerName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                        Text(
                            text = profile.tier.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = tierColor
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${profile.currentRp} RP",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = SolarGold
                    )
                    Text(
                        text = "Win Rate: ${profile.winRate}%",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // RP Progress Bar to next tier
            val nextTierRp = (profile.tier.maxRp + 1).coerceAtMost(3000)
            val currentTierMin = profile.tier.minRp
            val progress = ((profile.currentRp - currentTierMin).toFloat() / (nextTierRp - currentTierMin).toFloat()).coerceIn(0f, 1f)

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tier Progression",
                        fontSize = 9.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "${profile.currentRp} / $nextTierRp RP",
                        fontSize = 9.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(CircleShape),
                    color = tierColor,
                    trackColor = AnimeDarkBg
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stats Row: Wins, Losses, Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatPill("WINS", "${profile.wins}", StatHp)
                StatPill("LOSSES", "${profile.losses}", NeonCrimson)
                StatPill("WIN STREAK", "${profile.winStreak} 🔥", SolarGold)
            }
        }
    }
}

@Composable
private fun StatPill(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = AnimeCardBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "$label: ", fontSize = 9.sp, color = TextSecondary)
            Text(text = value, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun MatchmakingActionCard(
    matchmakingStatus: MatchmakingStatus,
    isRankedMode: Boolean,
    radarScale: Float,
    foundOpponent: com.example.data.repository.MatchOpponentInfo?,
    onStartQueue: () -> Unit,
    onCancelQueue: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = AnimeSurface,
        border = BorderStroke(1.dp, if (matchmakingStatus == MatchmakingStatus.SEARCHING) CyberCyan else AnimeCardBg),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (matchmakingStatus) {
                MatchmakingStatus.IDLE -> {
                    Button(
                        onClick = onStartQueue,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRankedMode) SolarGold else CyberCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("find_match_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Search Queue",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRankedMode) "FIND RANKED MATCH" else "FIND CASUAL MATCH",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }

                MatchmakingStatus.SEARCHING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = CyberCyan,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "SCANNING ANIME SERVERS...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = CyberCyan
                                )
                                Text(
                                    text = "Matching by RP skill bracket",
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onCancelQueue,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, NeonCrimson),
                            modifier = Modifier.testTag("cancel_queue_button")
                        ) {
                            Text("Cancel", fontSize = 10.sp, color = NeonCrimson)
                        }
                    }
                }

                MatchmakingStatus.MATCH_FOUND -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "⚔️ MATCH OPPONENT FOUND! ⚔️",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SolarGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${foundOpponent?.name} (${foundOpponent?.animeDeckTitle} Deck • ${foundOpponent?.rp} RP)",
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Entering arena...",
                            fontSize = 10.sp,
                            color = CyberCyan
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardListView(entries: List<LeaderboardEntry>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(entries) { entry ->
            val isTop3 = entry.rank <= 3
            val medalColor = when (entry.rank) {
                1 -> SolarGold
                2 -> Color(0xFFC0C0C0)
                3 -> Color(0xFFCD7F32)
                else -> TextSecondary
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AnimeSurface,
                border = BorderStroke(
                    if (isTop3) 1.dp else 0.5.dp,
                    if (isTop3) medalColor else AnimeCardBg
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "#${entry.rank}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = medalColor,
                            modifier = Modifier.width(30.dp)
                        )

                        Column {
                            Text(
                                text = entry.playerName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${entry.favoriteUniverse} Deck • ${entry.tier.title}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${entry.rp} RP",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = SolarGold
                        )
                        Text(
                            text = "WR: ${entry.winRate}",
                            fontSize = 9.sp,
                            color = StatHp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchHistoryListView(history: List<MatchHistoryItem>) {
    if (history.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No online matches played yet.\nEnter the queue to duel anime battlers!",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(history) { match ->
            val resultColor = if (match.isVictory) StatHp else NeonCrimson

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = AnimeSurface,
                border = BorderStroke(1.dp, resultColor.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = resultColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (match.isVictory) Icons.Default.Check else Icons.Default.Close,
                                    contentDescription = "Result",
                                    tint = resultColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "vs ${match.opponentName}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${match.opponentUniverse} • ${match.dateText}",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (match.isVictory) "VICTORY" else "DEFEAT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = resultColor
                        )
                        if (match.isRanked) {
                            Text(
                                text = if (match.rpChange >= 0) "+${match.rpChange} RP" else "${match.rpChange} RP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (match.rpChange >= 0) SolarGold else NeonCrimson
                            )
                        } else {
                            Text("Casual", fontSize = 9.sp, color = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
