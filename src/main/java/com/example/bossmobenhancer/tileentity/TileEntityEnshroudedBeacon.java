package com.example.bossmobenhancer.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * An “enshrouded” beacon that periodically applies a selected curse
 * (potion effects or fire) to all nearby hostile mobs.
 */
public class TileEntityEnshroudedBeacon extends TileEntityBeacon implements ITickable {
    private static final double EFFECT_RADIUS = 128D;
    private static final String NBT_CURSE      = "CurseId";
    private static final int    TICKS_PER_USE  = 20;

    /** –1 means no curse selected */
    private int currentCurseId = -1;
    private int tickTimer      = 0;

    @Override
    public void update() {
        if (world.isRemote || currentCurseId < 0) return;
        if (++tickTimer < TICKS_PER_USE) return;
        tickTimer = 0;

        BlockPos origin = getPos();
        AxisAlignedBB area = new AxisAlignedBB(origin).grow(EFFECT_RADIUS);
        for (EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, area)) {
            // skip players and non-living mobs
            if (e instanceof EntityPlayer || !(e instanceof EntityLiving)) continue;
            applyCurse((EntityLiving) e);
        }
    }

    /** Applies the currently selected curse to a single mob. */
    private void applyCurse(EntityLiving mob) {
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
                // no-op
        }
    }

    /**
     * Called from your packet handler on the server when the player selects a new curse.
     */
    public void setActiveCurse(int curseId) {
        this.currentCurseId = curseId;
        markDirty();

        // Sync to clients
        if (!world.isRemote) {
            IBlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    public int getCurseId() {
        return currentCurseId;
    }

    // ---- NBT persistence ----

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.currentCurseId = nbt.getInteger(NBT_CURSE);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger(NBT_CURSE, this.currentCurseId);
        return nbt;
    }

    // ---- Networking: tile‐entity sync ----

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = super.getUpdateTag();
        tag.setInteger(NBT_CURSE, this.currentCurseId);
        return tag;
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }
}