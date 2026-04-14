package com.mountedknight.compat;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

/**
 * Custom tool tiers for Mekanism materials.
 *
 * Osmium: Same as Netherite but 50% more durable
 *   - 3046 durability, 9.0 speed, 4.0 base damage, enchantability 15
 *
 * Refined Obsidian: 50% better than Netherite in all stats, 50% more durable than Osmium
 *   - 4569 durability, 13.5 speed, 6.0 base damage, enchantability 22
 *
 * Repair ingredients use Mekanism item tags.
 */
public final class MekanismTiers {

    /**
     * Osmium tier - equivalent to Netherite.
     * Repair ingredient will be set lazily via tag when Mekanism is loaded.
     */
    public static final Tier OSMIUM = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,  // same harvest level as netherite
            3046,    // durability (netherite 2031 * 1.5)
            9.0F,    // speed (same as netherite)
            4.0F,    // attack damage bonus (same as netherite)
            15,      // enchantability (same as netherite)
            () -> Ingredient.EMPTY  // repair ingredient set via tags at runtime
    );

    /**
     * Refined Obsidian tier - 50% better than Netherite in all stats.
     */
    public static final Tier REFINED_OBSIDIAN = new SimpleTier(
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL,  // same harvest level as netherite
            4569,    // durability (osmium 3046 * 1.5)
            13.5F,   // speed (netherite 9.0 * 1.5)
            6.0F,    // attack damage bonus (netherite 4.0 * 1.5)
            22,      // enchantability (netherite 15 * 1.5 ≈ 22)
            () -> Ingredient.EMPTY
    );

    private MekanismTiers() {}
}
