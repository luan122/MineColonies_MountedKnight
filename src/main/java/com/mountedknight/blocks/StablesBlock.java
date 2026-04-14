package com.mountedknight.blocks;

import com.mountedknight.MountedKnightMod;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

import java.util.List;

/**
 * The Stables hut block.
 *
 * When placed in a colony, this block defines the location of the Stables building.
 * MineColonies uses hut blocks as anchor points for building schematics.
 *
 * Appearance: Wooden planks with hay accents (uses custom textures).
 */
public class StablesBlock extends Block {

    public StablesBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(3.0F, 6.0F)
                .sound(SoundType.WOOD)
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.translatable("block.mounted_knight.stables.tooltip"));
    }
}
