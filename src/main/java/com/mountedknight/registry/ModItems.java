package com.mountedknight.registry;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.items.LanceItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MountedKnightMod.MOD_ID);

    // === Vanilla tier lances ===
    // Pattern: diagonal lance shape (  X / .S. / S..)  with head material + sticks

    // Wood Lance - starter tier
    public static final DeferredItem<LanceItem> WOODEN_LANCE = ITEMS.register("wooden_lance",
            () -> new LanceItem(Tiers.WOOD, 3.0F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(3.0F))
            ));

    // Stone Lance
    public static final DeferredItem<LanceItem> STONE_LANCE = ITEMS.register("stone_lance",
            () -> new LanceItem(Tiers.STONE, 4.0F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(4.0F))
            ));

    // Iron Lance
    public static final DeferredItem<LanceItem> IRON_LANCE = ITEMS.register("iron_lance",
            () -> new LanceItem(Tiers.IRON, 5.0F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(5.0F))
            ));

    // Golden Lance (fast but fragile, like gold tools)
    public static final DeferredItem<LanceItem> GOLDEN_LANCE = ITEMS.register("golden_lance",
            () -> new LanceItem(Tiers.GOLD, 3.0F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(3.0F))
            ));

    // Diamond Lance
    public static final DeferredItem<LanceItem> DIAMOND_LANCE = ITEMS.register("diamond_lance",
            () -> new LanceItem(Tiers.DIAMOND, 6.0F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(6.0F))
            ));

    // Netherite Lance (obtained via smithing table upgrade, like all netherite gear)
    public static final DeferredItem<LanceItem> NETHERITE_LANCE = ITEMS.register("netherite_lance",
            () -> new LanceItem(Tiers.NETHERITE, 7.0F, new Item.Properties()
                    .attributes(LanceItem.createAttributes(7.0F))
                    .fireResistant()
            ));

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        modEventBus.addListener(ModItems::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(WOODEN_LANCE);
            event.accept(STONE_LANCE);
            event.accept(IRON_LANCE);
            event.accept(GOLDEN_LANCE);
            event.accept(DIAMOND_LANCE);
            event.accept(NETHERITE_LANCE);
        }
    }
}
