package com.mountedknight.colony.ai;

import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard;
import com.mountedknight.colony.jobs.JobMountedKnight;
import org.jetbrains.annotations.NotNull;

/**
 * Guard AI for the Mounted Knight.
 * Extends MineColonies' built-in guard AI so it participates in the standard
 * patrol / follow / guard-tower targeting behaviour.
 *
 * Horse-mounting and lance-charge logic will be layered on top via
 * future state-machine extensions once the base registration is proven.
 */
public class EntityAIMountedKnight extends AbstractEntityAIGuard<JobMountedKnight, AbstractBuildingGuards> {

    public EntityAIMountedKnight(@NotNull JobMountedKnight job) {
        super(job);
    }

    @Override
    public Class<AbstractBuildingGuards> getExpectedBuildingClass() {
        return AbstractBuildingGuards.class;
    }
}
