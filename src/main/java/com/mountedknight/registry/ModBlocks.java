package com.mountedknight.registry;

import com.minecolonies.api.items.ItemBlockHut;
import com.mountedknight.MountedKnightMod;
import com.mountedknight.blocks.BlockHutStables;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry for blocks added by the Mounted Knight addon.
 *
 * The hut block extends AbstractBlockHut for full MineColonies integration.
 * ItemBlockHut is registered manually since MineColonies' own init only
 * handles its own blocks, not addon blocks.
 */
public class ModBlocks {

    private static final String STABLES_BLOCK_NAME = "blockhut" + ModBuildings.STABLES_ID;

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MountedKnightMod.MOD_ID);
    private static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(MountedKnightMod.MOD_ID);

    // Stables hut block — extends AbstractBlockHut for full MineColonies integration
    public static final DeferredBlock<BlockHutStables> STABLES_BLOCK = BLOCKS.register(STABLES_BLOCK_NAME,
            BlockHutStables::new);

    // ItemBlockHut for inventory/hand/JEI rendering (MineColonies' block item type)
    public static final DeferredItem<ItemBlockHut> STABLES_BLOCK_ITEM = BLOCK_ITEMS.register(STABLES_BLOCK_NAME,
            () -> new ItemBlockHut(STABLES_BLOCK.get(), new Item.Properties()));

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
