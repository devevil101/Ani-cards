package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SportsKabaddi
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.AnimeCardRepository
import com.example.data.repository.BattleRepository
import com.example.data.repository.RankedRepository
import com.example.ui.screens.BattleScreen
import com.example.ui.screens.DeckManagerScreen
import com.example.ui.screens.OnlineRankedScreen
import com.example.ui.screens.SearchDeckScreen
import com.example.ui.theme.AnimeDarkBg
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.SolarGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class AppTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    BATTLE(
        title = "Battle",
        selectedIcon = Icons.Filled.SportsKabaddi,
        unselectedIcon = Icons.Outlined.SportsKabaddi,
        testTag = "nav_battle_tab"
    ),
    FORGE(
        title = "Anime Forge",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Outlined.Search,
        testTag = "nav_forge_tab"
    ),
    ONLINE(
        title = "Ranked",
        selectedIcon = Icons.Filled.EmojiEvents,
        unselectedIcon = Icons.Outlined.EmojiEvents,
        testTag = "nav_ranked_tab"
    ),
    LINEUP(
        title = "Lineup",
        selectedIcon = Icons.Filled.Style,
        unselectedIcon = Icons.Outlined.Style,
        testTag = "nav_lineup_tab"
    )
}

class MainActivity : ComponentActivity() {
    private val cardRepository by lazy { AnimeCardRepository() }
    private val battleRepository by lazy { BattleRepository() }
    private val rankedRepository by lazy { RankedRepository() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AnimeClashApp(
                    cardRepo = cardRepository,
                    battleRepo = battleRepository,
                    rankedRepo = rankedRepository
                )
            }
        }
    }
}

@Composable
fun AnimeClashApp(
    cardRepo: AnimeCardRepository,
    battleRepo: BattleRepository,
    rankedRepo: RankedRepository
) {
    var currentTab by remember { mutableStateOf(AppTab.BATTLE) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                color = AnimeSurface,
                border = BorderStroke(0.5.dp, AnimeDarkBg)
            ) {
                NavigationBar(
                    containerColor = AnimeSurface,
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    AppTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            modifier = Modifier.testTag(tab.testTag),
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title,
                                    modifier = Modifier.size(20.dp),
                                    tint = if (isSelected) {
                                        when (tab) {
                                            AppTab.BATTLE -> NeonCrimson
                                            AppTab.FORGE -> CyberCyan
                                            AppTab.ONLINE -> SolarGold
                                            AppTab.LINEUP -> CyberCyan
                                        }
                                    } else {
                                        TextMuted
                                    }
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) TextPrimary else TextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = AnimeDarkBg
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = AnimeDarkBg
        ) {
            when (currentTab) {
                AppTab.BATTLE -> BattleScreen(
                    battleRepo = battleRepo,
                    cardRepo = cardRepo,
                    rankedRepo = rankedRepo,
                    onNavigateToSearch = { currentTab = AppTab.FORGE }
                )
                AppTab.FORGE -> SearchDeckScreen(
                    repository = cardRepo,
                    onNavigateToBattle = { currentTab = AppTab.BATTLE }
                )
                AppTab.ONLINE -> OnlineRankedScreen(
                    rankedRepo = rankedRepo,
                    cardRepo = cardRepo,
                    battleRepo = battleRepo,
                    onNavigateToBattle = { currentTab = AppTab.BATTLE }
                )
                AppTab.LINEUP -> DeckManagerScreen(
                    cardRepo = cardRepo,
                    battleRepo = battleRepo,
                    onNavigateToSearch = { currentTab = AppTab.FORGE },
                    onNavigateToBattle = { currentTab = AppTab.BATTLE }
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 840)
@Composable
fun AnimeClashAppPreview() {
    MyApplicationTheme {
        AnimeClashApp(
            cardRepo = remember { AnimeCardRepository() },
            battleRepo = remember { BattleRepository() },
            rankedRepo = remember { RankedRepository() }
        )
    }
}

