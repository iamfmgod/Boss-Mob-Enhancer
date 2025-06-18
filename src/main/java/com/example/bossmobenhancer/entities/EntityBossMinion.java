package com.example.bossmobenhancer.entities;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * EntityBossMinion is a custom minion entity used by the Boss Mob Enhancer mod.
 * It stores a reference to its boss owner and a tier level to scale its attributes.
 */
public class EntityBossMinion extends EntityMob {
    private EntityLiving bossOwner;
    private int tier = 1;

    public EntityBossMinion(World worldIn) {
        super(worldIn);
        // Set the entity's size. Adjust if needed.
        this.setSize(0.6F, 1.95F);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // Base attributes; these will be scaled based on tier.
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    /**
     * Sets the boss owner for this minion.
     *
     * @param bossOwner The boss that spawned this minion.
     */
    public void setBossOwner(EntityLiving bossOwner) {
        this.bossOwner = bossOwner;
    }

    /**
     * Gets the boss owner of this minion.
     *
     * @return The boss entity.
     */
    public EntityLiving getBossOwner() {
        return this.bossOwner;
    }

    /**
     * Sets the tier for this minion.
     * Higher tiers will have increased attributes.
     *
     * @param tier The tier level.
     */
    public void setTier(int tier) {
        this.tier = tier;
        applyTierAttributes();
    }

    /**
     * Returns the current tier.
     *
     * @return The tier level.
     */
    public int getTier() {
        return this.tier;
    }

    /**
     * Scales attributes based on the minion's tier.
     */
    protected void applyTierAttributes() {
        double baseHealth = 20.0D;
        double healthMultiplier = 1.0D + (tier * 0.5D); // Increase health 50% per tier.
        double baseDamage = 4.0D;
        double damageMultiplier = 1.0D + (tier * 0.3D); // Increase damage 30% per tier.

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(baseHealth * healthMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(baseDamage * damageMultiplier);
    }

    /**
     * Returns the loot table for the minion.
     * Currently returns null so no loot is dropped.
     */
    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }
}