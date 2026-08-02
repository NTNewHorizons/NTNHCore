package com.bufka.commandcompletion;

import com.bufka.commandcompletion.command.RootCommand;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

public class CommonProxy {

    public void preInit(FMLPreInitializationEvent event) {
        CommandCompletionMod.LOG.info("Command Completion initializing");
    }

    public void init(FMLInitializationEvent event) {}

    public void postInit(FMLPostInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {
        CommandCompletionMod.LOG.info("Registering Command Completion commands");
        event.registerServerCommand(new RootCommand());
    }
}
