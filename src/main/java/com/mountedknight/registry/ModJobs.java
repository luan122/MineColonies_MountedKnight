package com.mountedknight.registry;

import com.mountedknight.MountedKnightMod;
import net.neoforged.bus.api.IEventBus;

/**
 * Registry for MineColonies jobs added by this addon.
 * Jobs are registered through MineColonies' own registry system.
 */
public class ModJobs {

    // Job IDs used by MineColonies registry
    public static final String HORSE_BREEDER_ID = "horsebreeder";
    public static final String MOUNTED_KNIGHT_ID = "mountedknight";
    public static final String MOUNTED_ARCHER_ID = "mountedarcher";

    public static void register(IEventBus modEventBus) {
        MountedKnightMod.LOGGER.debug("ModJobs registry initialized");
    }
}
