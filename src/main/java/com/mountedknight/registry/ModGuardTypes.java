package com.mountedknight.registry;

import com.minecolonies.api.colony.guardtype.GuardType;
import com.minecolonies.api.entity.citizen.Skill;
import com.mountedknight.MountedKnightMod;
import com.mountedknight.colony.jobs.JobMountedKnight;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registers guard types into MineColonies' custom guard type registry ("minecolonies:guardtypes").
 * Guard types appear in the guard tower/barracks UI for assigning guard roles.
 */
public class ModGuardTypes {

    public static final String MOUNTED_KNIGHT_ID = "mountedknight";

    private static final ResourceKey<Registry<GuardType>> GUARD_TYPE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("minecolonies", "guardtypes"));

    public static final DeferredRegister<GuardType> GUARD_TYPE_ENTRIES =
            DeferredRegister.create(GUARD_TYPE_REGISTRY_KEY, MountedKnightMod.MOD_ID);

    @SuppressWarnings("unchecked")
    public static final DeferredHolder<GuardType, GuardType> MOUNTED_KNIGHT =
            GUARD_TYPE_ENTRIES.register(MOUNTED_KNIGHT_ID, () ->
                    new GuardType.Builder()
                            .setJobEntry(ModJobs.MOUNTED_KNIGHT::get)
                            .setJobTranslationKey("com." + MountedKnightMod.MOD_ID + ".job.mountedknight")
                            .setButtonTranslationKey("com." + MountedKnightMod.MOD_ID + ".guard.mountedknight")
                            .setPrimarySkill(Skill.Strength)
                            .setSecondarySkill(Skill.Agility)
                            .setWorkerSoundName("mounted_knight")
                            .setRegistryName(ResourceLocation.fromNamespaceAndPath(MountedKnightMod.MOD_ID, MOUNTED_KNIGHT_ID))
                            .setClazz(JobMountedKnight.class)
                            .createGuardType()
            );

    public static void register(IEventBus modEventBus) {
        GUARD_TYPE_ENTRIES.register(modEventBus);
    }
}
