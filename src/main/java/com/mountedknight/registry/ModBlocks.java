package com.mountedknight.registry;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.blocks.StablesBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for blocks added by the Mounted Knight addon.
 */
public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MountedKnightMod.MOD_ID);
    private static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(MountedKnightMod.MOD_ID);

    // Stables hut block
    public static final DeferredBlock<StablesBlock> STABLES_BLOCK = BLOCKS.register("stables",
            StablesBlock::new);

    // The BlockItem for inventory/hand/JEI rendering
    public static final DeferredItem<BlockItem> STABLES_BLOCK_ITEM = BLOCK_ITEMS.register("stables",
            () -> new BlockItem(STABLES_BLOCK.get(), new Item.Properties()));

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
        BLOCK_ITEMS.register(modEventBus);
        modEventBus.addListener(ModBlocks::addCreative);
    }

    private static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(STABLES_BLOCK_ITEM);
        }
    }
}
