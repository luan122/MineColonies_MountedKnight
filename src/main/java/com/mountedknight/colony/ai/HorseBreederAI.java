package com.mountedknight.colony.ai;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.buildings.BuildingStables;
import com.mountedknight.colony.jobs.JobHorseBreeder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * AI Controller for the Horse Breeder working in the Stables.
 *
 * Behaviors cycle through:
 *   1. Feed horses (check each horse health; if low, feed wheat/hay)
 *   2. Breed horses (when capacity allows, feed breeding pair)
 *   3. Heal injured horses (apply potion or slow heal via hay)
 *   4. Collect breeding items from colony storage
 *   5. Idle/patrol stables when all tasks done
 */
public class HorseBreederAI {

    private static final double FEED_RANGE = 2.0;
    private static final int FEED_COOLDOWN_TICKS = 40; // 2 seconds between feeds
    private static final int BREED_COOLDOWN_TICKS = 100; // 5 seconds between breed attempts

    public enum BreederState {
        IDLE,
        FEEDING,
        BREEDING,
        HEALING,
        COLLECTING_ITEMS,
        PATROLLING
    }

    private final JobHorseBreeder job;
    private BreederState currentState = BreederState.IDLE;
    private int actionCooldown = 0;
    private int ticksSinceAction = 0;

    public HorseBreederAI(@NotNull JobHorseBreeder job) {
        this.job = job;
    }

    /**
     * Main tick called every game tick from the job.
     */
    public void tick(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        ticksSinceAction++;
        if (actionCooldown > 0) {
            actionCooldown--;
            return;
        }

        BuildingStables stables = job.getStables();
        if (stables == null) {
            currentState = BreederState.IDLE;
            return;
        }

        // Priority-based task selection
        BreederState nextState = determineNextTask(level, stables, citizen);
        currentState = nextState;

        switch (currentState) {
            case FEEDING -> handleFeeding(level, stables, citizen);
            case BREEDING -> handleBreeding(level, stables, citizen);
            case HEALING -> handleHealing(level, stables, citizen);
            case COLLECTING_ITEMS -> handleCollecting(level, stables, citizen);
            case PATROLLING -> handlePatrolling(level, stables, citizen);
            case IDLE -> { /* Wait at stables */ }
        }
    }

    /**
     * Determine the highest-priority task.
     */
    private BreederState determineNextTask(@NotNull ServerLevel level,
                                           @NotNull BuildingStables stables,
                                           @NotNull LivingEntity citizen) {
        List<Horse> horses = getHorsesNearStables(level, stables);

        // Priority 1: Heal any horse below 50% health
        for (Horse horse : horses) {
            if (horse.getHealth() < horse.getMaxHealth() * 0.5f) {
                return BreederState.HEALING;
            }
        }

        // Priority 2: Feed any horse below 80% health
        for (Horse horse : horses) {
            if (horse.getHealth() < horse.getMaxHealth() * 0.8f) {
                return BreederState.FEEDING;
            }
        }

        // Priority 3: Breed if capacity allows
        if (stables.canBreed()) {
            return BreederState.BREEDING;
        }

        // Priority 4: Collect items if inventory low
        if (needsMoreItems(citizen)) {
            return BreederState.COLLECTING_ITEMS;
        }

        // Priority 5: Patrol/idle
        if (ticksSinceAction > 200) {
            return BreederState.PATROLLING;
        }

        return BreederState.IDLE;
    }

    /**
     * Find all horses within the stables area.
     */
    private List<Horse> getHorsesNearStables(@NotNull ServerLevel level, @NotNull BuildingStables stables) {
        BlockPos pos = stables.getPosition();
        double radius = 15.0 + stables.getBuildingLevel() * 3.0; // Larger area at higher levels
        AABB area = new AABB(
                pos.getX() - radius, pos.getY() - 3, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + 5, pos.getZ() + radius
        );
        return level.getEntitiesOfClass(Horse.class, area);
    }

    // --- State Handlers ---

    private void handleFeeding(@NotNull ServerLevel level, @NotNull BuildingStables stables,
                               @NotNull LivingEntity citizen) {
        List<Horse> horses = getHorsesNearStables(level, stables);
        Horse needsFood = null;

        for (Horse horse : horses) {
            if (horse.getHealth() < horse.getMaxHealth() * 0.8f) {
                needsFood = horse;
                break;
            }
        }

        if (needsFood == null) {
            return;
        }

        double distSq = citizen.distanceToSqr(needsFood);
        if (distSq > FEED_RANGE * FEED_RANGE) {
            // Move to horse
            if (citizen instanceof Mob mob) {
                mob.getNavigation().moveTo(needsFood, 1.0);
            }
        } else {
            // Feed the horse
            needsFood.heal(4.0F);
            actionCooldown = FEED_COOLDOWN_TICKS;
            ticksSinceAction = 0;

            MountedKnightMod.LOGGER.debug("Breeder fed horse at {}",
                    needsFood.blockPosition().toShortString());
        }
    }

