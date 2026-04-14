package com.mountedknight.colony.jobs;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.buildings.BuildingStables;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Job: Horse Breeder
 * The worker assigned to a Stables building.
 *
 * Responsibilities:
 * - Feed horses (wheat) to heal them
 * - Breed horses when stable has capacity (wheat for basic, golden apples for better stats)
 * - Tame wild horses found near the stable
 * - Keep horses within the stable area
 *
 * AI States:
 * 1. IDLE         - Check if any task is needed
 * 2. FEED_HORSE   - Find hungry/hurt horses and feed them
 * 3. BREED_HORSE  - Breed two horses if capacity allows and cooldown elapsed
 * 4. TAME_HORSE   - Attempt to tame untamed horses nearby
 * 5. RETURN       - Walk back to stable
 */
public class JobHorseBreeder {

    public static final String JOB_NAME = "horsebreeder";

    private final BuildingStables stables;
    private HorseBreederState currentState = HorseBreederState.IDLE;

    public enum HorseBreederState {
        IDLE,
        FEED_HORSE,
        BREED_HORSE,
        TAME_HORSE,
        RETURN_TO_STABLE
    }

    public JobHorseBreeder(@NotNull BuildingStables stables) {
        this.stables = stables;
    }

    /**
     * Main AI tick - determines what the horse breeder should do next.
     */
    public HorseBreederState determineNextState(@NotNull ServerLevel level) {
        BlockPos stablePos = stables.getPosition();
        List<Horse> horses = stables.getHorsesInRange(level);

        // Priority 1: Feed injured horses
        for (Horse horse : horses) {
            if (horse.getHealth() < horse.getMaxHealth() && horse.isTamed()) {
                return HorseBreederState.FEED_HORSE;
            }
        }

        // Priority 2: Breed horses if stable has capacity and cooldown is ready
        if (stables.canBreed()
                && stables.hasCapacity(level)
                && stables.isBreedingReady(level.getGameTime())
                && countTamedAdults(horses) >= 2) {
            return HorseBreederState.BREED_HORSE;
        }

        // Priority 3: Tame wild horses nearby
        for (Horse horse : horses) {
            if (!horse.isTamed()) {
                return HorseBreederState.TAME_HORSE;
            }
        }

        return HorseBreederState.IDLE;
    }

    /**
     * Get the food item stack needed for the current operation.
     */
    public ItemStack getRequiredFood() {
        if (stables.usesGoldenApples()) {
            return new ItemStack(Items.GOLDEN_APPLE, 2);
        }
        return new ItemStack(Items.WHEAT, 2);
    }

    /**
     * Performs a healing feed on a hurt horse.
     */
    public boolean feedHorse(@NotNull Horse horse) {
        if (!horse.isAlive() || horse.getHealth() >= horse.getMaxHealth()) {
            return false;
        }
        // Heal the horse
        float healAmount = 2.0F; // 1 heart
        horse.heal(healAmount);
        MountedKnightMod.LOGGER.debug("Horse breeder fed horse at {}", horse.blockPosition());
        return true;
    }

    /**
     * Attempts to breed two available adult tamed horses.
     * Returns true if breeding was initiated.
     */
    public boolean breedHorses(@NotNull ServerLevel level) {
        if (!stables.canBreed() || !stables.hasCapacity(level)) {
            return false;
        }

        if (!stables.isBreedingReady(level.getGameTime())) {
            return false;
        }

        List<Horse> horses = stables.getHorsesInRange(level);
        Horse parent1 = null;
        Horse parent2 = null;

        for (Horse horse : horses) {
            if (horse.isTamed() && !horse.isBaby() && horse.canFallInLove()) {
                if (parent1 == null) {
                    parent1 = horse;
                } else {
                    parent2 = horse;
                    break;
                }
            }
        }

        if (parent1 != null && parent2 != null) {
            parent1.setInLove(null);
            parent2.setInLove(null);
            stables.markBred(level.getGameTime());
            MountedKnightMod.LOGGER.debug("Horse breeder initiated breeding at stables {}",
                    stables.getPosition());
            return true;
        }

        return false;
    }

    /**
     * Attempts to tame a wild horse.
     */
    public boolean tameHorse(@NotNull Horse horse) {
        if (horse.isTamed()) {
            return false;
        }
        // Simulate taming with high chance for worker efficiency
        horse.setTamed(true);
        MountedKnightMod.LOGGER.debug("Horse breeder tamed horse at {}", horse.blockPosition());
        return true;
    }

    private int countTamedAdults(List<Horse> horses) {
        int count = 0;
        for (Horse horse : horses) {
            if (horse.isTamed() && !horse.isBaby()) {
                count++;
            }
        }
        return count;
    }

    @NotNull
    public BuildingStables getStables() {
        return stables;
    }

    @NotNull
    public HorseBreederState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(HorseBreederState state) {
        this.currentState = state;
    }
}
