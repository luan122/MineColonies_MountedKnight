package com.mountedknight.colony.jobs;

import com.minecolonies.api.colony.ICitizenData;
import com.minecolonies.core.colony.jobs.AbstractJob;
import com.minecolonies.core.entity.ai.workers.AbstractAISkeleton;
import org.jetbrains.annotations.NotNull;

/**
 * Job: Horse Breeder
 * The worker assigned to a Stables building.
 *
 * Responsibilities:
 * - Feed horses (wheat) to heal them
 * - Breed horses when stable has capacity
 * - Tame wild horses found near the stable
 * - Keep horses within the stable area
 *
 * AI is handled by MineColonies' citizen system via the building assignment.
 */
public class JobHorseBreeder extends AbstractJob<AbstractAISkeleton<JobHorseBreeder>, JobHorseBreeder> {

    public static final String JOB_NAME = "horsebreeder";

    public JobHorseBreeder(@NotNull ICitizenData entity) {
        super(entity);
    }

    @Override
    public AbstractAISkeleton<JobHorseBreeder> generateAI() {
        return null;
    }
}
