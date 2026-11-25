package com.ntnh.ntnhcore.mixins.late.gravigun;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.ntnh.ntnhcore.modules.thomass47.gravitygunfix.GravityGunFix;

import gravigun.common.core.EntityHelper;

@Mixin(value = EntityHelper.class, remap = false)
public class MixinEntityHelper {

    @Redirect(
        method = "tryGrab",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;isBlockNormalCubeDefault(IIIZ)Z", remap = true))
    private static boolean gravityGunFix$isGrabbable(World world, int x, int y, int z, boolean def) {
        return world.isBlockNormalCubeDefault(x, y, z, def) || GravityGunFix.isMadeGrabbable(world.getBlock(x, y, z));
    }
}
