# Heroes Arena ⚔️

**5v5 MOBA - Clash of Legends**

A fully open-source Mobile Legends: Bang Bang-style MOBA game built with Kotlin for Android.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue)
![Android](https://img.shields.io/badge/Android-API%2024%2B-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 🎮 Features

### Core Gameplay
- **5v5 MOBA Combat** - Classic three-lane map with jungle
- **7 Unique Heroes** - Each with 4 skills + passive ability
- **6 Hero Classes** - Tank, Fighter, Assassin, Mage, Marksman, Support
- **Real-time Combat** - Touch controls with virtual joystick
- **Minion Waves** - Auto-spawning minions on 3 lanes
- **Tower Defense** - Team towers guarding each lane
- **Jungle Monsters** - River crabs, buff monsters, and Lord (boss)
- **Item Shop** - 25+ items across 6 categories with build paths
- **Level System** - Level 1-15 with stat growth per level

### Game Modes
- **Classic 5v5** - Standard MOBA match
- **Ranked Match** - Competitive with ELO ranking
- **Brawl Mode** - 3v3 fast-paced action
- **Custom Game** - Create your own rules
- **VS AI** - Practice against bots

### Heroes
| Hero | Class | Rarity | Role |
|------|-------|--------|------|
| Titan Guard | Tank | Epic | Frontline protector |
| Ocean Sovereign | Tank | Legendary | CC-heavy controller |
| Blade Dancer | Fighter | Epic | Melee damage dealer |
| Shadow Reaper | Assassin | Legendary | Burst assassin |
| Arcane Witch | Mage | Epic | Area damage mage |
| Storm Archer | Marksman | Rare | Long-range carry |
| Divine Oracle | Support | Epic | Healer/resurrector |

### Heavy UI/UX
- Animated splash screen
- Hero selection with stat bars and lore
- In-game HUD with HP/MP bars, KDA, gold, level
- Minimap with real-time tracking
- Virtual joystick controls
- Skill buttons with cooldown indicators
- Damage/heal floating numbers
- Level-up effects
- Victory/Defeat screens

### Settings
- **Graphics**: Quality presets, FPS counter, screen shake, blood effects
- **Audio**: Music, SFX, voice volume sliders
- **Controls**: Joystick size/opacity, quick cast, auto-attack, lock mode
- **Gameplay**: Recommended items, level-up notifications, death recap, minimap size

### Systems
- **Rank System** - Warrior → Elite → Master → Grandmaster → Epic → Legend → Mythic → Mythic Glory
- **Player Profile** - Stats, match history, achievements
- **Leaderboard** - Global competitive ranking
- **Item Builds** - 25+ items with build paths and passives

## 🏗️ Architecture

```
app/
├── src/main/
│   ├── java/com/heroesarena/
│   │   ├── engine/          # Game engine, loop, view, camera
│   │   ├── game/            # Game logic and systems
│   │   ├── models/          # Data models (Hero, Item, Skill, etc.)
│   │   ├── ui/              # Activities and UI components
│   │   ├── network/         # API communication
│   │   ├── audio/           # Music and sound effects
│   │   └── utils/           # Settings, helpers
│   ├── res/                 # Layouts, values, drawables
│   └── assets/              # Game data (maps, heroes, items)
└── build.gradle
```

### Tech Stack
- **Language**: Kotlin 1.9.22
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34
- **Architecture**: MVVM with Game Engine pattern
- **Rendering**: Android Canvas (SurfaceView)
- **Networking**: OkHttp 4.12
- **Serialization**: Gson 2.10
- **Image Loading**: Glide 4.16
- **UI**: Material Design Components, ViewBinding

## 🚀 Building

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 34

### Build APK
```bash
./gradlew assembleRelease
```

The APK will be at `app/build/outputs/apk/release/`

### Build for armv7a
```bash
./gradlew assembleRelease -Pabi=armeabi-v7a
```

## 📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## ⚠️ Disclaimer

This is an open-source fan project inspired by Mobile Legends: Bang Bang. It is not affiliated with, endorsed by, or connected to Moonton or any other game company. All game design and code are original.
