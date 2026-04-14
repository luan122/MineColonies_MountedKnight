package com.mountedknight.colony.buildings.views;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.buildings.BuildingStables;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Client-side view handler for the Stables building GUI.
 *
 * Manages the Blockout XML window defined in:
 *   assets/mounted_knight/gui/windowhut/stables.xml
 *
 * Displays:
 * - Horse count / capacity
 * - Breeding status (ready, cooldown, or requires level)
 * - Feed type (wheat or golden apples based on level)
 * - List of horses within the stables' range with health
 * - Level progression info
 */
public class WindowHutStables {

    public static final ResourceLocation WINDOW_RESOURCE =
            ResourceLocation.fromNamespaceAndPath(MountedKnightMod.MOD_ID, "gui/windowhut/stables.xml");

    private final BuildingStables building;
    private final Level level;

    public WindowHutStables(BuildingStables building) {
        this.building = building;
        this.level = Minecraft.getInstance().level;
    }

    /**
     * Get the Blockout XML resource location for this window.
     */
    public ResourceLocation getWindowResource() {
        return WINDOW_RESOURCE;
    }

    /**
     * Generate the header text with building level.
     */
    public Component getHeaderText() {
        return Component.translatable("gui.mounted_knight.stables.level",
                building.getBuildingLevel());
    }

    /**
     * Get formatted horse count label.
     */
    public Component getHorseCountText() {
        int current = 0;
        if (level != null) {
            current = building.getHorsesInRange(level).size();
        }
        return Component.translatable("gui.mounted_knight.stables.horses",
                current, building.getMaxHorses());
    }

    /**
     * Get breeding status text.
     */
    public Component getBreedingStatusText() {
        String statusKey;
        if (!building.canBreed()) {
            statusKey = "gui.mounted_knight.stables.breeding.unavailable";
        } else if (level != null && building.isBreedingReady(level.getGameTime())) {
            statusKey = "gui.mounted_knight.stables.breeding.ready";
        } else {
            statusKey = "gui.mounted_knight.stables.breeding.cooldown";
        }
        return Component.translatable("gui.mounted_knight.stables.breeding",
                Component.translatable(statusKey));
    }

    /**
     * Get feed type text based on building level.
     */
    public Component getFeedTypeText() {
        String foodKey = building.usesGoldenApples()
                ? "gui.mounted_knight.stables.food.golden_apple"
                : "gui.mounted_knight.stables.food.wheat";
        return Component.translatable("gui.mounted_knight.stables.food",
                Component.translatable(foodKey));
    }

    /**
     * Build a list of horse data for the scrolling list in the GUI.
     * Each entry contains the horse's display name and health percentage.
     */
    public List<HorseDisplayData> getHorseListData() {
        List<HorseDisplayData> result = new ArrayList<>();
        if (level == null) {
            return result;
        }

        List<Horse> horses = building.getHorsesInRange(level);
        for (Horse horse : horses) {
            String name = horse.hasCustomName()
                    ? horse.getCustomName().getString()
                    : horse.getDisplayName().getString();
            float healthPercent = (horse.getHealth() / horse.getMaxHealth()) * 100f;
            String healthStr = String.format("%.0f%%", healthPercent);
            boolean isTamed = horse.isTamed();
            boolean isRidden = horse.isVehicle();

            result.add(new HorseDisplayData(name, healthStr, isTamed, isRidden));
        }
        return result;
    }

    /**
     * Data holder for a single horse entry in the GUI list.
     */
    public static class HorseDisplayData {
        public final String name;
        public final String health;
        public final boolean tamed;
        public final boolean ridden;

        public HorseDisplayData(String name, String health, boolean tamed, boolean ridden) {
            this.name = name;
            this.health = health;
            this.tamed = tamed;
            this.ridden = ridden;
        }
    }
}
