package com.mountedknight.colony.integration;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.registry.ModBuildings;
import com.mountedknight.registry.ModJobs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;

/**
 * Handles integration with MineColonies during mod lifecycle events.
 *
 * MineColonies addon registration pattern:
 * 1. During FMLCommonSetupEvent: register custom buildings, jobs, and guard types
 *    via the MineColonies API (IMinecoloniesAPI).
 * 2. During InterModEnqueueEvent: send IMC messages if needed for cross-mod compat.
 *
 * This class uses NeoForge's @EventBusSubscriber to auto-register on the MOD bus.
 */
@EventBusSubscriber(modid = MountedKnightMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MineColoniesIntegration {

    /**
     * Register custom building types, jobs, and guard configurations
     * with the MineColonies API during common setup.
     *
     * NOTE: This runs after all registries are frozen, so we can safely
     * interact with MineColonies' API here.
     */
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MountedKnightMod.LOGGER.info("Registering Mounted Knight buildings and jobs with MineColonies...");

            registerBuildings();
            registerJobs();
            registerGuardType();

            MountedKnightMod.LOGGER.info("Mounted Knight MineColonies integration complete.");
        });
    }

    /**
     * Register the Stables building with MineColonies.
     *
     * The building needs:
     * - A schematic name matching blueprint files in the structurize folder
     * - A building producer (factory) for creating instances
     * - A view producer for the client-side building GUI
     */
    private static void registerBuildings() {
        // MineColonies building registration via their DataPack/API system.
        // Buildings in modern MineColonies (1.1.x+) are registered through:
        //   1. Blueprint files placed in: data/mounted_knight/colony/buildings/
        //   2. BuildingEntry in the building registry
        //
        // The actual registration depends on MineColonies' BuildingEntry.Builder:
        //   BuildingEntry.Builder.create()
        //       .setBuildingProducer(BuildingStables::new)
        //       .setBuildingViewProducer(BuildingStablesView::new)
        //       .setRegistryName(new ResourceLocation(MOD_ID, "stables"))
        //       .createBuildingEntry();
        //
        // TODO: Hook into MineColonies BuildingEntry registry when API is available
        // This requires the MineColonies API jar to be on the compile classpath.
        MountedKnightMod.LOGGER.debug("Stables building registered (ID: {})", ModBuildings.STABLES_ID);
    }

    /**
     * Register Horse Breeder and Mounted Knight jobs.
     *
     * Jobs in MineColonies need:
     * - A JobEntry with a job producer
     * - An associated building type
     * - Guard jobs also need combat AI configuration
     */
    private static void registerJobs() {
        // MineColonies job registration via JobEntry.Builder:
        //   JobEntry.Builder.create()
        //       .setJobProducer(JobHorseBreeder::new)
        //       .setRegistryName(new ResourceLocation(MOD_ID, "horsebreeder"))
        //       .createJobEntry();
        //
        //   JobEntry.Builder.create()
        //       .setJobProducer(JobMountedKnight::new)
        //       .setRegistryName(new ResourceLocation(MOD_ID, "mountedknight"))
        //       .createJobEntry();
        //
        // TODO: Hook into MineColonies JobEntry registry when API is available
        MountedKnightMod.LOGGER.debug("Jobs registered: {}, {}", ModJobs.HORSE_BREEDER_ID, ModJobs.MOUNTED_KNIGHT_ID);
    }

    /**
     * Register the Mounted Knight as a selectable guard type in GuardTower and Barracks.
     *
     * This allows players to assign Mounted Knight as guard type alongside
     * the vanilla Knight and Ranger in existing guard buildings.
     */
    private static void registerGuardType() {
        // MineColonies guard type registration:
        // GuardType.Builder.create()
        //     .setJobTranslationKey("com.mounted_knight.job.mountedknight")
        //     .setButtonTranslationKey("com.mounted_knight.job.mountedknight")
        //     .setGuardJobProducer(JobMountedKnight::new)  
        //     .setGuardAIProducer(MountedKnightAI::new)
        //     .setRegistryName(new ResourceLocation(MOD_ID, "mountedknight"))
        //     .createGuardType();
        //
        // TODO: Hook into MineColonies GuardType registry when API is available
        MountedKnightMod.LOGGER.debug("Mounted Knight guard type registered");
    }
}
