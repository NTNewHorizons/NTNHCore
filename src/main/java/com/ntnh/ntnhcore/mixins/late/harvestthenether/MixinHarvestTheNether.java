package com.ntnh.ntnhcore.mixins.late.harvestthenether;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.ntnh.ntnhcore.modules.ntnh.harvestthenetherfix.HarvestTheNetherFix;
import com.pam.harvestthenether.harvestthenether;

/** Makes Harvest the Nether register its own compatibility instead of HarvestCraft's. */
@Mixin(value = harvestthenether.class, remap = false)
public class MixinHarvestTheNether {

    @Redirect(
        method = "onPreInit",
        at = @At(value = "INVOKE", target = "Lcom/pam/harvestcraft/PamThaumcraftAspectsList;getRegistry()V"))
    private void harvestTheNetherFix$registerOwnAspects() {
        HarvestTheNetherFix.registerCompat(HarvestTheNetherFix.THAUMCRAFT_REGISTRY);
    }

    @Redirect(
        method = "onPreInit",
        at = @At(value = "INVOKE", target = "Lcom/pam/harvestcraft/PamMFRCompatibility;getRegistry()V"))
    private void harvestTheNetherFix$registerOwnMFRCompat() {
        HarvestTheNetherFix.registerCompat(HarvestTheNetherFix.MFR_REGISTRY);
    }
}
