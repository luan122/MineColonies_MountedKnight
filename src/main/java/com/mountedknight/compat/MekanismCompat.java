package com.mountedknight.compat;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.items.LanceItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Mekanism compatibility module.
 *
 * Registers Osmium and Refined Obsidian lance variants
 * ONLY if Mekanism is present at runtime.
 *
 * Osmium Lance:      7.0 base damage (same as netherite)
 * Ref. Obsidian Lance: 10.5 base damage (50% more than netherite 7.0)
 *
 * Recipes use Mekanism ingots (not dusts):
 *   - mekanism:ingot_osmium
 *   - mekanism:ingot_refined_obsidian
 */
public final class MekanismCompat {

    public static final String MEKANISM_MOD_ID = "mekanism";

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MountedKnightMod.MOD_ID);

    // Osmium Lance - same as netherite
    public static final DeferredItem<LanceItem> OSMIUM_LANCE = ITEMS.register("osmium_lance",
            () -> new LanceItem(MekanismTiers.OSMIUM, 7.0F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(7.0F))
                    .fireResistant()
            ));

    // Refined Obsidian Lance - 50% better than netherite
    public static final DeferredItem<LanceItem> REFINED_OBSIDIAN_LANCE = ITEMS.register("refined_obsidian_lance",
            () -> new LanceItem(MekanismTiers.REFINED_OBSIDIAN, 10.5F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(10.5F))
                    .fireResistant()
            ));

    /**
     * Returns true if Mekanism is loaded.
     */
    public static boolean isLoaded() {
        return ModList.get().isLoaded(MEKANISM_MOD_ID);
    }

    /**
     * Register Mekanism items on the mod event bus.
     * Call this only after checking isLoaded().
     */
    public static void register(IEventBus modEventBus) {
        if (!isLoaded()) {
            return;
        }
        MountedKnightMod.LOGGER.info("Mekanism detected - registering Osmium and Refined Obsidian lances");
        ITEMS.register(modEventBus);
        modEventBus.addListener(MekanismCompat::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(OSMIUM_LANCE);
            event.accept(REFINED_OBSIDIAN_LANCE);
        }
    }

    private MekanismCompat() {}
}
