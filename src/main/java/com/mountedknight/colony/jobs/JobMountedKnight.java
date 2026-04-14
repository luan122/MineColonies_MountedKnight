package com.mountedknight.colony.jobs;

import com.minecolonies.api.client.render.modeltype.ModModelTypes;
import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.colony.buildings.AbstractBuildingGuards;
import com.minecolonies.core.colony.jobs.AbstractJobGuard;
import com.minecolonies.core.entity.ai.workers.guard.AbstractEntityAIGuard;
import com.mountedknight.colony.ai.EntityAIMountedKnight;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

/**
 * Job: Mounted Knight
 * A guard-type job where the colonist fights while riding a horse.
 * Extends AbstractJobGuard to integrate with MineColonies' guard system
 * (guard towers, barracks, patrol/follow/guard modes, etc.).
 */
public class JobMountedKnight extends AbstractJobGuard<JobMountedKnight> {

    public static final String JOB_NAME = "mountedknight";

    public JobMountedKnight(ICitizenData citizen) {
        super(citizen);
    }

    @Override
    protected AbstractEntityAIGuard<JobMountedKnight, ? extends AbstractBuildingGuards> generateGuardAI() {
        return new EntityAIMountedKnight(this);
    }

    @NotNull
    @Override
    public ResourceLocation getModel() {
        return ModModelTypes.KNIGHT_GUARD_ID;
    }
}
