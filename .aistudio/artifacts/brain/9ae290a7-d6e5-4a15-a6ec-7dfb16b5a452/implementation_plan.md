# Anime Clash: Tactical Deck Battler

An immersive tactical card battle Android application where players can search for any anime universe (e.g. Dragon Ball, Naruto, One Piece, Jujutsu Kaisen, Bleach, Solo Leveling), instantly generate or customize a deck of that anime's characters with lore-accurate stats and abilities, and engage in tactical turn-based card battles across Offline Bot battles and Online Ranked matchmaking.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following user preferences were confirmed during the clarification interview:
> - **Anime Deck & Stats Generation**: Hybrid approach combining live Jikan / MyAnimeList search (for real characters, artwork, roles, and descriptions) with Gemini AI & smart algorithmic stat-scaling to ensure lore-accurate HP, ATK, DEF, SPD, elemental affinities, skills, and cinematic Ultimate moves.
> - **Combat Style**: Tactical turn-based battle arena featuring dynamic Energy (Ki/Chakra/Mana) management, character positioning, normal attacks, signature tactical skills, defensive stances, and high-impact ultimate gauge triggers.
> - **Game Modes**: Both an **Offline Mode** (vs Adaptive AI bots across difficulty tiers and Boss Trials) and an **Online Mode** with a toggle for **Casual vs Ranked Matchmaking**, Elo-based Rank Points (RP), tier badges (Bronze to Anime Master), and global leaderboards.

- **Confirmed Decision 1**: Live anime catalog search with instant deck generation + Gemini AI stat balancing and local fallback cache so the game is immediately playable offline or online.
- **Confirmed Decision 2**: Deep tactical turn-based combat with 3v3 active anime hero battles, dynamic energy points, status effects (Burn, Stun, Shield, Pierce, Bleed), and cinematic Awakened Ultimates.
- **Confirmed Decision 3**: Dual game modes: Offline Arena (Practice, Quick Bot Duel, Anime Gauntlet) and Online Arena (Live Matchmaking Queue with Ranked/Casual toggle, RP progression, and Leaderboard).

---

## 1. Overview & Core Concept

### What It Does
*Anime Clash* lets anime fans search for any anime series in existence. The app queries the live anime database to extract key characters and portraits, then runs them through a lore-accurate stat generator (using Gemini AI with a smart deterministic fallback engine) to assign attributes (HP, ATK, DEF, SPD, Element/Affinity), signature skills (e.g., Rasengan, Getsuga Tensho, Serious Punch, Hollow Purple), and game-changing Ultimate abilities. Players construct custom decks or use instant universe decks to duel against challenging AI bots or jump into the online matchmaking queue to climb the ranked ladder.

### Target Audience / Persona
Anime enthusiasts, competitive mobile card game players (Hearthstone, Yu-Gi-Oh, Shadowverse, Marvel Snap fans), and casual gamers who love dream crossovers (e.g., Goku & Gojo vs Naruto & Luffy).

### Key Value
- **Zero-Barrier Roster**: Search literally any anime; you are not restricted to a fixed small roster.
- **Lore-Accurate Tactical Combat**: Characters genuinely feel like their anime counterparts through their unique mechanics, energy costs, and ultimate cut-ins.
- **Complete Playability Anywhere**: Robust offline bot AI with multiple difficulties ensures offline fun, while online ranked matchmaking provides competitive thrill.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **Search & Deck Forge**:
   - Player types an anime name (or taps trending universes like *Dragon Ball Z*, *Jujutsu Kaisen*, *One Piece*, *Bleach*).
   - Live anime cards appear with poster art, score, and synopsis.
   - Player taps "Generate Universe Deck": characters are fetched with portraits and synthesized into combat cards with lore stats, elements, and 3 skills.
   - Player inspects character cards, filters by rarity/element, and saves their favorite active 3-to-5 character battle deck.
2. **Offline Battle Mode**:
   - Player selects "Solo / Offline", chooses Bot difficulty (Apprentice, Master, Legendary Boss) or selects an opponent Anime Universe.
   - Battlefield loads with custom anime arena backdrop, player hero cards on bottom, opponent cards on top, turn tracker, and energy counters.
   - Player spends energy to cast basic attacks, tactical skills, or charges their Ultimate meter. Turn passes to AI with real-time battle log and damage floaters.
3. **Online Ranked Matchmaking**:
   - Player switches to "Online Arena", toggles between **Casual Match** and **Ranked Match**.
   - Queue search simulates/connects matchmaking based on player's current Rank Points (e.g., Gold II, 1450 RP).
   - Matched screen reveals opponent's anime deck and player profile. Duel begins with turn timers, tactical plays, and rank points awarded or deducted on victory/defeat.
4. **Leaderboard & Profile**:
   - Player tracks their Win Rate, Match History, highest rank tier badge, and global leaderboards.

