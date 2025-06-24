package com.example.bossmobenhancer.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class TileEntityEnshroudedBeacon extends TileEntityBeacon {

    private static final double EFFECT_RADIUS = 128.0D;

    // ✅ Selected curse index
    private int currentCurseId = -1;

    @Override
    public void update() {
        super.update();

        if (!this.world.isRemote && currentCurseId >= 0) {
            BlockPos pos = this.getPos();
            AxisAlignedBB aabb = new AxisAlignedBB(pos).grow(EFFECT_RADIUS);

            for (Entity entity : this.world.getEntitiesWithinAABB(EntityLiving.class, aabb)) {
                if (entity instanceof EntityPlayer) continue;

                EntityLiving mob = (EntityLiving) entity;

                switch (currentCurseId) {
                    case 0: // Withering Touch
                        mob.addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 0));
                        break;
                    case 1: // Blindness Hex
                        mob.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 200, 0));
                        break;
                    case 2: // Sluggish Curse
                        mob.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 1));
                        mob.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 1));
                        break;
                    case 3: // Vampiric Fog
                        mob.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 0));
                        break;
                    case 4: // Curse of Static
                        mob.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, 60, 0));
                        break;
                    case 5: // Terror Pulse
                        mob.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
                        mob.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 200, 0));
                        break;
                    case 6: // Hex of Hunger
                        mob.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 1));
                        break;
                    case 7: // Doombrand
                        mob.setFire(3);
                        mob.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 100, 0));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void setActiveCurse(int curseId) {
        this.currentCurseId = curseId;
        this.markDirty();
    }

    public int getCurseId() {
        return this.currentCurseId;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.currentCurseId = compound.getInteger("CurseId");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CurseId", this.currentCurseId);
        return compound;
    }
}