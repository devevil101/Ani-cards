package com.example.data.preset

import com.example.data.model.AnimeCard
import com.example.data.model.AnimeSeries
import com.example.data.model.CardElement
import com.example.data.model.CardRarity
import com.example.data.model.CardSkill
import com.example.data.model.SkillType

object CuratedPresets {

    val trendingAnime = listOf(
        AnimeSeries(
            id = 813,
            title = "Dragon Ball Z",
            imageUrl = "https://cdn.myanimelist.net/images/anime/1607/117271.jpg",
            synopsis = "Five years after winning the World Martial Arts tournament, Gokuu is living a peaceful life when a stranger named Raditz arrives.",
            score = 8.16,
            genres = listOf("Action", "Adventure", "Fantasy"),
            year = 1989
        ),
        AnimeSeries(
            id = 40748,
            title = "Jujutsu Kaisen",
            imageUrl = "https://cdn.myanimelist.net/images/anime/1171/109222.jpg",
            synopsis = "Idly indulging in paranormal activities with the Occult Club, high schooler Yuuji Itadori spends his days at either the clubroom or the hospital.",
            score = 8.61,
            genres = listOf("Action", "Fantasy", "Supernatural"),
            year = 2020
        ),
        AnimeSeries(
            id = 21,
            title = "One Piece",
            imageUrl = "https://cdn.myanimelist.net/images/anime/1244/138851.jpg",
            synopsis = "Barely surviving in a barrel after passing through a terrible whirlpool, carefree Monkey D. Luffy sets out to become the Pirate King.",
            score = 8.73,
            genres = listOf("Action", "Adventure", "Fantasy"),
            year = 1999
        ),
        AnimeSeries(
            id = 20,
            title = "Naruto",
            imageUrl = "https://cdn.myanimelist.net/images/anime/13/17405.jpg",
            synopsis = "Moments prior to Naruto Uzumaki's birth, a huge demon known as the Kyuubi, the Nine-Tailed Fox, attacked Konohagakure.",
            score = 7.99,
            genres = listOf("Action", "Adventure", "Fantasy"),
            year = 2002
        ),
        AnimeSeries(
            id = 269,
            title = "Bleach",
            imageUrl = "https://cdn.myanimelist.net/images/anime/3/40451.jpg",
            synopsis = "Ichigo Kurosaki is an ordinary high schooler until his family is attacked by a Hollow, a corrupt spirit that seeks to devour human souls.",
            score = 7.93,
            genres = listOf("Action", "Adventure", "Supernatural"),
            year = 2004
        ),
        AnimeSeries(
            id = 52299,
            title = "Solo Leveling",
            imageUrl = "https://cdn.myanimelist.net/images/anime/1908/139041.jpg",
            synopsis = "Ten years ago, the Gate appeared and connected the real world with the realm of magic and monsters.",
            score = 8.27,
            genres = listOf("Action", "Fantasy"),
            year = 2024
        )
    )

    fun getPresetDeck(animeTitle: String): List<AnimeCard> {
        val lower = animeTitle.lowercase()
        return when {
            lower.contains("dragon ball") -> dragonBallDeck
            lower.contains("jujutsu") -> jujutsuKaisenDeck
            lower.contains("one piece") -> onePieceDeck
            lower.contains("naruto") -> narutoDeck
            lower.contains("bleach") -> bleachDeck
            lower.contains("solo leveling") -> soloLevelingDeck
            else -> generateAlgorithmicDeck(animeTitle, listOf("Hero", "Rival", "Mentor", "Ally"))
        }
    }

