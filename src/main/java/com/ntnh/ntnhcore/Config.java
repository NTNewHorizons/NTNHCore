package com.ntnh.ntnhcore;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

    private static Configuration configuration;

    // General
    public static String greeting = "Hello World";

    // Module enable toggles (category: modules)
    public static boolean moduleFastEquipEnabled = true;
    public static boolean moduleGrassIsAnnoyingEnabled = true;

    // FastEquip module - right-click to equip armor from hotbar/inventory
    public static boolean fastEquipHotbarEnabled = true;
    public static boolean fastEquipInventoryEnabled = true;

    // GrassIsAnnoying module - attack entities through grass and hide block outline
    public static boolean grassIsAnnoyingModEnabled = true;
    public static boolean grassIsAnnoyingHideBlockOutline = true;

    public static void synchronizeConfiguration(File configFile) {
        configuration = new Configuration(configFile);
        syncConfig();
    }

    private static void syncConfig() {
        // General
        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");

        // Module enable toggles
        configuration.setCategoryComment("modules", "Master toggles to enable or disable entire modules");
        moduleFastEquipEnabled = configuration.getBoolean(
            "enable_fastequip", "modules", moduleFastEquipEnabled,
            "Set to false to disable the FastEquip module");
        moduleGrassIsAnnoyingEnabled = configuration.getBoolean(
            "enable_grassisannoying", "modules", moduleGrassIsAnnoyingEnabled,
            "Set to false to disable the GrassIsAnnoying module");

        // FastEquip module - right-click to equip armor from hotbar/inventory
        configuration.setCategoryComment("modules.fastequip", "FastEquip module - right-click to equip armor from hotbar/inventory");
        fastEquipHotbarEnabled = configuration.getBoolean(
            "isHotbarFastEquipEnabled", "modules.fastequip", fastEquipHotbarEnabled,
            "Set to false to disable fast armor equip when right clicking in hotbar");
        fastEquipInventoryEnabled = configuration.getBoolean(
            "isInventoryFastEquipEnabled", "modules.fastequip", fastEquipInventoryEnabled,
            "Set to false to disable fast armor equip when right clicking in inventory");

        // GrassIsAnnoying module - attack entities through grass and hide block outline
        configuration.setCategoryComment("modules.grassisannoying", "GrassIsAnnoying module - attack entities through grass and hide block outline");
        grassIsAnnoyingModEnabled = configuration.getBoolean(
            "isModEnabled", "modules.grassisannoying", grassIsAnnoyingModEnabled,
            "Set to false to disable attacking through grass");
        grassIsAnnoyingHideBlockOutline = configuration.getBoolean(
            "hideBlockOutline", "modules.grassisannoying", grassIsAnnoyingHideBlockOutline,
            "Set to false to keep the block outline when aiming at an entity through grass");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static boolean isModuleEnabled(String moduleId) {
        switch (moduleId) {
            case "fastequip":
                return moduleFastEquipEnabled;
            case "grassisannoying":
                return moduleGrassIsAnnoyingEnabled;
            default:
                return false;
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(NTNHCore.MODID)) {
            syncConfig();
        }
    }
}
