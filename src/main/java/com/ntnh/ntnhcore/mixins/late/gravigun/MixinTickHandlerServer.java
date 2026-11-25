package com.ntnh.ntnhcore.mixins.late.gravigun;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.ntnh.ntnhcore.modules.thomass47.gravitygunfix.IHeldBlock;

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
}
