package com.example.data.repository

import com.example.data.model.AnimeCard
import com.example.data.model.BattleLog
import com.example.data.model.BattlePhase
import com.example.data.model.BattleState
import com.example.data.model.CardSkill
import com.example.data.model.SkillType
import com.example.data.model.TurnOwner
import com.example.data.preset.CuratedPresets
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random

class BattleRepository {

    private val _battleState = MutableStateFlow(
        createBattleState(
            playerTeam = CuratedPresets.dragonBallDeck,
            opponentTeam = CuratedPresets.jujutsuKaisenDeck,
            isRanked = false,
            opponentName = "Jujutsu AI Bot"
        )
    )
    val battleState: StateFlow<BattleState> = _battleState.asStateFlow()

    private fun createBattleState(
        playerTeam: List<AnimeCard>,
        opponentTeam: List<AnimeCard>,
        isRanked: Boolean = false,
        opponentName: String = "Anime Bot"
    ): BattleState {
        val readyPlayerTeam = playerTeam.map { it.copy(currentHp = it.maxHp, shield = 0, ultimateGauge = 25) }
        val readyOpponentTeam = opponentTeam.map { it.copy(currentHp = it.maxHp, shield = 0, ultimateGauge = 20) }

        return BattleState(
            playerTeam = readyPlayerTeam,
            opponentTeam = readyOpponentTeam,
            activePlayerIndex = 0,
            activeOpponentIndex = 0,
            turnNumber = 1,
            currentTurn = TurnOwner.PLAYER,
            playerEnergy = 3,
            maxPlayerEnergy = 3,
            opponentEnergy = 3,
            maxOpponentEnergy = 3,
            phase = BattlePhase.PLAYER_INPUT,
            logs = listOf(
                BattleLog(text = "Battle commenced between ${readyPlayerTeam.firstOrNull()?.name ?: "Player"} and $opponentName!")
            ),
            isRanked = isRanked,
            opponentName = opponentName
        )
    }

    fun startBattle(
        playerTeam: List<AnimeCard>,
        opponentTeam: List<AnimeCard>,
        isRanked: Boolean = false,
        opponentName: String = "Anime Bot"
    ) {
        _battleState.value = createBattleState(playerTeam, opponentTeam, isRanked, opponentName)
    }

    suspend fun executePlayerSkill(skill: CardSkill) {
        val state = _battleState.value
        val playerCard = state.activePlayerCard ?: return
        val opponentCard = state.activeOpponentCard ?: return

        if (state.playerEnergy < skill.energyCost) return
        if (skill.isUltimate && playerCard.ultimateGauge < 100) return

        // Deduct energy
        val newPlayerEnergy = state.playerEnergy - skill.energyCost

        // Handle Ultimate cut-in visual
        if (skill.isUltimate) {
            _battleState.value = state.copy(
                ultimateCutInActive = true,
                ultimateSkillName = skill.name,
                ultimateCasterName = playerCard.name
            )
            delay(1200) // Cut-in banner duration
            _battleState.value = _battleState.value.copy(ultimateCutInActive = false)
        }

        val updatedPlayerTeam = state.playerTeam.toMutableList()
        val updatedOpponentTeam = state.opponentTeam.toMutableList()
        val newLogs = state.logs.toMutableList()

        if (skill.skillType == SkillType.HEAL) {
            val healAmount = (playerCard.maxHp * 0.25).toInt()
            val newHp = (playerCard.currentHp + healAmount).coerceAtMost(playerCard.maxHp)
            updatedPlayerTeam[state.activePlayerIndex] = playerCard.copy(
                currentHp = newHp,
                ultimateGauge = if (skill.isUltimate) 0 else (playerCard.ultimateGauge + 20).coerceAtMost(100)
            )
            newLogs.add(0, BattleLog(text = "${playerCard.name} used ${skill.name} and healed $healAmount HP!"))
        } else if (skill.skillType == SkillType.SHIELD) {
            val shieldGain = 650
            updatedPlayerTeam[state.activePlayerIndex] = playerCard.copy(
                shield = playerCard.shield + shieldGain,
                ultimateGauge = if (skill.isUltimate) 0 else (playerCard.ultimateGauge + 20).coerceAtMost(100)
            )
            newLogs.add(0, BattleLog(text = "${playerCard.name} raised ${skill.name} granting +$shieldGain Shield!"))
        } else {
            // Attack calculation
            val (damage, isSuperEffective, isResisted) = calculateDamage(playerCard, opponentCard, skill)
            val (remainingShield, hpDamage) = applyDamageToShieldAndHp(opponentCard.shield, opponentCard.currentHp, damage)

            val newOpponentHp = (opponentCard.currentHp - hpDamage).coerceAtLeast(0)
            val newOpponentCard = opponentCard.copy(
                shield = remainingShield,
                currentHp = newOpponentHp,
                ultimateGauge = (opponentCard.ultimateGauge + 15).coerceAtMost(100)
            )
            updatedOpponentTeam[state.activeOpponentIndex] = newOpponentCard

            val newPlayerCard = playerCard.copy(
                ultimateGauge = if (skill.isUltimate) 0 else (playerCard.ultimateGauge + 25).coerceAtMost(100)
            )
            updatedPlayerTeam[state.activePlayerIndex] = newPlayerCard

            val effectText = if (isSuperEffective) " (SUPER EFFECTIVE!)" else if (isResisted) " (Resisted)" else ""
            newLogs.add(
                0,
                BattleLog(
                    text = "${playerCard.name} unleashed ${skill.name} dealing $damage damage$effectText!",
                    isCritical = isSuperEffective || skill.isUltimate,
                    isPlayerAction = true
                )
            )

            // Check if opponent card fainted
            if (newOpponentHp <= 0) {
                newLogs.add(0, BattleLog(text = "${opponentCard.name} has fainted!"))
                val nextOpponentIndex = updatedOpponentTeam.indexOfFirst { !it.isFainted }
                if (nextOpponentIndex == -1) {
                    // VICTORY!
                    _battleState.value = state.copy(
                        playerTeam = updatedPlayerTeam,
                        opponentTeam = updatedOpponentTeam,
                        phase = BattlePhase.VICTORY,
                        logs = newLogs
                    )
                    return
                } else {
                    _battleState.value = state.copy(
                        playerTeam = updatedPlayerTeam,
                        opponentTeam = updatedOpponentTeam,
                        activeOpponentIndex = nextOpponentIndex,
                        logs = newLogs
                    )
                }
            }
        }

        _battleState.value = _battleState.value.copy(
            playerTeam = updatedPlayerTeam,
            opponentTeam = updatedOpponentTeam,
            playerEnergy = newPlayerEnergy,
            logs = newLogs
        )

        // Pass turn if player energy is exhausted or user triggers next
        if (newPlayerEnergy <= 0) {
            endPlayerTurn()
        }
    }

