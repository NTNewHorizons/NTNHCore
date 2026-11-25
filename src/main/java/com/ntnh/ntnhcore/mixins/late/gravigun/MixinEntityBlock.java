package com.ntnh.ntnhcore.mixins.late.gravigun;

import java.lang.ref.WeakReference;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.thomass47.gravitygunfix.GravityGunFix;
import com.ntnh.ntnhcore.modules.thomass47.gravitygunfix.IHeldBlock;

import gravigun.common.entity.EntityBlock;

@Mixin(EntityBlock.class)
public abstract class MixinEntityBlock extends Entity implements IHeldBlock {

    @Shadow(remap = false)
    public TileEntity tileEntity;

    @Unique
    private WeakReference<EntityPlayer> gravityGunFix$holder;

    @Unique
    private ItemStack gravityGunFix$stack;

    @Unique
    private NBTTagCompound gravityGunFix$data;

    private MixinEntityBlock(World world) {
        super(world);
    }

    @Shadow(remap = false)
    public abstract void setBlockIdAndMeta(Block block, int meta);

    @Override
    public void ntnhcore$setHolder(EntityLivingBase holder) {
        gravityGunFix$holder = holder instanceof EntityPlayer ? new WeakReference<>((EntityPlayer) holder) : null;
    }

    /**
     * Having a tile entity means it was picked up without the fix, so its contents are only in there. It's then also
     * placed without the fix, no matter if that has been enabled since.
     */
    @Unique
    private boolean gravityGunFix$placesOriginal() {
        return gravityGunFix$stack == null && gravityGunFix$data == null
            && (!Config.moduleGravityGunFixEnabled || tileEntity != null);
    }

    // The original replaces the tile entity with a blank one before breaking the block. Not finding one skips that.
    @Redirect(
        method = "<init>(Lnet/minecraft/world/World;IIIZ)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getTileEntity(III)Lnet/minecraft/tileentity/TileEntity;"))
    private TileEntity gravityGunFix$leaveTileEntity(World world, int x, int y, int z) {
        return Config.moduleGravityGunFixEnabled ? null : world.getTileEntity(x, y, z);
    }

    @Redirect(
        method = "<init>(Lnet/minecraft/world/World;IIIZ)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockToAir(III)Z"))
    private boolean gravityGunFix$breakBlock(World world, int x, int y, int z) {
        if (!Config.moduleGravityGunFixEnabled) {
            return world.setBlockToAir(x, y, z);
        }

        GravityGunFix.BrokenBlock broken = GravityGunFix.breakBlock(world, x, y, z);
        if (broken == null) {
            setDead();
            return false;
        }
        setBlockIdAndMeta(broken.block, broken.meta);
        gravityGunFix$stack = broken.stack;
        gravityGunFix$data = broken.data;
        return true;
    }

    @Redirect(
        method = "onUpdate",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z",
            ordinal = 0))
    private boolean gravityGunFix$placeBlock(World world, int x, int y, int z, Block block, int meta, int flags) {
        EntityPlayer holder = gravityGunFix$holder == null ? null : gravityGunFix$holder.get();
        if (gravityGunFix$data != null) {
            return GravityGunFix.placeWithData(world, x, y, z, block, meta, gravityGunFix$data, this, holder);
        }
        if (gravityGunFix$placesOriginal()) {
            return world.setBlock(x, y, z, block, meta, flags);
        }
        return GravityGunFix.placeBlock(world, x, y, z, block, meta, gravityGunFix$stack, this, holder);
    }

    // Would undo the rotation set by placing the block
    @Redirect(
        method = "onUpdate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlockMetadataWithNotify(IIIII)Z"))
    private boolean gravityGunFix$keepPlacedMeta(World world, int x, int y, int z, int meta, int flags) {
        return !gravityGunFix$placesOriginal() || world.setBlockMetadataWithNotify(x, y, z, meta, flags);
    }

    @Redirect(
        method = "onUpdate",
        at = @At(value = "NEW", target = "(Lnet/minecraft/block/Block;II)Lnet/minecraft/item/ItemStack;"))
    private ItemStack gravityGunFix$dropItem(Block block, int amount, int meta) {
        if (gravityGunFix$data != null) {
            // There's no space here, or the same block is already here
            EntityPlayer holder = gravityGunFix$holder == null ? null : gravityGunFix$holder.get();
            int x = MathHelper.floor_double(posX);
            int y = MathHelper.floor_double(posY);
            int z = MathHelper.floor_double(posZ);
            if (!GravityGunFix.placeNextToWithData(worldObj, x, y, z, block, meta, gravityGunFix$data, this, holder)) {
                // Wait for some space instead of losing its data
                isDead = false;
            }
            return new ItemStack((Item) null);
        }
        if (gravityGunFix$stack != null) {
            return gravityGunFix$stack;
        }
        return Config.moduleGravityGunFixEnabled ? GravityGunFix.getItemStack(worldObj, block, meta)
            : new ItemStack(block, amount, meta);
    }

    @Inject(method = "readEntityFromNBT", at = @At("RETURN"))
    private void gravityGunFix$readStack(NBTTagCompound nbt, CallbackInfo ci) {
        gravityGunFix$stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("ntnhcoreStack"));
        gravityGunFix$data = nbt.hasKey("ntnhcoreData") ? nbt.getCompoundTag("ntnhcoreData") : null;
        // The original always creates a tile entity and writes an empty tag if there was none. Such a blank one would
        // replace the one that's placed with the block, no matter if the fix has been disabled since.
        if (gravityGunFix$stack != null || gravityGunFix$data != null
            || nbt.getCompoundTag("tileEntity")
                .hasNoTags()) {
            tileEntity = null;
        }
    }

    @Inject(method = "writeEntityToNBT", at = @At("RETURN"))
    private void gravityGunFix$writeStack(NBTTagCompound nbt, CallbackInfo ci) {
        if (gravityGunFix$stack != null) {
            nbt.setTag("ntnhcoreStack", gravityGunFix$stack.writeToNBT(new NBTTagCompound()));
        }
        if (gravityGunFix$data != null) {
            nbt.setTag("ntnhcoreData", gravityGunFix$data.copy());
        }
    }
}
