package com.mountedknight.colony.integration;

import com.mountedknight.MountedKnightMod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Handles integration with MineColonies during mod lifecycle events.
 *
 * Building registration is handled by ModBuildings via DeferredRegister.
 * This class handles additional setup that must happen after registries freeze.
 */
@EventBusSubscriber(modid = MountedKnightMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class MineColoniesIntegration {

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MountedKnightMod.LOGGER.info("Mounted Knight MineColonies integration initialized.");
            // TODO: Register JobEntry and GuardType when MineColonies API supports addon job/guard registration
        });
    }
}
