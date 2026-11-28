package com.ntnh.ntnhcore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;

/** Loads mixins that target other mods, only when the mod they target is present. */
@LateMixin
public class LateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.ntnhcore.late.json";
    }

    @Override
    public @Nonnull List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if (loadedMods.contains("GraviGun")) {
            mixins.add("gravigun.MixinEntityBlock");
            mixins.add("gravigun.MixinEntityHelper");
            mixins.add("gravigun.MixinTickHandlerServer");
            if (FMLLaunchHandler.side()
                .isClient()) {
                mixins.add("gravigun.MixinRenderBlock");
            }
        }
        if (loadedMods.contains("harvestthenether")) {
            mixins.add("harvestthenether.MixinHarvestTheNether");
        }
        return mixins;
    }
}
