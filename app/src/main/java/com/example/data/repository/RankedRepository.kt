package com.example.data.repository

import com.example.data.model.LeaderboardEntry
import com.example.data.model.MatchHistoryItem
import com.example.data.model.RankProfile
import com.example.data.model.RankTier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

enum class MatchmakingStatus {
    IDLE,
    SEARCHING,
    MATCH_FOUND
}

data class MatchOpponentInfo(
    val name: String,
    val title: String,
    val rankTier: RankTier,
    val rp: Int,
    val animeDeckTitle: String
)

class RankedRepository {

    private val _profile = MutableStateFlow(
        RankProfile(
            playerName = "You (Duelist)",
            currentRp = 1250, // Gold Striker starting point
            wins = 18,
            losses = 6,
            winStreak = 3,
            title = "Cursed Energy Adept"
        )
    )
    val profile: StateFlow<RankProfile> = _profile.asStateFlow()

    private val _isRankedMode = MutableStateFlow(true)
    val isRankedMode: StateFlow<Boolean> = _isRankedMode.asStateFlow()

    private val _matchmakingStatus = MutableStateFlow(MatchmakingStatus.IDLE)
    val matchmakingStatus: StateFlow<MatchmakingStatus> = _matchmakingStatus.asStateFlow()

    private val _foundOpponent = MutableStateFlow<MatchOpponentInfo?>(null)
    val foundOpponent: StateFlow<MatchOpponentInfo?> = _foundOpponent.asStateFlow()

    private val _history = MutableStateFlow<List<MatchHistoryItem>>(
        listOf(
            MatchHistoryItem(
                opponentName = "SaitamaOnePunch",
                opponentUniverse = "One Punch Man",
                isVictory = true,
                isRanked = true,
                rpChange = 25,
                dateText = "Just now"
            ),
            MatchHistoryItem(
                opponentName = "UchihaVengeance",
                opponentUniverse = "Naruto",
                isVictory = true,
                isRanked = true,
                rpChange = 24,
                dateText = "1 hour ago"
            ),
            MatchHistoryItem(
                opponentName = "KingOfCurses_99",
                opponentUniverse = "Jujutsu Kaisen",
                isVictory = false,
                isRanked = true,
                rpChange = -18,
                dateText = "3 hours ago"
            )
        )
    )
    val history: StateFlow<List<MatchHistoryItem>> = _history.asStateFlow()

    val leaderboard: List<LeaderboardEntry> = listOf(
        LeaderboardEntry(1, "ShinigamiLord", "Bleach", RankTier.ANIME_MASTER, 2980, "88%"),
        LeaderboardEntry(2, "SuperSaiyanGod", "Dragon Ball Z", RankTier.ANIME_MASTER, 2840, "84%"),
        LeaderboardEntry(3, "LimitlessGojo", "Jujutsu Kaisen", RankTier.ANIME_MASTER, 2710, "81%"),
        LeaderboardEntry(4, "SunGodNika", "One Piece", RankTier.DIAMOND, 2450, "76%"),
        LeaderboardEntry(5, "ShadowMonarch_JW", "Solo Leveling", RankTier.DIAMOND, 2380, "75%"),
        LeaderboardEntry(6, "AllMightPrime", "My Hero Academia", RankTier.DIAMOND, 2210, "72%"),
        LeaderboardEntry(7, "LeviAckerman3D", "Attack on Titan", RankTier.PLATINUM, 1920, "69%"),
        LeaderboardEntry(8, "KurapikaEmperor", "Hunter x Hunter", RankTier.PLATINUM, 1780, "67%"),
        LeaderboardEntry(9, "TanjiroSunBreathing", "Demon Slayer", RankTier.PLATINUM, 1640, "65%"),
        LeaderboardEntry(10, "MobPsychic100", "Mob Psycho 100", RankTier.GOLD, 1490, "62%")
    )

    fun toggleRankedMode(isRanked: Boolean) {
        _isRankedMode.value = isRanked
    }

    suspend fun startMatchmaking(onMatchReady: (MatchOpponentInfo) -> Unit) {
        _matchmakingStatus.value = MatchmakingStatus.SEARCHING
        _foundOpponent.value = null

        // Realistic matchmaking radar search delay (2 to 3 seconds)
        delay(Random.nextLong(1800, 3200))

        val currentRp = _profile.value.currentRp
        val opponentUniversePool = listOf(
            Pair("Dragon Ball Z", listOf("GokuUltraFan", "PrinceOfSaiyans", "KamehamehaX")),
            Pair("Jujutsu Kaisen", listOf("HollowPurple_Goat", "MalevolentShrine", "BoogieWoogie")),
            Pair("One Piece", listOf("PirateKingLuffy", "EnmaZoro", "BlackLegDiable")),
            Pair("Naruto", listOf("RamenRasengan", "Sharingankakashi", "AmaterasuBurn")),
            Pair("Bleach", listOf("BankaiReaper", "GetsugaCrush", "KyokaHypnosis")),
            Pair("Solo Leveling", listOf("AriseCommander", "ShadowExtract", "DaggerStriker"))
        )

        val selectedUniverse = opponentUniversePool.random()
        val opponentName = selectedUniverse.second.random()
        val opponentRp = (currentRp + Random.nextInt(-80, 90)).coerceAtLeast(100)
        val opponentTier = RankTier.fromRp(opponentRp)

        val opponent = MatchOpponentInfo(
            name = opponentName,
            title = "${opponentTier.title}",
            rankTier = opponentTier,
            rp = opponentRp,
            animeDeckTitle = selectedUniverse.first
        )

        _foundOpponent.value = opponent
        _matchmakingStatus.value = MatchmakingStatus.MATCH_FOUND

        delay(1500) // Highlight matched opponent
        _matchmakingStatus.value = MatchmakingStatus.IDLE
        onMatchReady(opponent)
    }

    fun cancelMatchmaking() {
        _matchmakingStatus.value = MatchmakingStatus.IDLE
        _foundOpponent.value = null
    }

    fun recordBattleResult(isVictory: Boolean, opponentName: String, opponentUniverse: String) {
        val current = _profile.value
        val isRanked = _isRankedMode.value
        val rpChange = if (!isRanked) {
            0
        } else if (isVictory) {
            Random.nextInt(23, 28)
        } else {
            -Random.nextInt(15, 20)
        }

        val newRp = (current.currentRp + rpChange).coerceAtLeast(0)
        val newWins = if (isVictory) current.wins + 1 else current.wins
        val newLosses = if (!isVictory) current.losses + 1 else current.losses
        val newStreak = if (isVictory) current.winStreak + 1 else 0

        _profile.value = current.copy(
            currentRp = newRp,
            wins = newWins,
            losses = newLosses,
            winStreak = newStreak
        )

        val newHistoryItem = MatchHistoryItem(
            opponentName = opponentName,
            opponentUniverse = opponentUniverse,
            isVictory = isVictory,
            isRanked = isRanked,
            rpChange = rpChange,
            dateText = "Just now"
        )

        val updatedHistory = _history.value.toMutableList()
        updatedHistory.add(0, newHistoryItem)
        _history.value = updatedHistory
    }
}
