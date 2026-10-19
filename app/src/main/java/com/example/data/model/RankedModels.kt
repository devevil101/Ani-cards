package com.example.data.model

enum class RankTier(
    val title: String,
    val minRp: Int,
    val maxRp: Int,
    val badgeIconName: String,
    val colorHex: Long
) {
    BRONZE("Bronze Challenger", 0, 499, "shield_bronze", 0xFFCD7F32),
    SILVER("Silver Duelist", 500, 999, "shield_silver", 0xFFC0C0C0),
    GOLD("Gold Striker", 1000, 1499, "shield_gold", 0xFFFFD700),
    PLATINUM("Platinum Champion", 1500, 1999, "shield_plat", 0xFF00E5FF),
    DIAMOND("Diamond Titan", 2000, 2499, "shield_diamond", 0xFFBF5AF2),
    ANIME_MASTER("Anime Grandmaster", 2500, 99999, "shield_master", 0xFFFF2A6D);

    companion object {
        fun fromRp(rp: Int): RankTier {
            return entries.findLast { rp >= it.minRp } ?: BRONZE
        }
    }
}

data class RankProfile(
    val playerName: String = "Anime Duelist",
    val currentRp: Int = 1120,
    val wins: Int = 14,
    val losses: Int = 4,
    val winStreak: Int = 3,
    val title: String = "Rising Sorcerer"
) {
    val tier: RankTier
        get() = RankTier.fromRp(currentRp)

    val winRate: Int
        get() = if (wins + losses == 0) 0 else ((wins.toFloat() / (wins + losses)) * 100).toInt()
}

data class LeaderboardEntry(
    val rank: Int,
    val playerName: String,
    val favoriteUniverse: String,
    val tier: RankTier,
    val rp: Int,
    val winRate: String
)

data class MatchHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val opponentName: String,
    val opponentUniverse: String,
    val isVictory: Boolean,
    val isRanked: Boolean,
    val rpChange: Int,
    val dateText: String
)
