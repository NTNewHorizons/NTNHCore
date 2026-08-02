package com.ntnh.ntnhcore.modules.thomass47.whitelistdementianomore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.IModule;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class WhitelistDementiaNoMore implements IModule {

    public static final String MODULE_ID = "whitelistdementianomore";
    public static final Logger LOG = LogManager.getLogger(MODULE_ID);

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (Config.moduleWhitelistDementiaNoMoreEnabled) {
            LOG.info("Whitelist no longer has dementia");
        } else {
            LOG.info("Whitelist still has dementia");
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public void serverStarting(FMLServerStartingEvent event) {}
}
