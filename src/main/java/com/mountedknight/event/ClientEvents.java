package com.mountedknight.event;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.items.LanceItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * Client-side event handlers for the Mounted Knight mod.
 * Adds tooltip info to lance items showing mounted damage bonus.
 */
@EventBusSubscriber(modid = MountedKnightMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() instanceof LanceItem) {
            event.getToolTip().add(Component.translatable("tooltip.mounted_knight.lance.mounted_bonus")
                    .withStyle(ChatFormatting.GOLD));
            event.getToolTip().add(Component.translatable("tooltip.mounted_knight.lance.charge_bonus")
                    .withStyle(ChatFormatting.YELLOW));
        }
    }
}