    private void handleBreeding(@NotNull ServerLevel level, @NotNull BuildingStables stables,
                                @NotNull LivingEntity citizen) {
        List<Horse> horses = getHorsesNearStables(level, stables);

        if (horses.size() < 2) {
            // Need at least 2 horses to breed
            currentState = BreederState.IDLE;
            return;
        }

        // Find two adult horses
        Horse parent1 = null;
        Horse parent2 = null;
        for (Horse horse : horses) {
            if (!horse.isBaby()) {
                if (parent1 == null) {
                    parent1 = horse;
                } else {
                    parent2 = horse;
                    break;
                }
            }
        }

        if (parent1 == null || parent2 == null) {
            return;
        }

        // Move to first parent
        double distSq = citizen.distanceToSqr(parent1);
        if (distSq > FEED_RANGE * FEED_RANGE) {
            if (citizen instanceof Mob mob) {
                mob.getNavigation().moveTo(parent1, 1.0);
            }
            return;
        }

        // Trigger breeding on both parents
        ItemStack breedingItem = getBreedingItem(stables.getBuildingLevel());
        parent1.setInLove(null);
        parent2.setInLove(null);

        // Mark breeding cooldown in stables
        stables.onBreedingPerformed();

        actionCooldown = BREED_COOLDOWN_TICKS;
        ticksSinceAction = 0;

        MountedKnightMod.LOGGER.info("Horse breeding initiated at stables level {}",
                stables.getBuildingLevel());
    }

    private void handleHealing(@NotNull ServerLevel level, @NotNull BuildingStables stables,
                               @NotNull LivingEntity citizen) {
        List<Horse> horses = getHorsesNearStables(level, stables);
        Horse injured = null;

        for (Horse horse : horses) {
            if (horse.getHealth() < horse.getMaxHealth() * 0.5f) {
                injured = horse;
                break;
            }
        }

        if (injured == null) {
            return;
        }

        double distSq = citizen.distanceToSqr(injured);
        if (distSq > FEED_RANGE * FEED_RANGE) {
            if (citizen instanceof Mob mob) {
                mob.getNavigation().moveTo(injured, 1.2); // Move faster for healing
            }
        } else {
            // Heal more than regular feeding
            float healAmount = stables.getBuildingLevel() >= 4 ? 8.0F : 6.0F;
            injured.heal(healAmount);
            actionCooldown = FEED_COOLDOWN_TICKS;
            ticksSinceAction = 0;

            MountedKnightMod.LOGGER.debug("Breeder healed horse at {}",
                    injured.blockPosition().toShortString());
        }
    }

    private void handleCollecting(@NotNull ServerLevel level, @NotNull BuildingStables stables,
                                  @NotNull LivingEntity citizen) {
        // Move back toward stables center to collect items from rack/chest
        BlockPos stablePos = stables.getPosition();
        double distSq = citizen.blockPosition().distSqr(stablePos);

        if (distSq > 9.0) {
            if (citizen instanceof Mob mob) {
                mob.getNavigation().moveTo(stablePos.getX(), stablePos.getY(), stablePos.getZ(), 1.0);
            }
        } else {
            // Simulate picking up items from stables storage
            actionCooldown = 60;
            ticksSinceAction = 0;
        }
    }

    private void handlePatrolling(@NotNull ServerLevel level, @NotNull BuildingStables stables,
                                  @NotNull LivingEntity citizen) {
        BlockPos stablePos = stables.getPosition();
        // Randomly walk around the stables
        double angle = level.random.nextDouble() * 2 * Math.PI;
        double radius = 5.0 + level.random.nextDouble() * 8.0;
        BlockPos wanderTarget = stablePos.offset(
                (int) (Math.cos(angle) * radius), 0,
                (int) (Math.sin(angle) * radius)
        );

        if (citizen instanceof Mob mob) {
            mob.getNavigation().moveTo(wanderTarget.getX(), wanderTarget.getY(), wanderTarget.getZ(), 0.8);
        }

        ticksSinceAction = 0;
        actionCooldown = 100; // Wait 5s before next patrol step
    }

    // --- Utility ---

    private boolean needsMoreItems(@NotNull LivingEntity citizen) {
        // Check if breeder needs to restock on food items
        // In a full implementation, check citizen inventory for wheat/golden apples
        return false;
    }

    /**
     * Get the appropriate breeding item for the stables level.
     * Levels 1-3: Wheat (golden carrots)
     * Levels 4-5: Golden Apples (enchanted golden apples for level 5)
     */
    private ItemStack getBreedingItem(int level) {
        if (level >= 5) {
            return new ItemStack(Items.ENCHANTED_GOLDEN_APPLE);
        } else if (level >= 4) {
            return new ItemStack(Items.GOLDEN_APPLE);
        } else {
            return new ItemStack(Items.GOLDEN_CARROT);
        }
    }

    public BreederState getCurrentState() {
        return currentState;
    }
}
