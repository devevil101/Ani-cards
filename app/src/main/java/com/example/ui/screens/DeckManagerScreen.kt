package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimeCard
import com.example.data.repository.AnimeCardRepository
import com.example.data.repository.BattleRepository
import com.example.ui.components.AnimeCardView
import com.example.ui.components.CardDetailDialog
import com.example.ui.theme.AnimeCardBg
import com.example.ui.theme.AnimeDarkBg
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StatAtk
import com.example.ui.theme.StatDef
import com.example.ui.theme.StatHp
import com.example.ui.theme.StatSpd
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DeckManagerScreen(
    cardRepo: AnimeCardRepository,
    battleRepo: BattleRepository,
    onNavigateToSearch: () -> Unit,
    onNavigateToBattle: () -> Unit
) {
    val activeDeck by cardRepo.activeDeck.collectAsState()
    var selectedInspectCard by remember { mutableStateOf<AnimeCard?>(null) }

    val totalHp = activeDeck.sumOf { it.maxHp }
    val totalAtk = activeDeck.sumOf { it.attack }
    val totalDef = activeDeck.sumOf { it.defense }
    val teamPower = (totalHp * 0.4 + totalAtk * 1.5 + totalDef * 1.2).toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnimeDarkBg)
            .padding(16.dp)
            .testTag("deck_manager_screen")
    ) {
        // Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "BATTLE SQUAD LINEUP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Your 3 active heroes deployed into duels",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SolarGold.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, SolarGold)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Power",
                        tint = SolarGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$teamPower CP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = SolarGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Team Power Summary Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "TEAM STAT SYNERGY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberCyan,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatMetric("TEAM HP", "$totalHp", StatHp)
                    StatMetric("TOTAL ATK", "$totalAtk", StatAtk)
                    StatMetric("TOTAL DEF", "$totalDef", StatDef)
                }
                Spacer(modifier = Modifier.height(10.dp))

                // Elemental Coverage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Affinities:",
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        activeDeck.forEach { card ->
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AnimeCardBg,
                                border = BorderStroke(0.8.dp, CyberCyan)
                            ) {
                                Text(
                                    text = card.element.displayName.split(" ").first(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // The 3 Slots List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val slotTitles = listOf("Slot 1: Vanguard (Leader)", "Slot 2: Striker", "Slot 3: Anchor")

            items(3) { slotIndex ->
                val card = activeDeck.getOrNull(slotIndex)

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AnimeSurface,
                    border = BorderStroke(1.dp, AnimeCardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = slotTitles[slotIndex].uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (slotIndex == 0) SolarGold else CyberCyan
                            )

                            Text(
                                text = "Tap hero to inspect",
                                fontSize = 9.sp,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (card != null) {
                            AnimeCardView(
                                card = card,
                                isCombatCompact = false,
                                isSelected = slotIndex == 0,
                                onClick = { selectedInspectCard = card }
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = AnimeCardBg,
                                border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .clickable { onNavigateToSearch() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Card",
                                            tint = CyberCyan
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Search & Assign Hero to Slot ${slotIndex + 1}",
                                            fontSize = 11.sp,
                                            color = CyberCyan
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons Row: Search More vs Test in Battle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToSearch,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, CyberCyan),
                modifier = Modifier
                    .weight(1f)
                    .testTag("forge_more_anime_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = CyberCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Search Anime", fontSize = 11.sp, color = CyberCyan, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNavigateToBattle,
                colors = ButtonDefaults.buttonColors(containerColor = NeonCrimson),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("launch_duel_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SportsKabaddi,
                    contentDescription = "Duel",
                    tint = TextPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Enter Duel", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Black)
            }
        }
    }

    selectedInspectCard?.let { card ->
        CardDetailDialog(
            card = card,
            onDismiss = { selectedInspectCard = null },
            onAssignToSlot = { slot ->
                cardRepo.setCardInActiveDeck(slot, card)
            }
        )
    }
}

@Composable
private fun StatMetric(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = AnimeCardBg
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = TextPrimary)
        }
    }
}
