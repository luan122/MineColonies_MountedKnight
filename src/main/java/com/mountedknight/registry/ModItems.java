package com.mountedknight.registry;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.items.LanceItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MountedKnightMod.MOD_ID);

    // Lance - cavalaria melee weapon (more damage when mounted)
    public static final DeferredItem<LanceItem> LANCE = ITEMS.register("lance",
            () -> new LanceItem(Tiers.IRON, new Item.Properties()
                    .attributes(LanceItem.createAttributes())
            ));

    // Iron Lance
    public static final DeferredItem<LanceItem> IRON_LANCE = ITEMS.register("iron_lance",
            () -> new LanceItem(Tiers.IRON, new Item.Properties()
                    .attributes(LanceItem.createAttributes())
            ));

    // Diamond Lance
    public static final DeferredItem<LanceItem> DIAMOND_LANCE = ITEMS.register("diamond_lance",
            () -> new LanceItem(Tiers.DIAMOND, new Item.Properties()
                    .attributes(LanceItem.createAttributes())
            ));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModItems::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(LANCE);
            event.accept(IRON_LANCE);
            event.accept(DIAMOND_LANCE);
        }
    }
}
