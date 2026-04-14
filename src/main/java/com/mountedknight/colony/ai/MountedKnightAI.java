package com.mountedknight.colony.ai;

import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.buildings.BuildingStables;
import com.mountedknight.colony.jobs.JobMountedKnight;
import com.mountedknight.colony.jobs.JobMountedKnight.CombatMode;
import com.mountedknight.colony.jobs.JobMountedKnight.MountedKnightState;
import com.mountedknight.items.LanceItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * AI Controller for Mounted Knight behavior.
 * Manages the full lifecycle: seek horse -> mount -> patrol -> engage -> return.
 *
 * This AI works on top of MineColonies' citizen entity system and Horse pathfinding.
 * The horse's pathfinding handles movement; this AI determines targets and actions.
 */
public class MountedKnightAI {

    private static final double PATROL_RADIUS = 40.0;
    private static final double DETECTION_RANGE = 30.0;
    private static final double MELEE_ATTACK_RANGE = 3.5;
    private static final double RANGED_ATTACK_RANGE = 20.0;
    private static final int ARROW_COOLDOWN_TICKS = 30; // 1.5 seconds

    private final JobMountedKnight job;
    private int arrowCooldown = 0;
    private int ticksSinceLastAction = 0;

    public MountedKnightAI(@NotNull JobMountedKnight job) {
        this.job = job;
    }

    /**
     * Called every tick to update the knight's AI behavior.
     */
    public void tick(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        ticksSinceLastAction++;
        if (arrowCooldown > 0) {
            arrowCooldown--;
        }

        // Check if our horse is still alive
        if (job.isMounted() && job.getMountedHorse() != null && !job.getMountedHorse().isAlive()) {
            job.onHorseDeath();
        }

        // Scan for threats
        LivingEntity threat = scanForThreats(level, citizen);
        if (threat != null) {
            job.setCurrentTarget(threat);
        } else if (job.getCurrentTarget() != null && !job.getCurrentTarget().isAlive()) {
            job.setCurrentTarget(null);
        }

        // Determine and execute state
        MountedKnightState nextState = job.determineNextState(level, citizen);
        job.setCurrentState(nextState);

        switch (nextState) {
            case IDLE -> handleIdle(citizen);
            case SEEKING_HORSE -> handleSeekHorse(level, citizen);
            case MOUNTING -> handleMounting(level, citizen);
            case PATROLLING -> handlePatrolling(level, citizen);
            case ENGAGING_MELEE -> handleMeleeAttack(level, citizen);
            case ENGAGING_RANGED -> handleRangedAttack(level, citizen);
            case DISMOUNTED_COMBAT -> handleDismountedCombat(level, citizen);
            case RETURNING_TO_STABLES -> handleReturnToStables(level, citizen);
            case RETURNING_HOME -> handleReturnHome(citizen);
        }
    }

