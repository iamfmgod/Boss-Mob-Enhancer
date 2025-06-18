Define your scaling curves in JSON using the format:

  {
    "ProfileName": {
      "healthWeight": decimal (e.g. 1.0),
      "playerCountWeight": decimal (e.g. 1.5),
      "maxTier": integer from 1 to 5
    }
  }

Custom profiles must match the name defined in the config.cfg under:
    Difficulty Curve Preset = Raider   ← for example

This file is reloaded at launch. No restarts needed if you're using auto-reload tools like CraftTweaker or ModMenu extensions.


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