    suspend fun switchPlayerActiveHero(newIndex: Int) {
        val state = _battleState.value
        if (newIndex !in state.playerTeam.indices || state.playerTeam[newIndex].isFainted) return
        if (newIndex == state.activePlayerIndex) return

        val newLogs = state.logs.toMutableList()
        newLogs.add(0, BattleLog(text = "Player tagged in ${state.playerTeam[newIndex].name}!"))

        _battleState.value = state.copy(
            activePlayerIndex = newIndex,
            logs = newLogs
        )
    }

    suspend fun endPlayerTurn() {
        val state = _battleState.value
        if (state.phase == BattlePhase.VICTORY || state.phase == BattlePhase.DEFEAT) return

        // Switch to Opponent Turn
        _battleState.value = state.copy(
            currentTurn = TurnOwner.OPPONENT,
            phase = BattlePhase.BOT_THINKING
        )

        delay(900) // Bot thinking delay
        executeOpponentBotTurn()
    }

    private suspend fun executeOpponentBotTurn() {
        var state = _battleState.value
        val opponentCard = state.activeOpponentCard ?: return
        val playerCard = state.activePlayerCard ?: return

        // Bot selects best affordable skill
        val availableSkills = opponentCard.skills.filter { skill ->
            val costMet = state.opponentEnergy >= skill.energyCost
            val ultMet = !skill.isUltimate || opponentCard.ultimateGauge >= 100
            costMet && ultMet
        }

        val chosenSkill = availableSkills.maxByOrNull {
            if (it.isUltimate) 100.0 else it.powerMultiplier
        } ?: opponentCard.skills.firstOrNull()

        if (chosenSkill != null && state.opponentEnergy >= chosenSkill.energyCost) {
            val newOpponentEnergy = state.opponentEnergy - chosenSkill.energyCost

            if (chosenSkill.isUltimate) {
                _battleState.value = state.copy(
                    ultimateCutInActive = true,
                    ultimateSkillName = chosenSkill.name,
                    ultimateCasterName = opponentCard.name
                )
                delay(1200)
                state = _battleState.value.copy(ultimateCutInActive = false)
            }

            val updatedPlayerTeam = state.playerTeam.toMutableList()
            val updatedOpponentTeam = state.opponentTeam.toMutableList()
            val newLogs = state.logs.toMutableList()

            if (chosenSkill.skillType == SkillType.HEAL) {
                val healAmount = (opponentCard.maxHp * 0.25).toInt()
                val newHp = (opponentCard.currentHp + healAmount).coerceAtMost(opponentCard.maxHp)
                updatedOpponentTeam[state.activeOpponentIndex] = opponentCard.copy(
                    currentHp = newHp,
                    ultimateGauge = if (chosenSkill.isUltimate) 0 else (opponentCard.ultimateGauge + 20).coerceAtMost(100)
                )
                newLogs.add(0, BattleLog(text = "${opponentCard.name} used ${chosenSkill.name} recovering $healAmount HP!"))
            } else if (chosenSkill.skillType == SkillType.SHIELD) {
                val shieldGain = 600
                updatedOpponentTeam[state.activeOpponentIndex] = opponentCard.copy(
                    shield = opponentCard.shield + shieldGain,
                    ultimateGauge = if (chosenSkill.isUltimate) 0 else (opponentCard.ultimateGauge + 20).coerceAtMost(100)
                )
                newLogs.add(0, BattleLog(text = "${opponentCard.name} raised ${chosenSkill.name} gaining +$shieldGain Shield!"))
            } else {
                val (damage, isSuperEffective, isResisted) = calculateDamage(opponentCard, playerCard, chosenSkill)
                val (remainingShield, hpDamage) = applyDamageToShieldAndHp(playerCard.shield, playerCard.currentHp, damage)

                val newPlayerHp = (playerCard.currentHp - hpDamage).coerceAtLeast(0)
                val newPlayerCard = playerCard.copy(
                    shield = remainingShield,
                    currentHp = newPlayerHp,
                    ultimateGauge = (playerCard.ultimateGauge + 15).coerceAtMost(100)
                )
                updatedPlayerTeam[state.activePlayerIndex] = newPlayerCard

                val newOpponentCard = opponentCard.copy(
                    ultimateGauge = if (chosenSkill.isUltimate) 0 else (opponentCard.ultimateGauge + 25).coerceAtMost(100)
                )
                updatedOpponentTeam[state.activeOpponentIndex] = newOpponentCard

                val effectText = if (isSuperEffective) " (SUPER EFFECTIVE!)" else if (isResisted) " (Resisted)" else ""
                newLogs.add(
                    0,
                    BattleLog(
                        text = "${opponentCard.name} struck back with ${chosenSkill.name} dealing $damage damage$effectText!",
                        isCritical = isSuperEffective || chosenSkill.isUltimate,
                        isPlayerAction = false
                    )
                )

                // Check player hero faint
                if (newPlayerHp <= 0) {
                    newLogs.add(0, BattleLog(text = "${playerCard.name} has fallen in battle!"))
                    val nextPlayerIndex = updatedPlayerTeam.indexOfFirst { !it.isFainted }
                    if (nextPlayerIndex == -1) {
                        // DEFEAT!
                        _battleState.value = state.copy(
                            playerTeam = updatedPlayerTeam,
                            opponentTeam = updatedOpponentTeam,
                            phase = BattlePhase.DEFEAT,
                            logs = newLogs
                        )
                        return
                    } else {
                        state = state.copy(
                            playerTeam = updatedPlayerTeam,
                            opponentTeam = updatedOpponentTeam,
                            activePlayerIndex = nextPlayerIndex,
                            logs = newLogs
                        )
                    }
                }
            }

            state = state.copy(
                playerTeam = updatedPlayerTeam,
                opponentTeam = updatedOpponentTeam,
                opponentEnergy = newOpponentEnergy,
                logs = newLogs
            )
        }

        delay(800)

        // Reset to next turn
        val nextTurnNumber = state.turnNumber + 1
        val newMaxPlayerEnergy = (state.maxPlayerEnergy + 1).coerceAtMost(6)
        val newMaxOpponentEnergy = (state.maxOpponentEnergy + 1).coerceAtMost(6)

        _battleState.value = state.copy(
            turnNumber = nextTurnNumber,
            currentTurn = TurnOwner.PLAYER,
            playerEnergy = newMaxPlayerEnergy,
            maxPlayerEnergy = newMaxPlayerEnergy,
            opponentEnergy = newMaxOpponentEnergy,
            maxOpponentEnergy = newMaxOpponentEnergy,
            phase = BattlePhase.PLAYER_INPUT
        )
    }

    private fun calculateDamage(
        attacker: AnimeCard,
        defender: AnimeCard,
        skill: CardSkill
    ): Triple<Int, Boolean, Boolean> {
        val baseDamage = attacker.attack * skill.powerMultiplier
        val isSuperEffective = attacker.element.getAdvantageAgainst() == defender.element
        val isResisted = defender.element.getAdvantageAgainst() == attacker.element

        val elementalMultiplier = when {
            isSuperEffective -> 1.30
            isResisted -> 0.80
            else -> 1.0
        }

        val defenseFactor = 1000.0 / (1000.0 + defender.defense)
        val variance = Random.nextDouble(0.92, 1.08)

        val totalDamage = (baseDamage * elementalMultiplier * defenseFactor * variance).toInt().coerceAtLeast(120)
        return Triple(totalDamage, isSuperEffective, isResisted)
    }

    private fun applyDamageToShieldAndHp(currentShield: Int, currentHp: Int, damage: Int): Pair<Int, Int> {
        return if (currentShield > 0) {
            if (currentShield >= damage) {
                Pair(currentShield - damage, 0)
            } else {
                val remainingDamage = damage - currentShield
                Pair(0, remainingDamage)
            }
        } else {
            Pair(0, damage)
        }
    }
}
