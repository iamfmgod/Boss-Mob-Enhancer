package com.example.bossmobenhancer.entities;

import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityBossMinion extends EntityWitherSkeleton {

    private EntityLiving bossOwner;
    private int tier = 1;

    public EntityBossMinion(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.95F); // Custom hitbox size (optional)
        this.updateNameplate();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0);
    }

    public void setBossOwner(EntityLiving bossOwner) {
        this.bossOwner = bossOwner;
    }

    public EntityLiving getBossOwner() {
        return this.bossOwner;
    }

    public void setTier(int tier) {
        this.tier = tier;
        this.applyTierAttributes();
        this.updateNameplate();
    }

    public int getTier() {
        return this.tier;
    }

    protected void applyTierAttributes() {
        double baseHealth = 20.0;
        double healthMultiplier = 1.0 + this.tier * 0.5;
        double baseDamage = 4.0;
        double damageMultiplier = 1.0 + this.tier * 0.3;

        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(baseHealth * healthMultiplier);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(baseDamage * damageMultiplier);
    }

    private void updateNameplate() {
        if (ConfigHandler.showBossNamePlate) {
            this.setCustomNameTag("Minion");
            this.setAlwaysRenderNameTag(true);
        } else {
            this.setCustomNameTag("");
            this.setAlwaysRenderNameTag(false);
        }
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        boolean result = super.attackEntityAsMob(entityIn);
        if (result && entityIn instanceof EntityLivingBase) {
            double dX = entityIn.posX - this.posX;
            double dZ = entityIn.posZ - this.posZ;
            ((EntityLivingBase) entityIn).knockBack(this, 10.0F, dX, dZ);
        }
        return result;
    }

    @Override
    protected ResourceLocation getLootTable() {
        return null; // Override with a loot table if needed
    }
}
