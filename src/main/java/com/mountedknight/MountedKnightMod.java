package com.mountedknight;

import com.mountedknight.registry.ModBlocks;
import com.mountedknight.registry.ModBuildings;
import com.mountedknight.registry.ModItems;
import com.mountedknight.registry.ModJobs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(MountedKnightMod.MOD_ID)
public class MountedKnightMod {
    public static final String MOD_ID = "mounted_knight";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public MountedKnightMod(IEventBus modEventBus) {
        LOGGER.info("Mounted Knight Addon for MineColonies - Initializing");

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBuildings.register(modEventBus);
        ModJobs.register(modEventBus);

        // Mekanism integration (osmium + refined obsidian lances)
        // IMPORTANT: Only load MekanismCompat class if Mekanism is present.
        // Referencing the class when Mekanism is absent is safe (no Mekanism imports),
        // but we guard here to avoid registering orphan items.
        if (ModList.get().isLoaded("mekanism")) {
            com.mountedknight.compat.MekanismCompat.register(modEventBus);
        } else {
            LOGGER.debug("Mekanism not detected - skipping Osmium/Refined Obsidian lances");
        }

        LOGGER.info("Mounted Knight Addon - Registration complete");
    }
}