    val dragonBallDeck = listOf(
        AnimeCard(
            id = "db_goku",
            name = "Son Goku",
            animeTitle = "Dragon Ball Z",
            imageUrl = "https://cdn.myanimelist.net/images/characters/15/72220.jpg",
            role = "Main Protagonist",
            element = CardElement.FIRE,
            rarity = CardRarity.SSR,
            maxHp = 3600,
            currentHp = 3600,
            attack = 820,
            defense = 520,
            speed = 95,
            loreQuote = "I am the hope of the universe! I am the Super Saiyan, Son Goku!",
            skills = listOf(
                CardSkill("goku_s1", "Ki Blast Barrage", "Fires rapid ki blasts dealing 1.0x attack damage.", 1, 1.0),
                CardSkill("goku_s2", "Kamehameha", "Concentrates energy for a devastating beam dealing 1.8x damage and piercing 20% defense.", 2, 1.8),
                CardSkill("goku_s3", "Spirit Bomb / Ultra Instinct", "Channels the energy of all living beings into a cataclysmic blast dealing 3.2x damage and restoring 500 HP.", 3, 3.2, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "db_vegeta",
            name = "Vegeta",
            animeTitle = "Dragon Ball Z",
            imageUrl = "https://cdn.myanimelist.net/images/characters/11/498376.jpg",
            role = "Saiyan Prince",
            element = CardElement.LIGHTNING,
            rarity = CardRarity.SSR,
            maxHp = 3300,
            currentHp = 3300,
            attack = 860,
            defense = 480,
            speed = 90,
            loreQuote = "There is only one certainty in life: A strong man stands above and conquers all!",
            skills = listOf(
                CardSkill("veg_s1", "Galick Gun", "Unleashes an iconic purple energy cannon dealing 1.1x damage.", 1, 1.1),
                CardSkill("veg_s2", "Big Bang Attack", "Fires a concentrated ball of explosive ki dealing 1.9x damage.", 2, 1.9),
                CardSkill("veg_s3", "Final Flash", "Charges with maximum royal pride to obliterate the target dealing 3.4x critical damage.", 3, 3.4, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "db_piccolo",
            name = "Piccolo",
            animeTitle = "Dragon Ball Z",
            imageUrl = "https://cdn.myanimelist.net/images/characters/14/81418.jpg",
            role = "Namekian Master",
            element = CardElement.WIND,
            rarity = CardRarity.SR,
            maxHp = 3800,
            currentHp = 3800,
            attack = 680,
            defense = 650,
            speed = 80,
            loreQuote = "Even with the energy of the gods, you can't defeat a Namekian warrior!",
            skills = listOf(
                CardSkill("pic_s1", "Demon Ray", "Fires rapid eye lasers dealing 0.9x damage.", 1, 0.9),
                CardSkill("pic_s2", "Namekian Regeneration", "Regenerates limbs to recover 800 HP and gains 300 shield.", 2, 0.0, skillType = SkillType.HEAL),
                CardSkill("pic_s3", "Special Beam Cannon", "Pierces completely through enemy guard dealing 2.8x defense-ignoring damage.", 3, 2.8, isUltimate = true)
            )
        )
    )

    val jujutsuKaisenDeck = listOf(
        AnimeCard(
            id = "jjk_gojo",
            name = "Satoru Gojo",
            animeTitle = "Jujutsu Kaisen",
            imageUrl = "https://cdn.myanimelist.net/images/characters/14/423075.jpg",
            role = "Special Grade Sorcerer",
            element = CardElement.LIGHT,
            rarity = CardRarity.SSR,
            maxHp = 3500,
            currentHp = 3500,
            attack = 880,
            defense = 600,
            speed = 100,
            loreQuote = "Don't worry. I'm the strongest.",
            skills = listOf(
                CardSkill("gojo_s1", "Cursed Technique: Blue", "Creates attraction forces dealing 1.2x damage and pulling in enemy defense.", 1, 1.2),
                CardSkill("gojo_s2", "Reversal: Red", "Expels explosive divergent energy dealing 1.9x burst damage.", 2, 1.9),
                CardSkill("gojo_s3", "Domain Expansion: Infinite Void", "Floods the enemy's mind with infinite information, dealing 3.5x damage and stunning for 1 turn.", 3, 3.5, skillType = SkillType.STUN, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "jjk_sukuna",
            name = "Ryomen Sukuna",
            animeTitle = "Jujutsu Kaisen",
            imageUrl = "https://cdn.myanimelist.net/images/characters/3/430113.jpg",
            role = "King of Curses",
            element = CardElement.DARK,
            rarity = CardRarity.SSR,
            maxHp = 3700,
            currentHp = 3700,
            attack = 920,
            defense = 500,
            speed = 98,
            loreQuote = "Know your place, fool.",
            skills = listOf(
                CardSkill("suk_s1", "Dismantle", "An invisible slash slicing target dealing 1.2x damage.", 1, 1.2),
                CardSkill("suk_s2", "Cleave & Flame Arrow", "Adjusts slash to enemy toughness then engulfs in flame dealing 2.0x damage.", 2, 2.0),
                CardSkill("suk_s3", "Domain Expansion: Malevolent Shrine", "Unrelenting slashes in all directions dealing 3.6x catastrophic damage.", 3, 3.6, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "jjk_itadori",
            name = "Yuji Itadori",
            animeTitle = "Jujutsu Kaisen",
            imageUrl = "https://cdn.myanimelist.net/images/characters/16/423074.jpg",
            role = "Vessel of Sukuna",
            element = CardElement.FIRE,
            rarity = CardRarity.SR,
            maxHp = 4000,
            currentHp = 4000,
            attack = 740,
            defense = 580,
            speed = 92,
            loreQuote = "I want to save people, so when I die, I'll be surrounded by people who care.",
            skills = listOf(
                CardSkill("ita_s1", "Divergent Fist", "Dual impact strike dealing 1.1x damage.", 1, 1.1),
                CardSkill("ita_s2", "Soul Resonance", "Strikes the soul directly dealing 1.7x damage ignoring 30% armor.", 2, 1.7),
                CardSkill("ita_s3", "Consecutive Black Flash", "Distorts space within 0.000001 seconds unleashing 3.1x critical black lightning.", 3, 3.1, isUltimate = true)
            )
        )
    )

    val onePieceDeck = listOf(
        AnimeCard(
            id = "op_luffy",
            name = "Monkey D. Luffy",
            animeTitle = "One Piece",
            imageUrl = "https://cdn.myanimelist.net/images/characters/9/131317.jpg",
            role = "Captain / Yonko",
            element = CardElement.FIRE,
            rarity = CardRarity.SSR,
            maxHp = 3900,
            currentHp = 3900,
            attack = 850,
            defense = 520,
            speed = 92,
            loreQuote = "I'm Monkey D. Luffy, the man who's going to become the King of the Pirates!",
            skills = listOf(
                CardSkill("luf_s1", "Gomu Gomu no Pistol", "Stretches arm back to launch a high-velocity punch dealing 1.0x damage.", 1, 1.0),
                CardSkill("luf_s2", "Red Hawk", "Ignites fist with armament haki dealing 1.8x fire damage.", 2, 1.8),
                CardSkill("luf_s3", "Gear 5: Bajrang Gun", "Transforms into Sun God Nika with an island-sized fist dealing 3.5x mythical damage.", 3, 3.5, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "op_zoro",
            name = "Roronoa Zoro",
            animeTitle = "One Piece",
            imageUrl = "https://cdn.myanimelist.net/images/characters/3/100534.jpg",
            role = "Master Swordsman",
            element = CardElement.WIND,
            rarity = CardRarity.SSR,
            maxHp = 3600,
            currentHp = 3600,
            attack = 870,
            defense = 490,
            speed = 88,
            loreQuote = "Scars on the back are a swordsman's shame.",
            skills = listOf(
                CardSkill("zor_s1", "Oni Giri", "Simultaneous three-blade crossing slash dealing 1.1x damage.", 1, 1.1),
                CardSkill("zor_s2", "Sanzen Sekai", "Spins dual swords like windmills delivering 1.8x cutting damage.", 2, 1.8),
                CardSkill("zor_s3", "King of Hell: Ashura", "Summons nine-sword demon aura dealing 3.3x devastating strike.", 3, 3.3, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "op_sanji",
            name = "Vinsmoke Sanji",
            animeTitle = "One Piece",
            imageUrl = "https://cdn.myanimelist.net/images/characters/5/136769.jpg",
            role = "Black Leg Chef",
            element = CardElement.FIRE,
            rarity = CardRarity.SR,
            maxHp = 3400,
            currentHp = 3400,
            attack = 790,
            defense = 530,
            speed = 96,
            loreQuote = "I refuse to let anyone insult my cooking or touch a lady!",
            skills = listOf(
                CardSkill("san_s1", "Collier Strike", "Flurry of pinpoint kicks dealing 1.0x damage.", 1, 1.0),
                CardSkill("san_s2", "Diable Jambe", "Heats legs with passion to deal 1.7x flame kick damage.", 2, 1.7),
                CardSkill("san_s3", "Ifrit Jambe: Boeuf Burst", "Ignites blue lightning flames dealing 3.1x sonic impact.", 3, 3.1, isUltimate = true)
            )
        )
    )

    val narutoDeck = listOf(
        AnimeCard(
            id = "nar_naruto",
            name = "Naruto Uzumaki",
            animeTitle = "Naruto",
            imageUrl = "https://cdn.myanimelist.net/images/characters/2/284123.jpg",
            role = "Seventh Hokage",
            element = CardElement.WIND,
            rarity = CardRarity.SSR,
            maxHp = 4200,
            currentHp = 4200,
            attack = 830,
            defense = 550,
            speed = 90,
            loreQuote = "I'm not gonna run away, I never go back on my word! That's my nindo: my ninja way!",
            skills = listOf(
                CardSkill("nar_s1", "Shadow Clone Combo", "Surrounds foe with dozens of clones dealing 1.0x damage.", 1, 1.0),
                CardSkill("nar_s2", "Rasenshuriken", "Wind chakra sphere spinning at microscopic blades dealing 1.9x damage.", 2, 1.9),
                CardSkill("nar_s3", "Kurama Link / Super Bijuu Bomb", "Unleashes full Nine-Tails avatar dealing 3.4x catastrophic energy blast.", 3, 3.4, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "nar_sasuke",
            name = "Sasuke Uchiha",
            animeTitle = "Naruto",
            imageUrl = "https://cdn.myanimelist.net/images/characters/9/131317.jpg",
            role = "Shadow Hokage",
            element = CardElement.LIGHTNING,
            rarity = CardRarity.SSR,
            maxHp = 3500,
            currentHp = 3500,
            attack = 890,
            defense = 470,
            speed = 99,
            loreQuote = "I have long since closed my eyes... My only goal is in the darkness.",
            skills = listOf(
                CardSkill("sas_s1", "Chidori", "One thousand birds lightning blade dealing 1.2x damage.", 1, 1.2),
                CardSkill("sas_s2", "Amaterasu", "Black inextinguishable flames dealing 1.9x persistent burn damage.", 2, 1.9),
                CardSkill("sas_s3", "Indra's Arrow / Susanoo", "Fires lightning arrow from Complete Body Susanoo dealing 3.5x damage.", 3, 3.5, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "nar_kakashi",
            name = "Kakashi Hatake",
            animeTitle = "Naruto",
            imageUrl = "https://cdn.myanimelist.net/images/characters/7/284124.jpg",
            role = "Copy Ninja",
            element = CardElement.LIGHTNING,
            rarity = CardRarity.SR,
            maxHp = 3300,
            currentHp = 3300,
            attack = 760,
            defense = 520,
            speed = 94,
            loreQuote = "Those who break the rules are scum, but those who abandon their friends are worse than scum.",
            skills = listOf(
                CardSkill("kak_s1", "Lightning Blade", "Precision thrust dealing 1.1x damage.", 1, 1.1),
                CardSkill("kak_s2", "Earth Style: Mud Wall", "Erects solid rock wall granting 600 shield to self.", 1, 0.0, skillType = SkillType.SHIELD),
                CardSkill("kak_s3", "Kamui Warp", "Distorts space-time to banish target dealing 3.0x spatial damage.", 3, 3.0, isUltimate = true)
            )
        )
    )

    val bleachDeck = listOf(
        AnimeCard(
            id = "bl_ichigo",
            name = "Ichigo Kurosaki",
            animeTitle = "Bleach",
            imageUrl = "https://cdn.myanimelist.net/images/characters/3/495893.jpg",
            role = "Substitute Soul Reaper",
            element = CardElement.DARK,
            rarity = CardRarity.SSR,
            maxHp = 3700,
            currentHp = 3700,
            attack = 860,
            defense = 500,
            speed = 97,
            loreQuote = "I'm not fighting because I want to win. I'm fighting because I have to protect them!",
            skills = listOf(
                CardSkill("ich_s1", "Zangetsu Slash", "Heavy spiritual sword swing dealing 1.0x damage.", 1, 1.0),
                CardSkill("ich_s2", "Getsuga Tensho", "Releases crescent wave of spiritual pressure dealing 1.8x damage.", 2, 1.8),
                CardSkill("ich_s3", "Bankai: Tensa Zangetsu & Mugetsu", "Transcends limits into Final Getsuga Tensho dealing 3.5x void damage.", 3, 3.5, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "bl_aizen",
            name = "Sosuke Aizen",
            animeTitle = "Bleach",
            imageUrl = "https://cdn.myanimelist.net/images/characters/5/74404.jpg",
            role = "Former Captain",
            element = CardElement.LIGHT,
            rarity = CardRarity.SSR,
            maxHp = 3800,
            currentHp = 3800,
            attack = 910,
            defense = 580,
            speed = 95,
            loreQuote = "No one stands on the top of the world. Not you, not me, not even gods. But that will end.",
            skills = listOf(
                CardSkill("aiz_s1", "Hado #90: Kurohitsugi", "Envelopes target in black coffin dealing 1.3x dark damage.", 1, 1.3),
                CardSkill("aiz_s2", "Kyoka Suigetsu: Complete Hypnosis", "Distorts enemy perceptions, granting 500 shield and confusing enemy.", 2, 0.0, skillType = SkillType.SHIELD),
                CardSkill("aiz_s3", "Hogyoku Transcendence", "Unlocks absolute divine evolution dealing 3.6x reality-shattering damage.", 3, 3.6, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "bl_urahara",
            name = "Kisuke Urahara",
            animeTitle = "Bleach",
            imageUrl = "https://cdn.myanimelist.net/images/characters/13/284126.jpg",
            role = "Shopkeeper & Scientist",
            element = CardElement.FIRE,
            rarity = CardRarity.SR,
            maxHp = 3400,
            currentHp = 3400,
            attack = 780,
            defense = 540,
            speed = 93,
            loreQuote = "There is nothing, but nothing I wouldn't do to protect the balance of worlds.",
            skills = listOf(
                CardSkill("ura_s1", "Nake, Benihime", "Fires crimson energy blade dealing 1.1x damage.", 1, 1.1),
                CardSkill("ura_s2", "Chikasumi no Tate", "Forms hexagonal shield absorbing 700 damage.", 1, 0.0, skillType = SkillType.SHIELD),
                CardSkill("ura_s3", "Bankai: Kannonbiraki Benihime", "Restructures the battlefield stitching and rebuilding allies while dealing 3.1x damage.", 3, 3.1, isUltimate = true)
            )
        )
    )

    val soloLevelingDeck = listOf(
        AnimeCard(
            id = "sl_jinwoo",
            name = "Sung Jin-woo",
            animeTitle = "Solo Leveling",
            imageUrl = "https://cdn.myanimelist.net/images/characters/11/498410.jpg",
            role = "Shadow Monarch",
            element = CardElement.DARK,
            rarity = CardRarity.SSR,
            maxHp = 4100,
            currentHp = 4100,
            attack = 940,
            defense = 520,
            speed = 100,
            loreQuote = "Arise.",
            skills = listOf(
                CardSkill("jin_s1", "Dagger Rush / Bloodlust", "Vicious twin daggers striking vital spots dealing 1.2x damage.", 1, 1.2),
                CardSkill("jin_s2", "Ruler's Authority", "Invisible telekinetic force slamming enemy dealing 1.8x damage.", 2, 1.8),
                CardSkill("jin_s3", "Domain of the Monarch: Arise", "Summons thousands of shadow soldiers dealing 3.7x total field destruction.", 3, 3.7, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "sl_cha",
            name = "Cha Hae-In",
            animeTitle = "Solo Leveling",
            imageUrl = "https://cdn.myanimelist.net/images/characters/15/498412.jpg",
            role = "S-Rank Hunter",
            element = CardElement.LIGHT,
            rarity = CardRarity.SSR,
            maxHp = 3300,
            currentHp = 3300,
            attack = 830,
            defense = 480,
            speed = 98,
            loreQuote = "My blade shines for those I protect.",
            skills = listOf(
                CardSkill("cha_s1", "Sword Dance", "Elegantly slashes multiple times dealing 1.1x damage.", 1, 1.1),
                CardSkill("cha_s2", "Light of the Blade", "Infuses rapier with blinding radiant light dealing 1.8x damage.", 2, 1.8),
                CardSkill("cha_s3", "Quake of the Sword", "Drives blade into ground sending shockwaves dealing 3.2x holy damage.", 3, 3.2, isUltimate = true)
            )
        ),
        AnimeCard(
            id = "sl_igris",
            name = "Blood-Red Commander Igris",
            animeTitle = "Solo Leveling",
            imageUrl = "https://cdn.myanimelist.net/images/characters/12/502123.jpg",
            role = "Shadow Knight",
            element = CardElement.LIGHTNING,
            rarity = CardRarity.SR,
            maxHp = 3900,
            currentHp = 3900,
            attack = 780,
            defense = 640,
            speed = 85,
            loreQuote = "My lord's will is absolute.",
            skills = listOf(
                CardSkill("igr_s1", "Greatsword Cleave", "Sweeping slash dealing 1.0x damage.", 1, 1.0),
                CardSkill("igr_s2", "Iron Fortress", "Raises shield raising team defense and gaining 500 shield.", 1, 0.0, skillType = SkillType.SHIELD),
                CardSkill("igr_s3", "Thunderbolt Longsword", "Channels lightning down huge blade dealing 3.0x shock damage.", 3, 3.0, isUltimate = true)
            )
        )
    )

    fun generateAlgorithmicDeck(animeTitle: String, characterNames: List<String>): List<AnimeCard> {
        val elements = CardElement.entries
        return characterNames.take(5).mapIndexed { index, name ->
            val element = elements[(name.hashCode().coerceAtLeast(0) + index) % elements.size]
            val rarity = when (index) {
                0 -> CardRarity.SSR
                1 -> CardRarity.SSR
                2 -> CardRarity.SR
                else -> CardRarity.R
            }
            val hp = 3000 + (name.length * 70).coerceIn(0, 1000) + (if (rarity == CardRarity.SSR) 400 else 0)
            val atk = 650 + (name.hashCode().coerceAtLeast(0) % 250) + (if (rarity == CardRarity.SSR) 100 else 0)
            val def = 420 + (name.length * 20).coerceIn(0, 200)
            val spd = 75 + (index * 5)

            AnimeCard(
                id = "${animeTitle.replace(" ", "_")}_${index}_$name",
                name = name,
                animeTitle = animeTitle,
                imageUrl = "https://cdn.myanimelist.net/images/characters/15/72220.jpg",
                role = if (index == 0) "Protagonist" else if (index == 1) "Rival" else "Ally",
                element = element,
                rarity = rarity,
                maxHp = hp,
                currentHp = hp,
                attack = atk,
                defense = def,
                speed = spd,
                loreQuote = "I fight with the power of $animeTitle!",
                skills = listOf(
                    CardSkill(
                        id = "${name}_s1",
                        name = "$name Strike",
                        description = "Quick strike dealing 1.0x attack damage.",
                        energyCost = 1,
                        powerMultiplier = 1.0
                    ),
                    CardSkill(
                        id = "${name}_s2",
                        name = "$name Secret Art",
                        description = "Signature anime technique dealing 1.8x elemental damage.",
                        energyCost = 2,
                        powerMultiplier = 1.8
                    ),
                    CardSkill(
                        id = "${name}_s3",
                        name = "$name Awakened Ultimate",
                        description = "Maximum output special move dealing 3.3x cataclysmic damage.",
                        energyCost = 3,
                        powerMultiplier = 3.3,
                        isUltimate = true
                    )
                )
            )
        }
    }
}
