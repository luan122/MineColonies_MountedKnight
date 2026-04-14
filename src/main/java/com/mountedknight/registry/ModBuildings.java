package com.mountedknight.registry;

import com.mountedknight.MountedKnightMod;
import net.neoforged.bus.api.IEventBus;

/**
 * Registry for MineColonies buildings added by this addon.
 * Buildings are registered through MineColonies' own registry system
 * via the IMinecoloniesAPI during mod construction/common setup.
 */
public class ModBuildings {

    // Building IDs used by MineColonies registry
    public static final String STABLES_ID = "stables";
    public static final String MOUNTED_KNIGHT_TOWER_ID = "mountedknighttower";

    public static void register(IEventBus modEventBus) {
        // MineColonies buildings are registered via their API in ModBuildingInitializer
        MountedKnightMod.LOGGER.debug("ModBuildings registry initialized");
    }
}
