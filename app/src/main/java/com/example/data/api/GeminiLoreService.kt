package com.example.data.api

import com.example.BuildConfig
import com.example.data.model.AnimeCard
import com.example.data.model.CardElement
import com.example.data.model.CardRarity
import com.example.data.model.CardSkill
import com.example.data.model.SkillType
import com.example.data.preset.CuratedPresets
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiLoreService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {

    suspend fun enhanceDeckWithAi(
        animeTitle: String,
        characters: List<Pair<String, String?>> // Pair(name, imageUrl)
    ): List<AnimeCard> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Instant smart fallback using anime knowledge
            return@withContext fallbackDeck(animeTitle, characters)
        }

        try {
            val charNames = characters.take(5).joinToString(", ") { it.first }
            val prompt = """
                You are a master anime game designer. Generate card stats for these characters from the anime "$animeTitle":
                Characters: $charNames

                Respond ONLY with a valid JSON array of objects. Each object must have:
                - "name": character name
                - "element": one of ["FIRE", "WATER", "LIGHTNING", "DARK", "LIGHT", "WIND"]
                - "rarity": one of ["SSR", "SR", "R"]
                - "hp": integer between 2800 and 4200
                - "attack": integer between 650 and 950
                - "defense": integer between 400 and 650
                - "speed": integer between 75 and 100
                - "loreQuote": famous quote or battle cry from anime
                - "skill1Name": basic attack or ki move
                - "skill1Desc": short 10-word description
                - "skill2Name": signature tactical technique
                - "skill2Desc": short 10-word description
                - "ultimateName": famous awakened ultimate move / domain / bankai
                - "ultimateDesc": short 10-word description
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contents = JSONArray().apply {
                    put(JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", prompt)
                            })
                        }
                        put("parts", parts)
                    })
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext fallbackDeck(animeTitle, characters)
            }

            val responseText = response.body?.string() ?: return@withContext fallbackDeck(animeTitle, characters)
            val jsonRoot = JSONObject(responseText)
            val candidates = jsonRoot.optJSONArray("candidates") ?: return@withContext fallbackDeck(animeTitle, characters)
            val firstCandidate = candidates.optJSONObject(0) ?: return@withContext fallbackDeck(animeTitle, characters)
            val content = firstCandidate.optJSONObject("content") ?: return@withContext fallbackDeck(animeTitle, characters)
            val parts = content.optJSONArray("parts") ?: return@withContext fallbackDeck(animeTitle, characters)
            val textPart = parts.optJSONObject(0)?.optString("text") ?: return@withContext fallbackDeck(animeTitle, characters)

            val cleanedJson = textPart.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val array = JSONArray(cleanedJson)
            val resultCards = mutableListOf<AnimeCard>()

            for (i in 0 until array.length().coerceAtMost(characters.size)) {
                val obj = array.getJSONObject(i)
                val charPair = characters.getOrNull(i)
                val name = obj.optString("name", charPair?.first ?: "Hero")
                val imgUrl = charPair?.second ?: "https://cdn.myanimelist.net/images/characters/15/72220.jpg"
                val elementStr = obj.optString("element", "FIRE")
                val element = try { CardElement.valueOf(elementStr) } catch (_: Exception) { CardElement.FIRE }
                val rarityStr = obj.optString("rarity", if (i == 0) "SSR" else "SR")
                val rarity = try { CardRarity.valueOf(rarityStr) } catch (_: Exception) { CardRarity.SSR }

                val hp = obj.optInt("hp", 3200)
                val atk = obj.optInt("attack", 750)
                val def = obj.optInt("defense", 500)
                val spd = obj.optInt("speed", 85)
                val quote = obj.optString("loreQuote", "Let's duel!")

                val s1Name = obj.optString("skill1Name", "$name Strike")
                val s1Desc = obj.optString("skill1Desc", "Basic attack dealing 1.0x damage.")
                val s2Name = obj.optString("skill2Name", "$name Signature Move")
                val s2Desc = obj.optString("skill2Desc", "Tactical skill dealing 1.8x damage.")
                val ultName = obj.optString("ultimateName", "$name Awakened Strike")
                val ultDesc = obj.optString("ultimateDesc", "Cataclysmic ultimate dealing 3.4x damage.")

                val card = AnimeCard(
                    id = "${animeTitle}_${name}_$i",
                    name = name,
                    animeTitle = animeTitle,
                    imageUrl = imgUrl,
                    role = if (i == 0) "Protagonist" else if (i == 1) "Rival" else "Ally",
                    element = element,
                    rarity = rarity,
                    maxHp = hp,
                    currentHp = hp,
                    attack = atk,
                    defense = def,
                    speed = spd,
                    loreQuote = quote,
                    skills = listOf(
                        CardSkill("${name}_s1", s1Name, s1Desc, 1, 1.0),
                        CardSkill("${name}_s2", s2Name, s2Desc, 2, 1.8),
                        CardSkill("${name}_s3", ultName, ultDesc, 3, 3.4, isUltimate = true)
                    )
                )
                resultCards.add(card)
            }

            if (resultCards.isNotEmpty()) resultCards else fallbackDeck(animeTitle, characters)
        } catch (_: Exception) {
            fallbackDeck(animeTitle, characters)
        }
    }

    private fun fallbackDeck(
        animeTitle: String,
        characters: List<Pair<String, String?>>
    ): List<AnimeCard> {
        val presets = CuratedPresets.getPresetDeck(animeTitle)
        if (presets.isNotEmpty() && !animeTitle.lowercase().contains("custom")) {
            // If we have characters from Jikan, combine character images with preset stats
            return presets.mapIndexed { index, presetCard ->
                val matchingChar = characters.getOrNull(index)
                if (matchingChar != null && !matchingChar.second.isNullOrBlank()) {
                    presetCard.copy(
                        name = matchingChar.first,
                        imageUrl = matchingChar.second!!
                    )
                } else {
                    presetCard
                }
            }
        }

        // Algorithmic generation from provided characters
        val elements = CardElement.entries
        return characters.take(5).mapIndexed { idx, pair ->
            val name = pair.first
            val img = pair.second ?: "https://cdn.myanimelist.net/images/characters/15/72220.jpg"
            val element = elements[(name.hashCode().coerceAtLeast(0) + idx) % elements.size]
            val rarity = when (idx) {
                0 -> CardRarity.SSR
                1 -> CardRarity.SSR
                2 -> CardRarity.SR
                else -> CardRarity.R
            }
            val hp = 3200 + (name.length * 60).coerceIn(0, 800)
            val atk = 700 + (name.hashCode().coerceAtLeast(0) % 200)
            val def = 460 + (name.length * 15).coerceIn(0, 150)
            val spd = 80 + idx * 4

            AnimeCard(
                id = "${animeTitle}_${name}_$idx",
                name = name,
                animeTitle = animeTitle,
                imageUrl = img,
                role = if (idx == 0) "Protagonist" else if (idx == 1) "Rival" else "Ally",
                element = element,
                rarity = rarity,
                maxHp = hp,
                currentHp = hp,
                attack = atk,
                defense = def,
                speed = spd,
                loreQuote = "I carry the spirit of $animeTitle!",
                skills = listOf(
                    CardSkill("${name}_s1", "$name Attack", "Deals 1.0x standard damage.", 1, 1.0),
                    CardSkill("${name}_s2", "$name Technique", "Signature skill dealing 1.8x damage.", 2, 1.8),
                    CardSkill("${name}_s3", "$name Ultimate Awakening", "Unleashes full potential dealing 3.3x damage.", 3, 3.3, isUltimate = true)
                )
            )
        }
    }
}
