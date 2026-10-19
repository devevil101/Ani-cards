package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AnimeCard
import com.example.data.model.CardElement
import com.example.data.model.CardRarity
import com.example.ui.theme.AnimeCardBg
import com.example.ui.theme.AnimeDarkBg
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.ElementDark
import com.example.ui.theme.ElementFire
import com.example.ui.theme.ElementLight
import com.example.ui.theme.ElementLightning
import com.example.ui.theme.ElementWater
import com.example.ui.theme.ElementWind
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.SolarGold
import com.example.ui.theme.StatAtk
import com.example.ui.theme.StatDef
import com.example.ui.theme.StatHp
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

fun getElementColor(element: CardElement): Color {
    return when (element) {
        CardElement.FIRE -> ElementFire
        CardElement.WATER -> ElementWater
        CardElement.LIGHTNING -> ElementLightning
        CardElement.DARK -> ElementDark
        CardElement.LIGHT -> ElementLight
        CardElement.WIND -> ElementWind
    }
}

@Composable
fun AnimeCardView(
    card: AnimeCard,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isCombatCompact: Boolean = false,
    onClick: () -> Unit = {}
) {
    val elementColor = getElementColor(card.element)
    val infiniteTransition = rememberInfiniteTransition(label = "card_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    val borderStroke = when {
        isSelected -> BorderStroke(2.5.dp, CyberCyan)
        card.isFainted -> BorderStroke(1.dp, TextMuted.copy(alpha = 0.4f))
        card.rarity == CardRarity.SSR -> BorderStroke(1.5.dp, SolarGold.copy(alpha = glowAlpha))
        else -> BorderStroke(1.dp, elementColor.copy(alpha = 0.7f))
    }

    Card(
        modifier = modifier
            .testTag("anime_card_${card.name.replace(" ", "_")}")
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (card.isFainted) AnimeDarkBg.copy(alpha = 0.6f) else AnimeCardBg
        ),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCombatCompact) 6.dp else 10.dp)
        ) {
            // Header: Element Tag & Rarity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = elementColor.copy(alpha = 0.2f),
                    border = BorderStroke(0.8.dp, elementColor)
                ) {
                    Text(
                        text = card.element.displayName.split(" ").first(),
                        color = elementColor,
                        fontSize = if (isCombatCompact) 9.sp else 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (card.rarity) {
                        CardRarity.SSR -> SolarGold.copy(alpha = 0.2f)
                        CardRarity.SR -> SpiritPurple(0.2f)
                        CardRarity.R -> CyberCyan.copy(alpha = 0.2f)
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rarity",
                            tint = when (card.rarity) {
                                CardRarity.SSR -> SolarGold
                                CardRarity.SR -> SpiritPurple(1.0f)
                                CardRarity.R -> CyberCyan
                            },
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = card.rarity.name,
                            fontSize = if (isCombatCompact) 9.sp else 10.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Portrait Box with Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCombatCompact) 70.dp else 115.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeDarkBg)
            ) {
                AsyncImage(
                    model = card.imageUrl,
                    contentDescription = card.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Bottom gradient for legibility
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, AnimeDarkBg.copy(alpha = 0.85f)),
                                startY = 50f
                            )
                        )
                )

                if (card.isFainted) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "FAINTED",
                            color = NeonCrimson,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp
                        )
                    }
                }

                // Shield Badge if active
                if (card.shield > 0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = CyberCyan.copy(alpha = 0.85f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = "Shield",
                                tint = Color.Black,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = "${card.shield}",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Character Name & Anime Subtitle
            Text(
                text = card.name,
                fontWeight = FontWeight.Bold,
                fontSize = if (isCombatCompact) 11.sp else 13.sp,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = card.animeTitle,
                fontSize = if (isCombatCompact) 9.sp else 10.sp,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // HP Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "HP",
                        fontSize = 9.sp,
                        color = StatHp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${card.currentHp}/${card.maxHp}",
                        fontSize = 9.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
                LinearProgressIndicator(
                    progress = { card.hpPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = if (card.hpPercentage > 0.4f) StatHp else NeonCrimson,
                    trackColor = AnimeDarkBg,
                )
            }

            // Ultimate Gauge (if in combat)
            if (isCombatCompact) {
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ULT",
                        fontSize = 8.sp,
                        color = SolarGold,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${card.ultimateGauge}%",
                        fontSize = 8.sp,
                        color = SolarGold,
                        fontWeight = FontWeight.Bold
                    )
                }
                LinearProgressIndicator(
                    progress = { card.ultimatePercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(CircleShape),
                    color = SolarGold,
                    trackColor = AnimeDarkBg,
                )
            } else {
                // Regular view: Stats pill row (ATK, DEF, SPD)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatChip(label = "ATK", value = "${card.attack}", color = StatAtk)
                    StatChip(label = "DEF", value = "${card.defense}", color = StatDef)
                    StatChip(label = "SPD", value = "${card.speed}", color = CyberCyan)
                }
            }
        }
    }
}

@Composable
private fun StatChip(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = color
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = value,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}

private fun SpiritPurple(alpha: Float): Color = Color(0xFFA855F7).copy(alpha = alpha)
