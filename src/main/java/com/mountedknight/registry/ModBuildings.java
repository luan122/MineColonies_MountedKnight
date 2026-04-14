package com.mountedknight.registry;

import com.minecolonies.api.colony.buildings.registry.BuildingEntry;
import com.minecolonies.core.colony.buildings.views.EmptyView;
import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.buildings.BuildingStables;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers buildings into MineColonies' custom building registry.
 *
 * MineColonies uses a custom NeoForge registry keyed at "minecolonies:buildings".
 * Addon mods create their own DeferredRegister pointing at the same registry key
 * and register entries under their own mod ID namespace.
 */
public class ModBuildings {

    public static final String STABLES_ID = "stables";

    /**
     * MineColonies' building registry key: "minecolonies:buildings"
     */
    private static final ResourceKey<Registry<BuildingEntry>> BUILDING_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecolonies", "buildings"));

    public static final DeferredRegister<BuildingEntry> BUILDING_ENTRIES =
            DeferredRegister.create(BUILDING_REGISTRY_KEY, MountedKnightMod.MOD_ID);

    /**
     * Stables building entry — links the hut block, server-side building, and client view.
     */
    public static final DeferredHolder<BuildingEntry, BuildingEntry> STABLES =
            BUILDING_ENTRIES.register(STABLES_ID, () ->
                    new BuildingEntry.Builder()
                            .setBuildingBlock(ModBlocks.STABLES_BLOCK.get())
                            .setBuildingProducer(BuildingStables::new)
                            .setBuildingViewProducer(() -> EmptyView::new)
                            .setRegistryName(ResourceLocation.fromNamespaceAndPath(MountedKnightMod.MOD_ID, STABLES_ID))
                            .createBuildingEntry()
            );

    public static void register(IEventBus modEventBus) {
        BUILDING_ENTRIES.register(modEventBus);
    }
}
