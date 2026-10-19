package com.example.data.model

enum class TurnOwner {
    PLAYER,
    OPPONENT
}

enum class BattlePhase {
    PLAYER_INPUT,
    ANIMATING,
    BOT_THINKING,
    VICTORY,
    DEFEAT
}

data class BattleLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isCritical: Boolean = false,
    val isPlayerAction: Boolean = true
)

data class BattleState(
    val playerTeam: List<AnimeCard> = emptyList(),
    val opponentTeam: List<AnimeCard> = emptyList(),
    val activePlayerIndex: Int = 0,
    val activeOpponentIndex: Int = 0,
    val turnNumber: Int = 1,
    val currentTurn: TurnOwner = TurnOwner.PLAYER,
    val playerEnergy: Int = 3,
    val maxPlayerEnergy: Int = 3,
    val opponentEnergy: Int = 3,
    val maxOpponentEnergy: Int = 3,
    val phase: BattlePhase = BattlePhase.PLAYER_INPUT,
    val logs: List<BattleLog> = emptyList(),
    val lastDamageDealt: Int? = null,
    val ultimateCutInActive: Boolean = false,
    val ultimateSkillName: String? = null,
    val ultimateCasterName: String? = null,
    val isRanked: Boolean = false,
    val opponentName: String = "Anime Bot"
) {
    val activePlayerCard: AnimeCard?
        get() = playerTeam.getOrNull(activePlayerIndex)

    val activeOpponentCard: AnimeCard?
        get() = opponentTeam.getOrNull(activeOpponentIndex)

    val isGameOver: Boolean
        get() = playerTeam.all { it.isFainted } || opponentTeam.all { it.isFainted }

    val isPlayerVictorious: Boolean
        get() = opponentTeam.all { it.isFainted } && playerTeam.any { !it.isFainted }
}
