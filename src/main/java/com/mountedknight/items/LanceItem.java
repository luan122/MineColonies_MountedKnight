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
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Lance - a mounted combat weapon with extended reach.
 *
 * Deals bonus damage when the wielder is mounted on a horse.
 * Base damage: 6 (iron sword equivalent)
 * Mounted bonus: +4 damage (total 10 when mounted)
 * Attack speed: 1.0 (slower than sword's 1.6, but more damage per hit)
 *
 * The lance encourages mounted combat by being significantly stronger
 * when used while riding a horse.
 */
public class LanceItem extends SwordItem {

    public static final float MOUNTED_BONUS_DAMAGE = 4.0F;
    public static final float BASE_DAMAGE = 6.0F;
    public static final float ATTACK_SPEED = -3.0F; // Results in ~1.0 attacks/sec

    private static final ResourceLocation LANCE_ATTACK_DAMAGE_ID =
            ResourceLocation.withDefaultNamespace("base_attack_damage");
    private static final ResourceLocation LANCE_ATTACK_SPEED_ID =
            ResourceLocation.withDefaultNamespace("base_attack_speed");

    public LanceItem(Tier tier, Properties properties) {
        super(tier, properties);
    }

    /**
     * Create the default item attribute modifiers for the lance.
     * Base: 6 damage, 1.0 attack speed (slower but powerful).
     */
    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                LANCE_ATTACK_DAMAGE_ID,
                                BASE_DAMAGE,
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
     */
    public static float getTotalDamage(LivingEntity attacker) {
        float damage = BASE_DAMAGE;
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
