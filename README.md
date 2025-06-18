Boss Mob Enhancer
Version: 1.0 • Minecraft 1.12.2 • Forge Compatible
Unleash legendary chaos. Boss Mob Enhancer dynamically upgrades your Minecraft mobs into elite-tier enemies with unpredictable personalities, devastating powers, and epic visual flair. Every battle becomes a unique, memorable event.
✦ Features:
- Randomized Boss Loadouts
Each enhanced mob receives 2–3 randomized abilities from a deep personality trait pool, ensuring no two bosses are ever the same.
- Tier-Based Scaling
Mob enhancements scale with health and other factors, granting higher-tier enemies more powerful abilities, buffs, and effects.
- Configurable Through Forge GUI
Fine-tune every aspect from an intuitive in-game config screen: tier weights, whitelist/blacklist mobs, module toggles, and enhancement chance.
- Dynamic Visual Effects
Particle auras, sound cues, and visual tier indicators signal a true threat. Enhanced mobs even receive fully generated, color-coded names using a custom fantasy name generator.
- Customizable Abilities
From blink-teleportation and aura debuffs to terrain-altering strikes and minion summoning—bosses have diverse, devastating arsenals.
- Mod Compatibility
Full support for modded mobs from popular content packs like Twilight Forest, Lycanite’s Mobs, and more.

🔧 Configuration:
Accessible in-game via Forge’s mod menu or manually via bossmobenhancer.cfg. Key options include
- Enhancement chance
- Max tier
- Difficulty preset or custom weights
- Buff toggles: terrain effects, minions, potion buffs
- Entity whitelist/blacklist
- Visual name tag and particle options

💡 For Mod Developers:
Boss Mob Enhancer is designed to be extensible. Easily plug in new abilities, scale logic, or UI elements. Planning a framework to expose boss profiles and NBT tags for integration with other mods or mapmakers.

Compiled on mccreator due to IDE trouble; will resolve ASAP

Made with the assistance of Sweep AI and Copilot.

BossMobEnhancer Release 2

Dramatic GUI and scaling reforms, new item added, new terrain, new particle effects, new abilities, more of a V2 really. Sorry, I could not get the nameplate toggle to work on this update. I've put all the legwork in; it's just debugging now :3

├── build.gradle
├── settings.gradle
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── bossmobenhancer
│       │               ├── MainMod.java
│       │               ├── ai
│       │               │   ├── ArmorHandler.java
│       │               │   ├── AttributeSyncer.java
│       │               │   ├── BehaviorScaler.java
│       │               │   ├── MinionSpawner.java
│       │               │   ├── ParticleEmitter.java
│       │               │   ├── SpecialAbilities.java
│       │               │   └── TerrainManipulator.java
│       │               ├── client
│       │               │   ├── BossOverlayHandler.java
│       │               │   ├── BossRegistry.java
│       │               │   ├── GuiFactory.java
│       │               │   ├── GuiConfigScreen.java         ← (Legacy/Optional)
│       │               │   ├── GuiMainConfigScreen.java       ← New: Main config screen for choosing Basic/Advanced
│       │               │   ├── GuiBasicConfigScreen.java      ← New: Basic Options config screen (e.g., Boss Behavior, Visual Effects)
│       │               │   └── GuiAdvancedConfigScreen.java   ← New: Advanced Options config screen (e.g., Scaling, Abilities, Entities, Minions)
│       │               ├── commands
│       │               │   └── BossProfileCommand.java
│       │               ├── config
│       │               │   └── ConfigHandler.java
│       │               ├── data
│       │               │   └── ScalingProfileLoader.java
│       │               ├── entities
│       │               │   └── EntityBossMinion.java
│       │               ├── events
│       │               │   ├── BossMobEnhancer.java
│       │               │   ├── PassiveMobHostilityHandler.java
│       │               │   ├── BossLootHandler.java
│       │               │   ├── BossRewardHandler.java
│       │               │   └── LordSpawnHandler.java
│       │               ├── items
│       │               │   └── ItemLunarBlessedApple.java
│       │               └── utils
│       │                   ├── EnchantmentUtils.java
│       │                   └── NameGenerator.java
│       └── resources
│           ├── META-INF
│           │   └── mods.toml
│           └── assets
│               └── bossmobenhancer
│                   ├── logo.png
│                   ├── lang
│                   │   ├── en_us.lang
│                   │   ├── fr_fr.lang
│                   │   ├── de_de.lang
│                   │   ├── ja_jp.lang
│                   │   ├── zh_cn.lang
│                   │   └── es_es.lang
│                   ├── models
│                   │   └── item
│                   │       └── lunar_blessed_apple.json
│                   └── textures
│                       └── items
│                           └── lunar_blessed_apple.png
└── config
    └── bossmobenhancer
        ├── bossmobenhancer.cfg
        ├── scaling_profiles.json
        └── README.txt
