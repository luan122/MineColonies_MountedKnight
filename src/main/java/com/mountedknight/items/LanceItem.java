package com.mountedknight.items;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Lance - a mounted combat weapon with extended reach.
 *
 * Tier progression (base attack damage / mounted total):
 *   Wood:              3.0 / 7.0  (+4 mounted bonus)
 *   Stone:             4.0 / 8.0
 *   Iron:              5.0 / 9.0
 *   Gold:              3.0 / 7.0  (fast but weak, like gold sword)
 *   Diamond:           6.0 / 10.0
 *   Netherite:         7.0 / 11.0
 *   Osmium (Mek):      7.0 / 11.0  (same as netherite)
 *   Refined Obsidian: 10.5 / 14.5  (50% more than netherite)
 *
 * All lances have attack speed -3.0 (1.0 attacks/sec) — slower than swords
 * but compensated by the +4 mounted bonus and charge bonus at gallop.
 */
public class LanceItem extends SwordItem {

    public static final float MOUNTED_BONUS_DAMAGE = 4.0F;
    public static final float ATTACK_SPEED = -3.0F; // 1.0 attacks/sec

    private static final ResourceLocation LANCE_ATTACK_DAMAGE_ID =
            ResourceLocation.withDefaultNamespace("base_attack_damage");
    private static final ResourceLocation LANCE_ATTACK_SPEED_ID =
            ResourceLocation.withDefaultNamespace("base_attack_speed");

    private final float lanceDamage;

    public LanceItem(Tier tier, float attackDamage, Properties properties) {
        super(tier, properties);
        this.lanceDamage = attackDamage;
    }

    /**
     * Create attribute modifiers for a lance with the given base damage.
     */
    public static ItemAttributeModifiers createAttributes(float attackDamage) {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                LANCE_ATTACK_DAMAGE_ID,
                                attackDamage,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                LANCE_ATTACK_SPEED_ID,
                                ATTACK_SPEED,
                                AttributeModifier.Operation.ADD_VALUE
                        ),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    public float getLanceDamage() {
        return lanceDamage;
    }

    /**
     * Apply mounted bonus damage when attacking from horseback.
     */
    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (isRiderMounted(attacker)) {
            // Extra damage is applied via the AI controller (MountedKnightAI)
            // The AI checks if the attacker is mounted and applies the bonus
            // This vanilla method still triggers durability loss normally
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    /**
     * Check if the entity is currently riding a horse.
     */
    public static boolean isRiderMounted(LivingEntity entity) {
        return entity.getVehicle() instanceof AbstractHorse;
    }

    /**
     * Calculate the total damage including mounted bonus.
     * Uses the actual lance's base damage from its ItemStack.
     */
    public static float getTotalDamage(LivingEntity attacker, float baseDamage) {
        float damage = baseDamage;
        if (isRiderMounted(attacker)) {
            damage += MOUNTED_BONUS_DAMAGE;

            // Additional charge bonus based on horse speed
            if (attacker.getVehicle() instanceof AbstractHorse horse) {
                double speed = horse.getDeltaMovement().horizontalDistance();
                if (speed > 0.2) {
                    // Charge bonus: up to +3 damage at full gallop
                    damage += (float) Math.min(speed * 5.0, 3.0);
                }
            }
        }
        return damage;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.player.Player player) {
        // Cannot break blocks while on horseback - lance is for combat
        if (isRiderMounted(player)) {
            return false;
        }
        return super.canAttackBlock(state, level, pos, player);
    }
}
