package com.example.data.model

enum class SkillType {
    ATTACK,
    HEAL,
    SHIELD,
    BUFF_ATK,
    STUN
}

data class CardSkill(
    val id: String,
    val name: String,
    val description: String,
    val energyCost: Int, // 1 to 3
    val powerMultiplier: Double, // e.g. 1.0x, 1.8x, 3.0x
    val skillType: SkillType = SkillType.ATTACK,
    val isUltimate: Boolean = false,
    val soundEffectHint: String = "SLASH"
)
