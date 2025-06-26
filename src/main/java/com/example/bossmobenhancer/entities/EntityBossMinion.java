package com.example.bossmobenhancer.entities;

import com.example.bossmobenhancer.MainMod;
import com.example.bossmobenhancer.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A Wither‐skeleton minion whose attributes scale with tier,
 * and whose nameplate obeys the server config.
 */
public class EntityBossMinion extends EntitySkeleton {
    private static final Logger LOGGER = LogManager.getLogger(MainMod.MODID);

    /** Tier synced to clients via data manager. */
    private static final DataParameter<Integer> TIER =
            EntityDataManager.createKey(EntityBossMinion.class, DataSerializers.VARINT);

    /** Link back to the boss that spawned us (server‐side only). */
    private EntityLiving bossOwner;

    /** Server‐only source of truth for tier. */
    private int tier = 1;

    public EntityBossMinion(World worldIn) {
        super(worldIn);
        setSize(0.72F, 2.535F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        // register the tier parameter and default to 1
        this.dataManager.register(TIER, Integer.valueOf(tier));
        forceWitherSkeletonType();
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        if (world.isRemote) return;
        // Base stats
        IAttributeInstance healthAttr = getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        IAttributeInstance speedAttr  = getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        IAttributeInstance dmgAttr    = getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        if (healthAttr != null) healthAttr.setBaseValue(20.0D);
        if (speedAttr  != null) speedAttr.setBaseValue(0.3D);
        if (dmgAttr    != null) dmgAttr.setBaseValue(4.0D);

        // Immediately heal to max
        setHealth((float)getMaxHealth());
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        // sync client tier → server field and update visuals
        int observed = this.dataManager.get(TIER);
        if (observed != this.tier) {
            this.tier = observed;
            updateNameplate();
            applyTierAttributes();
        }
    }

    /**
     * Server‐only: set the boss owner.
     */
    public void setBossOwner(@Nonnull EntityLiving owner) {
        if (!world.isRemote && owner != null) {
            this.bossOwner = owner;
        }
    }

    @Nullable
    public EntityLiving getBossOwner() {
        return bossOwner;
    }

    /**
     * SERVER‐ONLY: update tier on both server & clients.
     */
    public void setTier(int newTier) {
        if (world.isRemote) return;
        this.tier = newTier;
        this.dataManager.set(TIER, Integer.valueOf(newTier));
        applyTierAttributes();
        updateNameplate();
    }

    public int getTier() {
        return this.tier;
    }

    /**
     * Scale health & damage according to tier multiplier.
     */
    protected void applyTierAttributes() {
        if (world.isRemote) return;
        double hMul = 1.0 + tier * 0.5;
        double dMul = 1.0 + tier * 0.3;

        IAttributeInstance healthAttr = getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH);
        IAttributeInstance dmgAttr    = getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);

        if (healthAttr != null) healthAttr.setBaseValue(20.0D * hMul);
        if (dmgAttr    != null) dmgAttr.setBaseValue(4.0D  * dMul);

        // heal up to new max
        setHealth((float)getMaxHealth());
    }

    /**
     * SERVER‐ONLY: toggle nameplate based on config.
     */
    private void updateNameplate() {
        if (world.isRemote) return;

        if (ConfigHandler.showBossNamePlate) {
            setCustomNameTag("Minion (Tier " + tier + ")");
            setAlwaysRenderNameTag(true);
        } else {
            setCustomNameTag("");
            setAlwaysRenderNameTag(false);
        }
    }

    /**
     * Heavy‐knockback melee attack (server side).
     */
    @Override
    public boolean attackEntityAsMob(@Nonnull net.minecraft.entity.Entity target) {
        if (world.isRemote) return false;

        boolean hit = super.attackEntityAsMob(target);
        if (hit && target instanceof EntityLivingBase) {
            EntityLivingBase victim = (EntityLivingBase) target;
            double dx = victim.posX - this.posX;
            double dz = victim.posZ - this.posZ;
            victim.knockBack(this, 10.0F, dx, dz);
        }
        return hit;
    }

    /**
     * No default loot by default.
     */
    @Nullable
    @Override
    protected ResourceLocation getLootTable() {
        return null;
    }

    /**
     * Reflection helper to force this skeleton into Wither variant.
     */
    private void forceWitherSkeletonType() {
        try {
            Method setType = EntitySkeleton.class
                    .getDeclaredMethod("setSkeletonType", int.class);
            setType.setAccessible(true);
            setType.invoke(this, 1);
        } catch (Exception e) {
            try {
                Field keyField = EntitySkeleton.class
                        .getDeclaredField("SKELETON_TYPE");
                keyField.setAccessible(true);
                @SuppressWarnings("unchecked")
                DataParameter<Integer> key =
                        (DataParameter<Integer>) keyField.get(null);
                this.dataManager.set(key, Integer.valueOf(1));
            } catch (Exception ex) {
                LOGGER.error("Failed to force Wither Skeleton type", ex);
            }
        }
    }

    /**
     * Persist our custom data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("bossMinionTier", tier);
    }

    /**
     * Read our custom data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("bossMinionTier")) {
            this.tier = compound.getInteger("bossMinionTier");
            this.dataManager.set(TIER, tier);
            applyTierAttributes();
            updateNameplate();
        }
    }
}