    /**
     * Scan for hostile entities within detection range.
     */
    @Nullable
    private LivingEntity scanForThreats(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        BlockPos pos = citizen.blockPosition();
        AABB searchArea = new AABB(
                pos.getX() - DETECTION_RANGE, pos.getY() - 5, pos.getZ() - DETECTION_RANGE,
                pos.getX() + DETECTION_RANGE, pos.getY() + 5, pos.getZ() + DETECTION_RANGE
        );

        List<Monster> monsters = level.getEntitiesOfClass(Monster.class, searchArea);
        if (monsters.isEmpty()) {
            return null;
        }

        // Find closest monster
        Monster closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Monster monster : monsters) {
            double dist = citizen.distanceToSqr(monster);
            if (dist < closestDist) {
                closestDist = dist;
                closest = monster;
            }
        }
        return closest;
    }

    // --- State Handlers ---

    private void handleIdle(@NotNull LivingEntity citizen) {
        // Stay at home position, await orders
        ticksSinceLastAction = 0;
    }

    private void handleSeekHorse(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        BuildingStables stables = job.getAssignedStables();
        if (stables == null) {
            return;
        }

        // Try to find an available horse
        Horse horse = stables.getAvailableHorse(level);
        if (horse != null) {
            // Move toward the horse
            double dist = citizen.distanceToSqr(horse);
            if (dist < 4.0) { // Within reach
                job.mountHorse(citizen, horse);
            } else if (citizen instanceof Mob mob) {
                mob.getNavigation().moveTo(horse, 1.2);
            }
        } else {
            // No horses available, move toward stables and wait
            if (citizen instanceof Mob mob) {
                BlockPos stablePos = stables.getPosition();
                mob.getNavigation().moveTo(stablePos.getX(), stablePos.getY(), stablePos.getZ(), 1.0);
            }
        }
    }

    private void handleMounting(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        // Transition state - find horse and mount
        handleSeekHorse(level, citizen);
    }

    private void handlePatrolling(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        if (!job.isMounted()) {
            return;
        }

        Horse horse = job.getMountedHorse();
        if (horse == null) {
            return;
        }

        // Patrol: wander near the home position
        BlockPos home = job.getHomePosition();
        if (home == null) {
            home = citizen.blockPosition();
        }

        if (ticksSinceLastAction > 100) { // Every ~5 seconds, pick new patrol point
            double angle = level.random.nextDouble() * 2 * Math.PI;
            double radius = level.random.nextDouble() * PATROL_RADIUS;
            BlockPos patrolTarget = home.offset(
                    (int) (Math.cos(angle) * radius), 0,
                    (int) (Math.sin(angle) * radius)
            );

            horse.getNavigation().moveTo(patrolTarget.getX(), patrolTarget.getY(), patrolTarget.getZ(), 1.0);
            ticksSinceLastAction = 0;
        }
    }

    private void handleMeleeAttack(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        LivingEntity target = job.getCurrentTarget();
        if (target == null || !target.isAlive()) {
            job.setCurrentTarget(null);
            return;
        }

        double distSq = citizen.distanceToSqr(target);
        Horse horse = job.getMountedHorse();

        // Move toward target
        if (horse != null && distSq > MELEE_ATTACK_RANGE * MELEE_ATTACK_RANGE) {
            // Charge! Move at gallop speed
            horse.getNavigation().moveTo(target, 1.5);
        }

        // Attack if in range
        if (distSq <= MELEE_ATTACK_RANGE * MELEE_ATTACK_RANGE) {
            ItemStack weapon = citizen.getMainHandItem();
            float baseDamage = 6.0F; // base lance damage
            if (weapon.getItem() instanceof LanceItem) {
                baseDamage = 8.0F;
            }

            float finalDamage = job.calculateMeleeDamage(baseDamage);
            target.hurt(level.damageSources().mobAttack(citizen), finalDamage);

            MountedKnightMod.LOGGER.debug("Mounted knight lance attack for {} damage on {}",
                    finalDamage, target.getName().getString());
            ticksSinceLastAction = 0;
        }
    }

    private void handleRangedAttack(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        LivingEntity target = job.getCurrentTarget();
        if (target == null || !target.isAlive()) {
            job.setCurrentTarget(null);
            return;
        }

        double distSq = citizen.distanceToSqr(target);
        Horse horse = job.getMountedHorse();

        // Maintain distance for ranged combat
        if (horse != null) {
            if (distSq > RANGED_ATTACK_RANGE * RANGED_ATTACK_RANGE) {
                // Too far, move closer
                horse.getNavigation().moveTo(target, 1.2);
            } else if (distSq < 5.0 * 5.0) {
                // Too close for archery, move away
                Vec3 awayDir = citizen.position().subtract(target.position()).normalize().scale(10);
                BlockPos retreatPos = citizen.blockPosition().offset((int) awayDir.x, 0, (int) awayDir.z);
                horse.getNavigation().moveTo(retreatPos.getX(), retreatPos.getY(), retreatPos.getZ(), 1.3);
            }
        }

        // Shoot arrows if in range and cooldown is ready
        if (distSq <= RANGED_ATTACK_RANGE * RANGED_ATTACK_RANGE && arrowCooldown <= 0) {
            Arrow arrow = new Arrow(level, citizen, new ItemStack(net.minecraft.world.item.Items.ARROW), null);

            // Aim at target
            double dx = target.getX() - citizen.getX();
            double dy = target.getY() + target.getEyeHeight() - arrow.getY();
            double dz = target.getZ() - citizen.getZ();
            arrow.shoot(dx, dy, dz, 2.5F, 2.0F); // velocity, inaccuracy

            level.addFreshEntity(arrow);
            arrowCooldown = ARROW_COOLDOWN_TICKS;

            MountedKnightMod.LOGGER.debug("Mounted archer fired arrow at {}", target.getName().getString());
        }
    }

    private void handleDismountedCombat(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        // Horse is dead - fight on foot like a regular knight
        LivingEntity target = job.getCurrentTarget();
        if (target == null || !target.isAlive()) {
            job.setCurrentTarget(null);
            // No more threats, go get a new horse
            job.setCurrentState(MountedKnightState.RETURNING_TO_STABLES);
            return;
        }

        double distSq = citizen.distanceToSqr(target);

        // Move toward target on foot
        if (citizen instanceof Mob mob && distSq > MELEE_ATTACK_RANGE * MELEE_ATTACK_RANGE) {
            mob.getNavigation().moveTo(target, 1.0);
        }

        // Melee attack on foot (no mounted bonus)
        if (distSq <= MELEE_ATTACK_RANGE * MELEE_ATTACK_RANGE) {
            float baseDamage = 5.0F;
            target.hurt(level.damageSources().mobAttack(citizen), baseDamage);
        }
    }

    private void handleReturnToStables(@NotNull ServerLevel level, @NotNull LivingEntity citizen) {
        BuildingStables stables = job.getAssignedStables();
        if (stables == null) {
            job.setCurrentState(MountedKnightState.IDLE);
            return;
        }

        BlockPos stablePos = stables.getPosition();
        double distSq = citizen.blockPosition().distSqr(stablePos);

        if (distSq < 16.0) { // Within 4 blocks of stables
            // Try to get a new horse
            Horse horse = stables.getAvailableHorse(level);
            if (horse != null) {
                job.mountHorse(citizen, horse);
            }
            // else wait for a horse to become available
        } else if (citizen instanceof Mob mob) {
            mob.getNavigation().moveTo(stablePos.getX(), stablePos.getY(), stablePos.getZ(), 1.0);
        }
    }

    private void handleReturnHome(@NotNull LivingEntity citizen) {
        BlockPos home = job.getHomePosition();
        if (home == null) {
            return;
        }

        // Dismount if mounted
        if (job.isMounted()) {
            job.dismount(citizen);
        }

        if (citizen instanceof Mob mob) {
            mob.getNavigation().moveTo(home.getX(), home.getY(), home.getZ(), 1.0);
        }
    }
}
