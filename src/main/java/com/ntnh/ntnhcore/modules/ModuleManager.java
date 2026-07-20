package com.ntnh.ntnhcore.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ntnh.ntnhcore.Config;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ModuleManager {

    private static final List<IModule> modules = new ArrayList<>();
    private static final Map<String, IModule> moduleMap = new HashMap<>();

    public static void register(IModule module) {
        modules.add(module);
        moduleMap.put(module.getModuleId(), module);
    }

    public static void preInit(FMLPreInitializationEvent event) {
        for (IModule module : modules) {
            if (Config.isModuleEnabled(module.getModuleId())) {
                module.preInit(event);
            }
        }
    }

    public static void init(FMLInitializationEvent event) {
        for (IModule module : modules) {
            if (Config.isModuleEnabled(module.getModuleId())) {
                module.init(event);
            }
        }
    }

    public static void postInit(FMLPostInitializationEvent event) {
        for (IModule module : modules) {
            if (Config.isModuleEnabled(module.getModuleId())) {
                module.postInit(event);
            }
        }
    }
}
