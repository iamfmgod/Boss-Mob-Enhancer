===============================================================================
                        Boss Mob Enhancer - README
===============================================================================

Overview:
---------
Boss Mob Enhancer is a mod designed to provide dynamic boss evolution, advanced mob AI enhancements, and custom scaling features. In addition to enhancing existing boss behavior and loot, this mod introduces new elements such as:
 - Custom boss minions and special abilities.
 - Enhanced AI for hostile and peaceful mobs.
 - A new Enshrouded Beacon that uses the standard beacon multiblock structure but applies debuffs to hostile mobs within a 128-block radius.
 - Multiple configuration screens (Basic & Advanced) and a command interface to switch scaling profiles in-game.

Configuration:
--------------
The mod is highly configurable via the "bossmobenhancer.cfg" file (located in the "config/bossmobenhancer/" directory) and the "scaling_profiles.json" file. Changes made to these files are reloaded at launch, and if you are using auto-reload tools like CraftTweaker or ModMenu, you can update settings without restarting Minecraft.

Mod Configuration (bossmobenhancer.cfg):
-------------------------------------------------
Key settings include:
  - **Difficulty Curve Preset:**
       This determines which scaling profile to use. For example:
         Difficulty Curve Preset = Raider
       *Note:* Custom profile names must exactly match the keys used in "scaling_profiles.json".

  - **Various toggles and numeric settings** that control boss enhancements, loot multipliers, abilities, terrain effects, etc.

Scaling Curves (scaling_profiles.json):
-----------------------------------------
Define your scaling curves in JSON using the following format:

{
  "ProfileName": {
    "healthWeight": decimal (e.g. 1.0),
    "playerCountWeight": decimal (e.g. 1.5),
    "maxTier": integer (allowed values: 1 to 5)
  },
  "Balanced": {
    "healthWeight": 1.0,
    "playerCountWeight": 1.0,
    "maxTier": 4
  },
  "Aggressive": {
    "healthWeight": 1.5,
    "playerCountWeight": 1.2,
    "maxTier": 4
  },
  "Brutal": {
    "healthWeight": 2.0,
    "playerCountWeight": 2.0,
    "maxTier": 4
  },
  "SkyblockFriendly": {
    "healthWeight": 0.5,
    "playerCountWeight": 0.0,
    "maxTier": 2
  },
  "SoloSurvivor": {
    "healthWeight": 0.7,
    "playerCountWeight": 0.1,
    "maxTier": 3
  },
  "Raider": {
    "healthWeight": 1.8,
    "playerCountWeight": 2.5,
    "maxTier": 4
  },
  "BerserkerRush": {
    "healthWeight": 3.0,
    "playerCountWeight": 0.0,
    "maxTier": 4
  },
  "Endwalker": {
    "healthWeight": 1.2,
    "playerCountWeight": 0.8,
    "maxTier": 5
  }
}

Important Notes:
 - Each profile's name (e.g., "Raider") must match exactly the value you enter under "Difficulty Curve Preset" in bossmobenhancer.cfg.
 - **healthWeight**: Adjusts how heavily a mob's health factors into its scaling. Higher values increase difficulty.
 - **playerCountWeight**: Adjusts scaling based on the number of players. Increase for more challenging encounters.
 - **maxTier**: The maximum boss tier allowed. Use an integer value between 1 and 5.

Changing the Profiles:
------------------------
To modify a scaling profile or add new ones:
  1. Open "scaling_profiles.json" using a text editor.
  2. Follow the JSON format as shown above. Ensure your new profile name (the key) is unique.
  3. Save the file. The mod will automatically reload these settings on launch (or use auto-reload tools if available).

Crafting & New Features:
-------------------------
- **Enshrouded Beacon:**
    - Uses the standard beacon model and multiblock structure.
    - Activation requires diamond blocks (as defined in the recipe).
    - Applies custom mob debuffs (e.g., Slowness, Weakness) to hostile mobs within a 128-block radius.
    - Configurable via the mod’s GUI screens.

- **Custom Items & Entities:**
    - The Lunar Blessed Apple is a custom item used in the Enshrouded Beacon recipe.
    - Boss minions and various boss enhancements are also configurable through the mod’s settings.

Troubleshooting:
----------------
- Ensure that all resource file paths (for textures, models, and blockstates) are lowercase.
- Validate the JSON syntax in "scaling_profiles.json", model files, and recipe files.
- If resource changes do not appear, try rebuilding the mod or clearing your resource cache.
- The mod supports auto-reload for configuration with tools such as CraftTweaker or ModMenu extensions, so changes may be applied without a full restart.

For additional details, refer to the in-mod configuration screens and tooltips provided with each feature. If you encounter any issues or wish to suggest improvements, please consult the mod’s support or community pages.

===============================================================================

