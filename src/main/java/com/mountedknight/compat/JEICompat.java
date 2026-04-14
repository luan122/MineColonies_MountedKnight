package com.mountedknight.compat;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.items.LanceItem;
import com.mountedknight.registry.ModBlocks;
import com.mountedknight.registry.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI (Just Enough Items) integration plugin.
 *
 * - Adds informational descriptions to all lance items showing
 *   mounted combat stats, charge bonuses, and tier details.
 * - Vanilla recipe types (crafting_shaped, smithing_transform) are
 *   auto-detected by JEI, so no recipe category registration needed.
 */
@JeiPlugin
public class JEICompat implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID =
            ResourceLocation.fromNamespaceAndPath(MountedKnightMod.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Add info pages for all vanilla tier lances
        addLanceInfo(registration, new ItemStack(ModItems.WOODEN_LANCE.get()), 3.0F);
        addLanceInfo(registration, new ItemStack(ModItems.STONE_LANCE.get()), 4.0F);
        addLanceInfo(registration, new ItemStack(ModItems.IRON_LANCE.get()), 5.0F);
        addLanceInfo(registration, new ItemStack(ModItems.GOLDEN_LANCE.get()), 3.0F);
        addLanceInfo(registration, new ItemStack(ModItems.DIAMOND_LANCE.get()), 6.0F);
        addLanceInfo(registration, new ItemStack(ModItems.NETHERITE_LANCE.get()), 7.0F);

        // Add Mekanism lance info ONLY if Mekanism is loaded
        // Use ModList directly to avoid loading MekanismCompat class when Mekanism is absent
        if (ModList.get().isLoaded("mekanism")) {
            addLanceInfo(registration, new ItemStack(MekanismCompat.OSMIUM_LANCE.get()), 7.0F);
            addLanceInfo(registration, new ItemStack(MekanismCompat.REFINED_OBSIDIAN_LANCE.get()), 10.5F);
        }

        // Stables block info
        registration.addItemStackInfo(
                List.of(new ItemStack(ModBlocks.STABLES_BLOCK_ITEM.get())),
                Component.translatable("jei.mounted_knight.stables.header"),
                Component.literal(""),
                Component.translatable("jei.mounted_knight.stables.desc"),
                Component.literal(""),
                Component.translatable("jei.mounted_knight.stables.levels")
        );
    }

    private void addLanceInfo(IRecipeRegistration registration, ItemStack stack, float baseDamage) {
        float mountedDamage = baseDamage + LanceItem.MOUNTED_BONUS_DAMAGE;

        List<Component> description = new ArrayList<>();
        description.add(Component.translatable("jei.mounted_knight.lance.header"));
        description.add(Component.literal(""));
        description.add(Component.translatable("jei.mounted_knight.lance.base_damage",
                String.format("%.1f", baseDamage)));
        description.add(Component.translatable("jei.mounted_knight.lance.mounted_damage",
                String.format("%.1f", mountedDamage)));
        description.add(Component.translatable("jei.mounted_knight.lance.charge_info"));
        description.add(Component.literal(""));
        description.add(Component.translatable("jei.mounted_knight.lance.usage"));

        registration.addItemStackInfo(
                List.of(stack),
                description.toArray(new Component[0])
        );
    }
}
