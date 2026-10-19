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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.BattlePhase
import com.example.data.model.CardSkill
import com.example.data.model.TurnOwner
import com.example.data.preset.CuratedPresets
import com.example.data.repository.AnimeCardRepository
import com.example.data.repository.BattleRepository
import com.example.data.repository.RankedRepository
import com.example.ui.components.AnimeCardView
import com.example.ui.components.UltimateCutInOverlay
import com.example.ui.theme.AnimeCardBg
import com.example.ui.theme.AnimeDarkBg
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.KiOrange
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StatHp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun BattleScreen(
    battleRepo: BattleRepository,
    cardRepo: AnimeCardRepository,
    rankedRepo: RankedRepository,
    onNavigateToSearch: () -> Unit = {}
) {
    val battleState by battleRepo.battleState.collectAsState()
    val activePlayerDeck by cardRepo.activeDeck.collectAsState()
    val scope = rememberCoroutineScope()

    var showResultDialog by remember { mutableStateOf(false) }

    // Start battle if empty
    LaunchedEffect(Unit) {
        if (battleState.playerTeam.isEmpty()) {
            val opponentDeck = CuratedPresets.jujutsuKaisenDeck
            battleRepo.startBattle(
                playerTeam = activePlayerDeck,
                opponentTeam = opponentDeck,
                isRanked = false,
                opponentName = "Jujutsu AI Bot"
            )
        }
    }

    LaunchedEffect(battleState.phase) {
        if (battleState.phase == BattlePhase.VICTORY || battleState.phase == BattlePhase.DEFEAT) {
            showResultDialog = true
            rankedRepo.recordBattleResult(
                isVictory = battleState.phase == BattlePhase.VICTORY,
                opponentName = battleState.opponentName,
                opponentUniverse = battleState.opponentTeam.firstOrNull()?.animeTitle ?: "Anime Bot"
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeDarkBg)
            .testTag("battle_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Top Bar: Opponent Info & Turn Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NeonCrimson,
                            modifier = Modifier.size(8.dp)
                        ) {}
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = battleState.opponentName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = if (battleState.isRanked) "RANKED DUEL" else "OFFLINE BOT TRIAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (battleState.isRanked) SolarGold else CyberCyan
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AnimeSurface,
                    border = BorderStroke(1.dp, AnimeCardBg)
                ) {
                    Text(
                        text = "TURN ${battleState.turnNumber}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = SolarGold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        val randomDeck = listOf(
                            CuratedPresets.dragonBallDeck,
                            CuratedPresets.onePieceDeck,
                            CuratedPresets.narutoDeck,
                            CuratedPresets.bleachDeck,
                            CuratedPresets.soloLevelingDeck
                        ).random()
                        battleRepo.startBattle(
                            playerTeam = activePlayerDeck,
                            opponentTeam = randomDeck,
                            isRanked = false,
                            opponentName = "AI ${randomDeck.first().animeTitle} Bot"
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeSurface),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("rematch_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "New Opponent",
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Bot", fontSize = 10.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // OPPONENT ROSTER & ACTIVE HERO
            Text(
                text = "OPPONENT LINEUP",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(battleState.opponentTeam) { index, card ->
                    AnimeCardView(
                        card = card,
                        isSelected = index == battleState.activeOpponentIndex,
                        isCombatCompact = true,
                        modifier = Modifier.width(105.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CENTER BATTLEFIELD & COMBAT TICKER
            CombatTickerSection(battleState = battleState)

            Spacer(modifier = Modifier.height(8.dp))

            // PLAYER ACTIVE HERO & BENCH
            Text(
                text = "YOUR BATTLE TEAM",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CyberCyan,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(battleState.playerTeam) { index, card ->
                    AnimeCardView(
                        card = card,
                        isSelected = index == battleState.activePlayerIndex,
                        isCombatCompact = true,
                        modifier = Modifier
                            .width(105.dp)
                            .clickable {
                                scope.launch {
                                    battleRepo.switchPlayerActiveHero(index)
                                }
                            }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PLAYER ACTION PANEL & ENERGY
            PlayerActionControls(
                battleState = battleState,
                onSkillSelected = { skill ->
                    scope.launch {
                        battleRepo.executePlayerSkill(skill)
                    }
                },
                onEndTurn = {
                    scope.launch {
                        battleRepo.endPlayerTurn()
                    }
                }
            )
        }

        // Ultimate Cut-In Banner Animation
        UltimateCutInOverlay(
            isVisible = battleState.ultimateCutInActive,
            casterName = battleState.ultimateCasterName,
            skillName = battleState.ultimateSkillName
        )

        // Victory / Defeat Modal Dialog
        if (showResultDialog) {
            BattleResultDialog(
                isVictory = battleState.phase == BattlePhase.VICTORY,
                opponentName = battleState.opponentName,
                isRanked = battleState.isRanked,
                onDismiss = { showResultDialog = false },
                onRematch = {
                    showResultDialog = false
                    val randomDeck = CuratedPresets.getPresetDeck("Jujutsu Kaisen")
                    battleRepo.startBattle(
                        playerTeam = activePlayerDeck,
                        opponentTeam = randomDeck,
                        isRanked = battleState.isRanked,
                        opponentName = battleState.opponentName
                    )
                },
                onForgeNewDeck = {
                    showResultDialog = false
                    onNavigateToSearch()
                }
            )
        }
    }
}

@Composable
fun CombatTickerSection(battleState: com.example.data.model.BattleState) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(12.dp),
        color = AnimeSurface,
        border = BorderStroke(1.dp, AnimeCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (battleState.currentTurn == TurnOwner.PLAYER) {
                        Surface(shape = CircleShape, color = CyberCyan, modifier = Modifier.size(6.dp)) {}
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "YOUR TURN - CHOOSE SKILL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = CyberCyan
                        )
                    } else {
                        CircularProgressIndicator(
                            modifier = Modifier.size(10.dp),
                            color = NeonCrimson,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "OPPONENT THINKING...",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonCrimson
                        )
                    }
                }

                Text(
                    text = "BATTLE LOG",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Latest Action Log
            val latestLog = battleState.logs.firstOrNull()
            Text(
                text = latestLog?.text ?: "Battle started. Defeat all 3 opponent warriors!",
                fontSize = 11.sp,
                fontWeight = if (latestLog?.isCritical == true) FontWeight.Bold else FontWeight.Normal,
                color = if (latestLog?.isCritical == true) SolarGold else TextPrimary,
                maxLines = 2
            )
        }
    }
}

@Composable
fun PlayerActionControls(
    battleState: com.example.data.model.BattleState,
    onSkillSelected: (CardSkill) -> Unit,
    onEndTurn: () -> Unit
) {
    val activeCard = battleState.activePlayerCard ?: return
    val isPlayerTurn = battleState.currentTurn == TurnOwner.PLAYER && battleState.phase == BattlePhase.PLAYER_INPUT

    val infiniteTransition = rememberInfiniteTransition(label = "ult_pulse")
    val ultGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ult_pulse_glow"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = AnimeSurface,
        border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Energy Bar Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ENERGY: ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CyberCyan
                    )
                    Text(
                        text = "${battleState.playerEnergy}/${battleState.maxPlayerEnergy}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))

                    // Energy crystal indicators
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        for (i in 1..battleState.maxPlayerEnergy) {
                            val isAvailable = i <= battleState.playerEnergy
                            Surface(
                                shape = CircleShape,
                                color = if (isAvailable) CyberCyan else TextMuted.copy(alpha = 0.3f),
                                modifier = Modifier.size(10.dp)
                            ) {}
                        }
                    }
                }

                // End Turn Button
                Button(
                    onClick = onEndTurn,
                    enabled = isPlayerTurn,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnimeCardBg,
                        disabledContainerColor = AnimeDarkBg
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("end_turn_button")
                ) {
                    Text(
                        text = "Pass Turn",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPlayerTurn) TextPrimary else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Skills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activeCard.skills.forEach { skill ->
                    val canAffordEnergy = battleState.playerEnergy >= skill.energyCost
                    val canAffordUlt = !skill.isUltimate || activeCard.ultimateGauge >= 100
                    val isUsable = isPlayerTurn && canAffordEnergy && canAffordUlt

                    val border = when {
                        skill.isUltimate && canAffordUlt -> BorderStroke(2.dp, SolarGold.copy(alpha = ultGlow))
                        skill.isUltimate -> BorderStroke(1.dp, SolarGold.copy(alpha = 0.4f))
                        isUsable -> BorderStroke(1.dp, CyberCyan)
                        else -> BorderStroke(0.5.dp, AnimeCardBg)
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = isUsable) { onSkillSelected(skill) }
                            .testTag("skill_${skill.name.replace(" ", "_")}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                skill.isUltimate && isUsable -> SolarGold.copy(alpha = 0.15f)
                                isUsable -> AnimeCardBg
                                else -> AnimeDarkBg.copy(alpha = 0.5f)
                            }
                        ),
                        border = border
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = skill.name,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (skill.isUltimate) SolarGold else if (isUsable) TextPrimary else TextMuted,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.FlashOn,
                                    contentDescription = "Cost",
                                    tint = if (isUsable) CyberCyan else TextMuted,
                                    modifier = Modifier.size(9.dp)
                                )
                                Text(
                                    text = "${skill.energyCost}⚡",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUsable) CyberCyan else TextMuted
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${skill.powerMultiplier}x",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BattleResultDialog(
    isVictory: Boolean,
    opponentName: String,
    isRanked: Boolean,
    onDismiss: () -> Unit,
    onRematch: () -> Unit,
    onForgeNewDeck: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = AnimeSurface,
            border = BorderStroke(2.dp, if (isVictory) SolarGold else NeonCrimson),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("battle_result_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isVictory) Icons.Default.EmojiEvents else Icons.Default.Warning,
                    contentDescription = if (isVictory) "Victory" else "Defeat",
                    tint = if (isVictory) SolarGold else NeonCrimson,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isVictory) "VICTORY ACHIEVED!" else "DEFEAT...",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isVictory) SolarGold else NeonCrimson,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isVictory)
                        "You triumphed over $opponentName with superior anime tactics!"
                    else
                        "Your squad fell before $opponentName. Upgrade your deck and rise again!",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                if (isRanked) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AnimeCardBg,
                        border = BorderStroke(1.dp, SolarGold.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = if (isVictory) "+25 RANK POINTS (RP)" else "-18 RANK POINTS (RP)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isVictory) SolarGold else NeonCrimson,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onRematch,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isVictory) SolarGold else NeonCrimson),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_rematch_button")
                ) {
                    Text(
                        text = "Play Again",
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onForgeNewDeck,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, CyberCyan),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_forge_deck_button")
                ) {
                    Text(
                        text = "Search Another Anime Deck",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
