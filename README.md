# Mounted Knight - MineColonies Cavalry Addon

A NeoForge addon mod for [MineColonies](https://github.com/ldtteam/minecolonies) that adds mounted cavalry to your colony defense.

**Author:** Feilor

## Features

### Stables Building
- New colony building where a **Horse Breeder** worker cares for and breeds horses
- 5 building levels with progressive horse capacity: 2 → 3 → 4 → 6 → 8 horses
- Levels 1-3: Breed with wheat/golden carrots
- Levels 4-5: Breed with golden apples for better horses
- Worker feeds, heals, and tames horses automatically
- Crafting recipe:

```
F H F       F = Oak Fence
S G S       H = Hay Bale
P L P       S = Saddle
            G = Structurize Build Tool (not consumed)
            P = Oak Planks
            L = Lead
```

### Mounted Knight Guard
- New guard type selectable in Guard Towers and Barracks
- Dual combat modes:
  - **Lancer**: Charges into melee with a lance (bonus damage when mounted + charge bonus at full gallop)
  - **Mounted Archer**: Fires arrows from horseback while maintaining distance
- If the horse dies in combat, the knight fights on foot then returns to stables for a new mount
- Progressive stats tied to guard building level

### Lance Weapons (6 Vanilla Tiers)
| Lance | Damage | Mounted Bonus | Durability |
|-------|--------|---------------|------------|
| Wooden | 3.0 | +4.0 | 59 |
| Stone | 4.0 | +4.0 | 131 |
| Iron | 5.0 | +4.0 | 250 |
| Golden | 3.0 | +4.0 | 32 |
| Diamond | 6.0 | +4.0 | 1561 |
| Netherite | 7.0 | +4.0 | 2031 |

All lances gain up to **+3 charge bonus** at full gallop speed.

### Mekanism Compatibility (Optional)
If [Mekanism](https://www.curseforge.com/minecraft/mc-mods/mekanism) is installed, two additional lances become available:

| Lance | Damage | Mounted Bonus | Durability |
|-------|--------|---------------|------------|
| Osmium | 7.0 | +4.0 | 3046 |
| Refined Obsidian | 10.5 | +4.0 | 4569 |

### JEI Integration
- Info pages for all lance tiers and the stables block in Just Enough Items

## Requirements

- **Minecraft** 1.21.1
- **NeoForge** 21.1.77+
- **MineColonies** 1.1.1300+
- **Structurize** 1.0.822+
- **Mekanism** (optional) — for Osmium and Refined Obsidian lances
- **JEI** (optional) — for recipe/info integration

## Installation

1. Install NeoForge for Minecraft 1.21.1
2. Install MineColonies and its dependencies
3. Drop `minecolonies_mounted_knight-1.21.1-x.x.x.jar` into your `mods/` folder
4. Place a Stables blueprint in your colony and assign a Horse Breeder
5. Set guard type to "Mounted Knight" in any Guard Tower

## Building

```bash
./gradlew build
```

The built jar will be in `build/libs/` as `minecolonies_mounted_knight-1.21.1-<version>.jar`.

## Development

```bash
./gradlew runClient   # Launch Minecraft client with the mod
./gradlew runServer   # Launch dedicated server
./gradlew runData     # Run data generators
```

## License

MIT
