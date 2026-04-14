# Mounted Knight - MineColonies Cavalry Addon

A NeoForge addon mod for [MineColonies](https://github.com/ldtteam/minecolonies) that adds mounted cavalry to your colony defense.

## Features

### Stables Building
- New colony building where a **Horse Breeder** worker cares for and breeds horses
- 5 building levels with progressive horse capacity: 2 → 3 → 4 → 6 → 8 horses
- Levels 1-3: Breed with wheat/golden carrots
- Levels 4-5: Breed with golden apples for better horses
- Worker feeds, heals, and tames horses automatically

### Mounted Knight Guard
- New guard type selectable in Guard Towers and Barracks
- Dual combat modes:
  - **Lancer**: Charges into melee with a lance (bonus damage when mounted + charge bonus at full gallop)
  - **Mounted Archer**: Fires arrows from horseback while maintaining distance
- If the horse dies in combat, the knight fights on foot then returns to stables for a new mount
- Progressive stats tied to guard building level

### Lance Weapons
- **Lance** (iron ingot + sticks): Base melee weapon
- **Iron Lance** (iron block + sticks): Stronger variant
- **Diamond Lance** (diamond + sticks): Best variant
- All lances deal **+4 bonus damage** when mounted, plus charge bonus at full gallop speed

## Requirements

- **Minecraft** 1.21.1
- **NeoForge** 21.1.77+
- **MineColonies** 1.1.702+
- **Structurize** 1.0.742+

## Installation

1. Install NeoForge for Minecraft 1.21.1
2. Install MineColonies and its dependencies
3. Drop `mounted_knight-x.x.x.jar` into your `mods/` folder
4. Place a Stables blueprint in your colony and assign a Horse Breeder
5. Set guard type to "Mounted Knight" in any Guard Tower

## Building

```bash
./gradlew build
```

The built jar will be in `build/libs/`.

## Development

```bash
./gradlew runClient   # Launch Minecraft client with the mod
./gradlew runServer   # Launch dedicated server
./gradlew runData     # Run data generators
```

## License

MIT
