package com.ntnh.ntnhcore;

import com.ntnh.ntnhcore.modules.ModuleManager;
import com.ntnh.coloredhearts.ColoredHearts;
import com.thomass47.fastequip.FastEquip;
import com.thomass47.grassisannoying.GrassIsAnnoying;
import com.tterrag.betterplacement.BetterPlacement;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());

        NTNHCore.LOG.info(Config.greeting);
        NTNHCore.LOG.info("I am NTNHCore at version " + Tags.VERSION);

        cpw.mods.fml.common.FMLCommonHandler.instance().bus().register(new Config());

        ModuleManager.register(new FastEquip());
        ModuleManager.register(new GrassIsAnnoying());
        ModuleManager.register(new ColoredHearts());
        ModuleManager.register(new BetterPlacement());
        ModuleManager.preInit(event);
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        ModuleManager.init(event);
    }

    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        ModuleManager.postInit(event);
    }

    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        ModuleManager.serverStarting(event);
    }
}