### Visual Identity & Theme
- **Aesthetic Direction**: High-energy, modern anime battle interface with dark luxury sci-fi/fantasy tones, neon elemental accents, holographic card borders, and cinematic visual impact.
- **Color Palette**:
  - *Deep Void Background*: `#0D0F18` / `#131625`
  - *Electric Cyan (Energy/Ki)*: `#00E5FF`
  - *Neon Crimson (Power/Attack)*: `#FF2A6D`
  - *Solar Gold (Ranked/Ultimate)*: `#FFD700`
  - *Spirit Violet (Special Moves/Curse)*: `#9D4EDD`
  - *Card Surface*: `#1A1E34` with subtle glowing borders based on character element
- **Typography**: Clean, bold athletic display headers paired with crisp high-legibility stat values and skill descriptions.
- **Interactive Feedback**: Dynamic card hover/selection elevations, attack slash animations, damage numbers popping on hit, screen shake on critical hits, and full-screen visual banners when an Ultimate is unleashed.

---

## 3. Key Product Decisions & Trade-Offs

### Decision 1: Hybrid Live Jikan API + Gemini AI + Deterministic Fallback
- **Approach**: Fetch live anime and character portraits from Jikan REST API. When generating combat stats and skills, run a structured Gemini prompt (`gemini-3.5-flash`) for authentic anime abilities. If offline or if the API key is not yet set in AI Studio Secrets, fall back to a built-in algorithmic stat-engine and curated presets.
- **Why**: Ensures 100% reliability. The user can start playing immediately on first launch without mandatory configuration, yet gains unlimited anime depth with real live search and AI-powered lore generation.

### Decision 2: 3v3 Active Tactical Combat System
- **Approach**: Each player selects a team of 3 active heroes (with bench reserves). Each turn grants Energy points (starting at 2, scaling up to 6). Players can activate normal attacks, defense guards, character tactical skills, or when the 100% Ultimate gauge is filled, trigger devastating awakened ultimates.
- **Why**: Much more engaging and strategic than simple automated stat comparison, while fitting smoothly into mobile portrait screens without visual clutter.

### Decision 3: Offline Bot Engine & Online Ranked System
- **Approach**: 
  - Offline: Heuristic AI bot that evaluates board state, target priority (lowest HP / highest threat), and combo potential.
  - Online: Matchmaking queue system with Casual/Ranked toggle, rank tiers (Bronze, Silver, Gold, Platinum, Diamond, Anime Master), dynamic matchmaking simulation/sync, match history, and Elo updates.
- **Why**: Directly satisfies the user's explicit request for offline bot battles and an online mode with ranked toggle.

---

## 4. Technical Architecture & Data Strategy

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├─────────────────┬──────────────────────┬───────────────────────────────┤
│  Deck Builder   │  Combat Arena View   │   Online / Ranked Lobby       │
│  • Search Bar   │  • Opponent Board    │   • Matchmaking Queue         │
│  • Anime Grid   │  • Battlefield/Logs  │   • Casual / Ranked Toggle    │
│  • Card Details │  • Player Hand/Energy│   • RP & Leaderboard Dialog   │
│  • Deck Stash   │  • Ultimate Cut-in   │   • Player Stats & Badges     │
└────────┬────────┴──────────┬───────────┴───────────────┬───────────────┘
         │                   │                           │
         ▼                   ▼                           ▼
┌────────────────────────────────────────────────────────────────────────┐
│                     ViewModel & State Managers                         │
│  • DeckViewModel (Search, Universe Fetch, AI Generation, Deck Save)    │
│  • BattleViewModel (Turns, Energy, Damage Calc, Status Effects, AI)    │
│  • RankedViewModel (Matchmaking, RP, Tiers, Match History)             │
└────────┬───────────────────┬───────────────────────────┬───────────────┘
         │                   │                           │
         ▼                   ▼                           ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        Data & Repository Layer                         │
├──────────────────────────┬───────────────────────────┬─────────────────┤
│    Anime API Client      │    Gemini AI Service      │   Persistence   │
│ • Jikan V4 REST Client   │ • Model: gemini-3.5-flash │ • Local Room/   │
│ • Character Extraction   │ • Lore Stat Generation    │   State Repos   │
│ • Image URL Resolution   │ • Deterministic Fallback  │ • Decks & Stats │
└──────────────────────────┴───────────────────────────┴─────────────────┘
```

### Data Models & State
- **AnimeSeries**: `id`, `title`, `imageUrl`, `synopsis`, `score`, `genres`
- **AnimeCard**: `id`, `name`, `animeTitle`, `imageUrl`, `role`, `element` (Fire, Water, Lightning, Dark, Light, Wind), `rarity` (SSR, SR, R), `maxHp`, `currentHp`, `attack`, `defense`, `speed`, `energyCost`, `ultimateGauge`, `skills` (List of `CardSkill`)
- **CardSkill**: `id`, `name`, `description`, `energyCost`, `powerMultiplier`, `skillType` (Attack, Heal, Shield, Buff, Debuff), `isUltimate`
- **BattleState**: `playerDeck`, `opponentDeck`, `activePlayerIndex`, `activeOpponentIndex`, `turnNumber`, `currentTurn` (Player / Opponent), `playerEnergy`, `opponentEnergy`, `battleLogs`, `battlePhase` (SelectAction, Animating, Victory, Defeat)
- **RankedProfile**: `playerName`, `rankTier`, `rankPoints`, `wins`, `losses`, `winStreak`, `matchHistory`
