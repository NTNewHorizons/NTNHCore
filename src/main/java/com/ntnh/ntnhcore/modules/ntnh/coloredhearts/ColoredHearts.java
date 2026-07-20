package com.ntnh.coloredhearts;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ntnh.ntnhcore.Tags;
import com.ntnh.ntnhcore.modules.IModule;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class ColoredHearts implements IModule {

    public static final String MODULE_ID = "coloredhearts";
    public static final Logger LOG = LogManager.getLogger(MODULE_ID);

    private HealthBarRenderer healthBarRenderer;

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("I am Colored Hearts Mod at version " + Tags.VERSION);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            healthBarRenderer = new HealthBarRenderer();
            MinecraftForge.EVENT_BUS.register(healthBarRenderer);
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new SetMaxHealthCommand());
    }
}
