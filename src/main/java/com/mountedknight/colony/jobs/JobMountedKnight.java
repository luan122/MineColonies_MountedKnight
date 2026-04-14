package com.mountedknight.colony.jobs;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.buildings.BuildingStables;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Job: Mounted Knight
 * A guard-type job where the colonist fights while riding a horse.
 *
 * Combat modes (determined by equipped weapon):
 * - LANCER: Equips a lance, charges into melee combat on horseback
 * - MOUNTED_ARCHER: Equips a bow, fires arrows while mounted
 *
 * Lifecycle:
 * 1. Spawns at guard tower/barracks
 * 2. Walks to nearest Stables building to acquire a horse
 * 3. Mounts horse and patrols assigned area
 * 4. Engages hostiles (melee lance or ranged bow depending on equipment)
 * 5. If horse dies: fights on foot, then returns to stables for new horse
 * 6. If low health: returns to home building to heal
 */
public class JobMountedKnight {

    public static final String JOB_NAME = "mountedknight";

    // Damage multiplier when attacking while mounted (lance charge bonus)
    public static final float MOUNTED_MELEE_MULTIPLIER = 1.5F;

    // Range at which mounted archers will engage
    public static final double MOUNTED_ARCHER_RANGE = 20.0;

    // Range at which lancers will charge
    public static final double LANCER_CHARGE_RANGE = 15.0;

    private MountedKnightState currentState = MountedKnightState.IDLE;
    private CombatMode combatMode = CombatMode.LANCER;

    @Nullable
    private Horse mountedHorse;

    @Nullable
    private LivingEntity currentTarget;

    @Nullable
    private BlockPos homePosition;

    @Nullable
    private BuildingStables assignedStables;

    public enum MountedKnightState {
        IDLE,                   // At home building, no task
        SEEKING_HORSE,          // Walking to stables to get a horse
        MOUNTING,               // In the process of mounting a horse
        PATROLLING,             // Patrolling assigned area while mounted
        ENGAGING_MELEE,         // Charging target with lance
        ENGAGING_RANGED,        // Shooting arrows at target from horseback
        DISMOUNTED_COMBAT,      // Horse died, fighting on foot
        RETURNING_TO_STABLES,   // Going back to get a new horse
        RETURNING_HOME          // Going back to home building to heal
    }

    public enum CombatMode {
        LANCER,          // Melee with lance
        MOUNTED_ARCHER   // Ranged with bow
    }

    public JobMountedKnight() {
    }

    /**
     * Determine the combat mode based on equipped weapon.
     */
    public CombatMode determineCombatMode(@NotNull LivingEntity citizen) {
        ItemStack mainHand = citizen.getItemBySlot(EquipmentSlot.MAINHAND);
        if (mainHand.getItem() instanceof BowItem) {
            return CombatMode.MOUNTED_ARCHER;
        }
        return CombatMode.LANCER;
    }

    /**
     * Main AI tick - determines what the mounted knight should do next.
     */
    public MountedKnightState determineNextState(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        // If citizen is dead, reset
        if (!citizen.isAlive()) {
            return MountedKnightState.IDLE;
        }

        // If low health, return home to heal
        if (citizen.getHealth() < citizen.getMaxHealth() * 0.25F) {
            return MountedKnightState.RETURNING_HOME;
        }

        // If we have a target, engage
        if (currentTarget != null && currentTarget.isAlive()) {
            if (isMounted()) {
                combatMode = determineCombatMode(citizen);
                return combatMode == CombatMode.MOUNTED_ARCHER
                        ? MountedKnightState.ENGAGING_RANGED
                        : MountedKnightState.ENGAGING_MELEE;
            } else {
                return MountedKnightState.DISMOUNTED_COMBAT;
            }
        }

        // If not mounted and no target, seek a horse
        if (!isMounted()) {
            if (assignedStables != null) {
                return MountedKnightState.SEEKING_HORSE;
            }
            return MountedKnightState.IDLE;
        }

        // Mounted with no target - patrol
        return MountedKnightState.PATROLLING;
    }

    /**
     * Mount a horse. The citizen entity will ride the horse.
     */
    public boolean mountHorse(@NotNull LivingEntity citizen, @NotNull Horse horse) {
        if (!horse.isTamed() || horse.isVehicle() || !horse.isAlive()) {
            return false;
        }

        boolean success = citizen.startRiding(horse, true);
        if (success) {
            this.mountedHorse = horse;
            this.currentState = MountedKnightState.PATROLLING;
            MountedKnightMod.LOGGER.debug("Mounted knight {} mounted horse at {}",
                    citizen.getName().getString(), horse.blockPosition());
        }
        return success;
    }

    /**
     * Dismount from current horse.
     */
    public void dismount(@NotNull LivingEntity citizen) {
        citizen.stopRiding();
        this.mountedHorse = null;
        MountedKnightMod.LOGGER.debug("Mounted knight {} dismounted", citizen.getName().getString());
    }

    /**
     * Called when the horse dies in combat.
     * Knight continues fighting on foot, then returns to stables.
     */
    public void onHorseDeath() {
        this.mountedHorse = null;
        if (currentTarget != null && currentTarget.isAlive()) {
            this.currentState = MountedKnightState.DISMOUNTED_COMBAT;
        } else {
            this.currentState = MountedKnightState.RETURNING_TO_STABLES;
        }
        MountedKnightMod.LOGGER.debug("Mounted knight's horse died, switching to foot combat");
    }

    /**
     * Calculate melee damage considering mounted bonus.
     */
    public float calculateMeleeDamage(float baseDamage) {
        if (isMounted()) {
            return baseDamage * MOUNTED_MELEE_MULTIPLIER;
        }
        return baseDamage;
    }

    /**
     * Check if the knight is currently mounted on a horse.
     */
    public boolean isMounted() {
        return mountedHorse != null && mountedHorse.isAlive();
    }

    // --- Getters and Setters ---

    @NotNull
    public MountedKnightState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(MountedKnightState state) {
        this.currentState = state;
    }

    @NotNull
    public CombatMode getCombatMode() {
        return combatMode;
    }

    public void setCombatMode(CombatMode mode) {
        this.combatMode = mode;
    }

    @Nullable
    public Horse getMountedHorse() {
        return mountedHorse;
    }

    @Nullable
    public LivingEntity getCurrentTarget() {
        return currentTarget;
    }

    public void setCurrentTarget(@Nullable LivingEntity target) {
        this.currentTarget = target;
    }

    @Nullable
    public BlockPos getHomePosition() {
        return homePosition;
    }

    public void setHomePosition(@Nullable BlockPos pos) {
        this.homePosition = pos;
    }

    @Nullable
    public BuildingStables getAssignedStables() {
        return assignedStables;
    }

    public void setAssignedStables(@Nullable BuildingStables stables) {
        this.assignedStables = stables;
    }
}
