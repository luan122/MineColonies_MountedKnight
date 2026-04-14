package com.mountedknight.colony.buildings;

import com.minecolonies.api.colony.IColony;
import com.mountedknight.MountedKnightMod;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Building: Stables
 * Manages horse breeding and care for the colony's mounted cavalry.
 *
 * Level progression:
 *   Level 1: Care for 2 horses (heal, feed)
 *   Level 2: Care for 3 horses, begin breeding with wheat
 *   Level 3: Care for 4 horses, breeding with wheat
 *   Level 4: Care for 6 horses, breeding with golden apples (faster horses)
 *   Level 5: Care for 8 horses, breeding with golden apples
 *
 * Breeding cooldown: 5 minutes (6000 ticks) between breeds.
 */
public class BuildingStables {

    public static final String SCHEMATIC_NAME = "stables";
    public static final int BREEDING_COOLDOWN_TICKS = 6000; // 5 minutes

    private final IColony colony;
    private final BlockPos position;
    private int buildingLevel = 1;
    private long lastBreedingTick = 0;

    public BuildingStables(@NotNull IColony colony, @NotNull BlockPos pos) {
        this.colony = colony;
        this.position = pos;
    }

    /**
     * Maximum number of horses this stable can maintain at its current level.
     */
    public int getMaxHorses() {
        return switch (buildingLevel) {
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 6;
            case 5 -> 8;
            default -> 2;
        };
    }

    /**
     * Whether this stable level supports breeding.
     */
    public boolean canBreed() {
        return buildingLevel >= 2;
    }

    /**
     * Whether this stable level uses golden apples for better horses.
     */
    public boolean usesGoldenApples() {
        return buildingLevel >= 4;
    }

    /**
     * Check if breeding cooldown has elapsed.
     */
    public boolean isBreedingReady(long currentTick) {
        return (currentTick - lastBreedingTick) >= BREEDING_COOLDOWN_TICKS;
    }

    /**
     * Mark that a breeding event just occurred.
     */
    public void markBred(long currentTick) {
        this.lastBreedingTick = currentTick;
    }

    /**
     * Called by the AI when a breeding action is performed.
     * Uses the colony world time for cooldown tracking.
     */
    public void onBreedingPerformed() {
        Level level = colony.getWorld();
        if (level != null) {
            markBred(level.getGameTime());
        }
    }

    /**
     * Find all horses within the stable's area of influence (32 block radius).
     */
    public List<Horse> getHorsesInRange(@NotNull Level level) {
        int range = 32;
        AABB searchArea = new AABB(
                position.getX() - range, position.getY() - 10, position.getZ() - range,
                position.getX() + range, position.getY() + 10, position.getZ() + range
        );
        return level.getEntitiesOfClass(Horse.class, searchArea);
    }

    /**
     * Whether the stable has room for more horses.
     */
    public boolean hasCapacity(@NotNull Level level) {
        return getHorsesInRange(level).size() < getMaxHorses();
    }

    /**
     * Find an available horse that is not currently being ridden.
     */
    public Horse getAvailableHorse(@NotNull Level level) {
        List<Horse> horses = getHorsesInRange(level);
        for (Horse horse : horses) {
            if (!horse.isVehicle() && horse.isTamed() && horse.isAlive()) {
                return horse;
            }
        }
        return null;
    }

    @NotNull
    public String getSchematicName() {
        return SCHEMATIC_NAME;
    }

    @NotNull
    public BlockPos getPosition() {
        return position;
    }

    public int getBuildingLevel() {
        return buildingLevel;
    }

    public void setBuildingLevel(int level) {
        this.buildingLevel = Math.max(1, Math.min(5, level));
    }

    public IColony getColony() {
        return colony;
    }
}
