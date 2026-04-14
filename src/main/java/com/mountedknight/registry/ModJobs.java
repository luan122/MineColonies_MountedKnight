package com.mountedknight.registry;

import com.minecolonies.api.colony.jobs.registry.JobEntry;
import com.minecolonies.core.colony.jobs.views.DefaultJobView;
import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.jobs.JobHorseBreeder;
import com.mountedknight.colony.jobs.JobMountedKnight;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers jobs into MineColonies' custom job registry ("minecolonies:jobs").
 */
public class ModJobs {

    public static final String HORSE_BREEDER_ID = "horsebreeder";
    public static final String MOUNTED_KNIGHT_ID = "mountedknight";

    private static final ResourceKey<Registry<JobEntry>> JOB_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecolonies", "jobs"));

    public static final DeferredRegister<JobEntry> JOB_ENTRIES =
            DeferredRegister.create(JOB_REGISTRY_KEY, MountedKnightMod.MOD_ID);

    public static final DeferredHolder<JobEntry, JobEntry> HORSE_BREEDER =
            JOB_ENTRIES.register(HORSE_BREEDER_ID, () ->
                    new JobEntry.Builder()
                            .setJobProducer(JobHorseBreeder::new)
                            .setJobViewProducer(() -> DefaultJobView::new)
                            .setRegistryName(ResourceLocation.fromNamespaceAndPath(MountedKnightMod.MOD_ID, HORSE_BREEDER_ID))
                            .createJobEntry()
            );

    public static final DeferredHolder<JobEntry, JobEntry> MOUNTED_KNIGHT =
            JOB_ENTRIES.register(MOUNTED_KNIGHT_ID, () ->
                    new JobEntry.Builder()
                            .setJobProducer(JobMountedKnight::new)
                            .setJobViewProducer(() -> DefaultJobView::new)
                            .setRegistryName(ResourceLocation.fromNamespaceAndPath(MountedKnightMod.MOD_ID, MOUNTED_KNIGHT_ID))
                            .createJobEntry()
            );

    public static void register(IEventBus modEventBus) {
        JOB_ENTRIES.register(modEventBus);
    }
}
