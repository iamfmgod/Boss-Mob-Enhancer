package com.example.bossmobenhancer.entities;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * EntityBossMinion is a custom minion entity used by the Boss Mob Enhancer mod.
 * It extends EntitySkeleton so that it uses the default Wither Skeleton model/texture.
 * The skeleton type is forced to 1 (Wither Skeleton) using reflection.
 * Its name is set to "Minion" (or hidden when the config toggle is off),
 * and its melee attacks apply a heavy knockback of strength 10.
 */
public class EntityBossMinion extends EntitySkeleton {
    private EntityLiving bossOwner;
    private int tier = 1;

    public EntityBossMinion(World worldIn) {
        super(worldIn);
        // Set the entity's bounding box dimensions.
        this.setSize(0.6F, 1.95F);

        // Force Wither Skeleton appearance using our reflection hack instead of setSkeletonType(1).
        setWitherSkeletonType();

        // Initialize the nameplate based on the config.
        updateNameplate();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // Set base attributes.
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    /**
     * Attempts to force the skeleton type to 1 (Wither Skeleton).
     * It first tries to call "setSkeletonType(int)" via reflection.
     * If that method is not available, it attempts to set the data parameter directly.
     */
    private void setWitherSkeletonType() {
        try {
            // Try to invoke the method by its deobfuscated name.
            Method method = EntitySkeleton.class.getDeclaredMethod("setSkeletonType", int.class);
            method.setAccessible(true);
            method.invoke(this, 1);
        } catch (NoSuchMethodException e) {
            // If the method doesn't exist, try setting the field directly.
            try {
                Field field = EntitySkeleton.class.getDeclaredField("SKELETON_TYPE");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                DataParameter<Integer> dataParam = (DataParameter<Integer>) field.get(null);
                this.dataManager.set(dataParam, 1);
                // Optionally adjust size for wither skeleton (if desired).
                this.setSize(0.72F, 2.535F);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
     * Sets the tier for this minion, scales its attributes accordingly,
     * and updates the nameplate.
     *
     * @param tier The tier level.
     */
    public void setTier(int tier) {
        this.tier = tier;
        applyTierAttributes();
        updateNameplate();
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
     * Health increases by 50% per tier and attack damage by 30% per tier.
     */
    protected void applyTierAttributes() {
        double baseHealth = 20.0D;
        double healthMultiplier = 1.0D + (tier * 0.5D);
        double baseDamage = 4.0D;
        double damageMultiplier = 1.0D + (tier * 0.3D);

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH)
                .setBaseValue(baseHealth * healthMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)
                .setBaseValue(baseDamage * damageMultiplier);
    }

    /**
     * Updates the entity's nameplate based on the configuration.
     * If nameplates are enabled, the custom name is set to "Minion" and always rendered;
     * otherwise, the custom name is cleared and nameplate rendering is disabled.
     */
    private void updateNameplate() {
        if (ConfigHandler.showBossNamePlate) {
            this.setCustomNameTag("Minion");
            this.setAlwaysRenderNameTag(true);
        } else {
            this.setCustomNameTag("");
            this.setAlwaysRenderNameTag(false);
        }
    }

    /**
     * Overrides the standard attack to deliver a heavy knockback.
     * If the attack is successful, the target is knocked back with a strength of 10.
     */
    @Override
    public boolean attackEntityAsMob(net.minecraft.entity.Entity entityIn) {
        boolean result = super.attackEntityAsMob(entityIn);
        if (result && entityIn instanceof EntityLivingBase) {
            // Compute the directional vector for knockback from the difference between positions.
            double dX = entityIn.posX - this.posX;
            double dZ = entityIn.posZ - this.posZ;
            ((EntityLivingBase) entityIn).knockBack(this, 10.0F, dX, dZ);
        }
        return result;
    }

    /**
     * Returns the loot table for the minion.
     * Currently returns null so that no loot is dropped.
     */
    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }
}