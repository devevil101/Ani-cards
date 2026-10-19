package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CardElement
import com.example.data.model.RankTier
import com.example.data.preset.CuratedPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Anime Clash", appName)
  }

  @Test
  fun `curated presets contain valid anime decks`() {
    val dbzDeck = CuratedPresets.dragonBallDeck
    assertEquals(3, dbzDeck.size)
    assertEquals("Son Goku", dbzDeck.first().name)
    assertTrue(dbzDeck.first().skills.size >= 3)
  }

  @Test
  fun `element advantage cycle is valid`() {
    assertEquals(CardElement.WIND, CardElement.FIRE.getAdvantageAgainst())
    assertEquals(CardElement.LIGHTNING, CardElement.WIND.getAdvantageAgainst())
    assertEquals(CardElement.WATER, CardElement.LIGHTNING.getAdvantageAgainst())
    assertEquals(CardElement.FIRE, CardElement.WATER.getAdvantageAgainst())
  }

  @Test
  fun `rank tier calculation works as expected`() {
    assertEquals(RankTier.BRONZE, RankTier.fromRp(200))
    assertEquals(RankTier.SILVER, RankTier.fromRp(650))
    assertEquals(RankTier.GOLD, RankTier.fromRp(1250))
    assertEquals(RankTier.ANIME_MASTER, RankTier.fromRp(2600))
  }
}