BossMobEnhancer/                          // Root directory for your mod project.
├── build.gradle                          // Gradle build file.
├── settings.gradle                       // Gradle settings file.
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── example
│       │           └── bossmobenhancer
│       │               ├── MainMod.java                      // ✅ Registers blocks, items, entities, configs, GUI handler, network packets.
│       │               ├── ai                                // Boss logic and behavioral enhancements.
│       │               ├── blocks                            // Custom blocks.
│       │               │   └── BlockEnshroudedBeacon.java    // Custom beacon block with GUI activation.
│       │               ├── client
│       │               │   ├── BossOverlayHandler.java       // Renders boss health bars and HUD info.
│       │               │   ├── BossRegistry.java             // Registry for active tracked boss entities.
│       │               │   ├── GuiFactory.java               // ✅ Launches mod config screen from Mod List.
│       │               │   ├── GuiMainConfigScreen.java      // Top-level config hub.
│       │               │   ├── GuiBasicConfigScreen.java     // Toggles for boss behavior and visuals.
│       │               │   ├── GuiAdvancedConfigScreen.java  // Detailed scaling and entity control.
│       │               │   ├── GuiHandler.java               // Handles server-side GUI open/close events.
│       │               │   └── gui
│       │               │       ├── GuiEnshroudedBeacon.java  // ✅ Main curse selection GUI for the beacon.
│       │               │       └── GuiCurseButton.java       // ✅ Custom textured button with icon/hover/select visuals.
│       │               ├── commands
│       │               │   └── BossProfileCommand.java       // Switch scaling presets during gameplay.
│       │               ├── config
│       │               │   └── ConfigHandler.java            // Loads and syncs config options from disk.
│       │               ├── data
│       │               │   └── ScalingProfileLoader.java     // Loads boss behavior profiles from JSON.
│       │               ├── entities
│       │               │   └── EntityBossMinion.java         // Minion mob summoned during boss fights.
│       │               ├── events
│       │               │   ├── BossMobEnhancer.java          // Top-level gameplay hooks and triggers.
│       │               │   ├── PassiveMobHostilityHandler.java // Converts neutral mobs when cursed/named.
│       │               │   ├── BossLootHandler.java          // Controls drops from elite/boss mobs.
│       │               │   ├── BossRewardHandler.java        // Distributes custom rewards after kills.
│       │               │   └── LordSpawnHandler.java         // Spawns ultra-rare lord-tier bosses.
│       │               ├── items
│       │               │   └── ItemLunarBlessedApple.java    // Used to craft and power the Enshrouded Beacon.
│       │               ├── network                           // ✅ Packet communication layer.
│       │               │   ├── MessageSetCurse.java          // ✅ Packet to sync selected curse to the server.
│       │               │   └── PacketHandler.java            // ✅ Registers and initializes packet channel.
│       │               ├── tileentity
│       │               │   └── TileEntityEnshroudedBeacon.java // ✅ Applies and persists area curses in real-time.
│       │               └── utils
│       │                   ├── EnchantmentUtils.java         // Utility functions for enchantment manipulation.
│       │                   └── NameGenerator.java            // Procedural name generator for mobs/bosses.
│       └── resources
│           ├── META-INF
│           │   └── mods.toml                                // Mod metadata and mod loader config.
│           └── assets
│               └── bossmobenhancer
│                   ├── blockstates
│                   │   └── enshrouded_beacon.json           // Defines block rendering and model references.
│                   ├── lang
│                   │   ├── en_us.lang                       // UI text, descriptions, tooltip keys.
│                   │   ├── fr_fr.lang
│                   │   ├── de_de.lang
│                   │   ├── ja_jp.lang
│                   │   ├── zh_cn.lang
│                   │   └── es_es.lang
│                   ├── models
│                   │   ├── block
│                   │   │   └── enshrouded_beacon.json       // Full cube model for the beacon block.
│                   │   └── item
│                   │       └── enshrouded_beacon.json       // Handheld and inventory rendering.
│                   ├── recipes
│                   │   └── enshrouded_beacon.json           // Crafted from Lunar Blessed Apple and base block.
│                   └── textures
│                       ├── blocks
│                       │   └── enshrouded_beacon.png        // All-side texture for the custom beacon.
│                       ├── gui
│                       │   ├── enshrouded_beacon.png        // Background layout for the custom GUI.
│                       │   └── buttons                      // ✅ NEW: Curse-specific textured buttons.
│                       │       ├── curse_button_blindness_hex.png     // Blindness-themed (dark eye).
│                       │       ├── curse_button_wither_touch.png      // Wither-touched (ashen glyph).
│                       │       ├── curse_button_sluggish_curse.png    // Chains/heavy mist.
│                       │       ├── curse_button_vampiric_fog.png      // Pale swirls/bloodstreaks.
│                       │       ├── curse_button_static.png            // Sharp zigzag or energy burst.
│                       │       ├── curse_button_terror_pulse.png      // Pulse ring or red haze.
│                       │       ├── curse_button_hex_of_hunger.png     // Gnashing jaws or bite mark.
│                       │       └── curse_button_doombrand.png         // Burning mark or ember rune.
│                       └── items
│                           └── lunar_blessed_apple.png      // Glowing fruit item used in beacon crafting.
└── config
    └── bossmobenhancer
        ├── bossmobenhancer.cfg            // Toggles for visuals, difficulty, drop rates, etc.
        ├── scaling_profiles.json          // Stat/effect curves keyed by biome or tier.
        └── README.txt                     // Human-readable guide to tuning values.