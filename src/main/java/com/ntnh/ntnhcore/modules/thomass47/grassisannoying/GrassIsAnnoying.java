package com.ntnh.ntnhcore.modules.thomass47.grassisannoying;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.IModule;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class GrassIsAnnoying implements IModule {

    public static final String MODULE_ID = "grassisannoying";
    public static final Logger LOG = LogManager.getLogger(MODULE_ID);

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info(
            "Grass annoyance reduced by "
                + (int) ((Config.grassIsAnnoyingModEnabled ? Math.random() / 2.0 + 0.5 : 0) * 100)
                + "%");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient()) {
            MinecraftForge.EVENT_BUS.register(new AttackThroughGrassHandler());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public void serverStarting(FMLServerStartingEvent event) {}
}
