zBoss Mob Enhancer
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


Made with the assistance of Sweep AI and Copilot.

BossMobEnhancer Release 2

Dramatic GUI and scaling reforms, new item added, new terrain, new particle effects, new abilities, more of a V2 really. Sorry, I could not get the nameplate toggle to work on this update. I've put all the legwork in; it's just debugging now :3

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
│       │               │   ├── CustomBossBarRenderer.java      // NEW: Custom boss bar GUI rendering boss bar based on health and phase.
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
│       │               │   ├── LordSpawnHandler.java         // Spawns ultra-rare lord-tier bosses.
│       │               │   └── BossPhaseHandler.java         // Handles multi-phase logic; applies phase transitions based on boss health.
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
