package com.ntnh.ntnhcore.mixins.late.gravigun;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.thomass47.gravitygunfix.IHeldBlock;

import gravigun.common.GraviGun;
import gravigun.common.core.TickHandlerServer;

@Mixin(value = TickHandlerServer.class, remap = false)
public class MixinTickHandlerServer {

    @Inject(method = "setGrabbed", at = @At("HEAD"), cancellable = true)
    private void gravityGunFix$rememberHolder(EntityLivingBase living, Entity grabbed, CallbackInfo ci) {
        if (grabbed instanceof IHeldBlock) {
            if (grabbed.isDead) {
                // Broke without leaving a block to hold
                ci.cancel();
                return;
            }
            ((IHeldBlock) grabbed).ntnhcore$setHolder(living);
        }
    }

    // Pulling blocks with the supercharged gun
    @Redirect(
        method = "playerTick",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockNormalCubeDefault(IIIZ)Z", remap = true))
    private boolean gravityGunFix$isPullable(World world, int x, int y, int z, boolean def) {
        if (world.isBlockNormalCubeDefault(x, y, z, def)) {
            return true;
        }
        Block block = world.getBlock(x, y, z);
        if (!Config.moduleGravityGunFixEnabled || !Config.gravityGunFixSuperchargedPulling
            || block.getBlockHardness(world, x, y, z) < 0.0F) {
            return false;
        }
        // The original already allows this when grabbing up close
        return block.getRenderType() == 22 && GraviGun.getSettings("grabChestTypeAllowed") == 1;
    }
}
