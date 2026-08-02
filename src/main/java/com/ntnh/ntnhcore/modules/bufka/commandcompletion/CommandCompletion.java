package com.ntnh.ntnhcore.modules.bufka.commandcompletion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bufka.commandcompletion.command.RootCommand;
import com.ntnh.ntnhcore.modules.IModule;
import com.ntnh.ntnhcore.modules.bufka.commandcompletion.client.GuiHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommandCompletion implements IModule {

    public static final String MODULE_ID = "commandcompletion";
    public static final Logger LOG = LogManager.getLogger(MODULE_ID);

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        LOG.info("Command Completion module initializing");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        if (FMLCommonHandler.instance()
            .getEffectiveSide()
            .isClient()) {
            FMLCommonHandler.instance()
                .bus()
                .register(new GuiHandler());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        LOG.info("Registering Command Completion commands");
        event.registerServerCommand(new RootCommand());
    }
}
