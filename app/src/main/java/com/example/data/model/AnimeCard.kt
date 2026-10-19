package com.example.data.model

enum class CardElement(val displayName: String) {
    FIRE("Fire / Ki"),
    WATER("Water / Chakra"),
    LIGHTNING("Lightning / Nen"),
    DARK("Dark / Curse"),
    LIGHT("Light / Spirit"),
    WIND("Wind / Blade");

    fun getAdvantageAgainst(): CardElement {
        return when (this) {
            FIRE -> WIND
            WIND -> LIGHTNING
            LIGHTNING -> WATER
            WATER -> FIRE
            DARK -> LIGHT
            LIGHT -> DARK
        }
    }
}

enum class CardRarity(val label: String, val stars: Int) {
    R("Rare", 3),
    SR("Super Rare", 4),
    SSR("Mythic SSR", 5)
}

data class AnimeCard(
    val id: String,
    val name: String,
    val animeTitle: String,
    val imageUrl: String,
    val role: String = "Main",
    val element: CardElement = CardElement.FIRE,
    val rarity: CardRarity = CardRarity.SSR,
    val maxHp: Int = 2500,
    val currentHp: Int = 2500,
    val attack: Int = 600,
    val defense: Int = 400,
    val speed: Int = 80,
    val shield: Int = 0,
    val ultimateGauge: Int = 0, // 0 to 100
    val isAwakened: Boolean = false,
    val loreQuote: String = "I won't give up!",
    val skills: List<CardSkill> = emptyList()
) {
    val isFainted: Boolean
        get() = currentHp <= 0

    val hpPercentage: Float
        get() = (currentHp.toFloat() / maxHp.toFloat()).coerceIn(0f, 1f)

    val ultimatePercentage: Float
        get() = (ultimateGauge.toFloat() / 100f).coerceIn(0f, 1f)
}
