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
    public static boolean moduleCommandCompletionEnabled = true;
    public static boolean moduleColoredHeartsEnabled = true;
    public static boolean moduleBetterPlacementEnabled = true;
    public static boolean moduleGravityGunFixEnabled = true;

    // BetterPlacement module - snappier block placement
    public static boolean betterPlacementCreativeOnly = false;
    public static boolean betterPlacementForceNewLoc = true;

    // GravityGunFix module - make GravityGun break and place blocks properly
    public static int gravityGunFixMaxBreaks = 4;
    public static boolean gravityGunFixSuperchargedPulling = true;
    public static String[] gravityGunFixKeepDataBlocks = { "minecraft:chest", "minecraft:trapped_chest",
        "minecraft:furnace", "minecraft:lit_furnace", "minecraft:dispenser", "minecraft:dropper", "minecraft:hopper",
        "minecraft:jukebox", "minecraft:ender_chest", "minecraft:mob_spawner", "rechests:*", "IronChest:BlockIronChest",
        "etfuturum:*barrel", "etfuturum:smoker", "etfuturum:lit_smoker", "etfuturum:blast_furnace",
        "etfuturum:lit_blast_furnace", "etfuturum:beehive", "etfuturum:bee_nest", "etfuturum:shulker_box",
        "hbm:tile.crate_iron", "hbm:tile.crate_steel", "hbm:tile.crate_desh", "hbm:tile.crate_tungsten",
        "hbm:tile.safe", "StorageDrawers*:fullDrawers*", "StorageDrawers*:halfDrawers*", "StorageDrawers:compDrawers",
        "StorageDrawers:fullCustom*", "StorageDrawers:halfCustom*" };
    public static String[] gravityGunFixGrabbableBlocks = { "minecraft:hopper", "etfuturum:shulker_box",
        "StorageDrawers*:fullDrawers*", "StorageDrawers*:halfDrawers*", "StorageDrawers:compDrawers",
        "StorageDrawers:fullCustom*", "StorageDrawers:halfCustom*" };

    public static void synchronizeConfiguration(File configFile) {
        configuration = new Configuration(configFile);
        syncConfig();
    }

    private static void syncConfig() {
        // General
        greeting = configuration.getString("greeting", Configuration.CATEGORY_GENERAL, greeting, "How shall I greet?");

        // Module enable toggles
        configuration.setCategoryComment("modules", "Master toggles to enable or disable entire modules");
        moduleCommandCompletionEnabled = configuration.getBoolean(
            "enable_commandcompletion",
            "modules",
            moduleCommandCompletionEnabled,
            "Set to false to disable the CommandCompletion module");
        moduleColoredHeartsEnabled = configuration.getBoolean(
            "enable_coloredhearts",
            "modules",
            moduleColoredHeartsEnabled,
            "Set to false to disable the ColoredHearts module");
        moduleBetterPlacementEnabled = configuration.getBoolean(
            "enable_betterplacement",
            "modules",
            moduleBetterPlacementEnabled,
            "Set to false to disable the BetterPlacement module");
        moduleGravityGunFixEnabled = configuration.getBoolean(
            "enable_gravitygunfix",
            "modules",
            moduleGravityGunFixEnabled,
            "Set to false to disable the GravityGunFix module");

        // BetterPlacement module - snappier block placement
        configuration.setCategoryComment(
            "modules.betterplacement",
            "BetterPlacement module - remove right-click delay for snappier block placement");
        betterPlacementCreativeOnly = configuration.getBoolean(
            "creativeOnly",
            "modules.betterplacement",
            betterPlacementCreativeOnly,
            "If true, the modifications will only apply in creative mode");
        betterPlacementForceNewLoc = configuration.getBoolean(
            "forceNewLoc",
            "modules.betterplacement",
            betterPlacementForceNewLoc,
            "When true, a held right click will never place two blocks in a row, the player must move the cursor to a new location");

        // GravityGunFix module - make GravityGun break and place blocks properly
        configuration.setCategoryComment(
            "modules.gravitygunfix",
            "GravityGunFix module - make GravityGun break and place blocks properly");
        gravityGunFixMaxBreaks = configuration.getInt(
            "maxBreaks",
            "modules.gravitygunfix",
            gravityGunFixMaxBreaks,
            1,
            16,
            "How many times to break what's left in the block's place before giving up. Some assembled NTM multiblock parts turn back into their original block when broken, so they need at least 2 to be grabbed.");
        gravityGunFixSuperchargedPulling = configuration.getBoolean(
            "superchargedPulling",
            "modules.gravitygunfix",
            gravityGunFixSuperchargedPulling,
            "Let the supercharged gun pull chests and grabbableBlocks from a distance, like it could for other blocks.");
        gravityGunFixKeepDataBlocks = configuration.getStringList(
            "keepDataBlocks",
            "modules.gravitygunfix",
            gravityGunFixKeepDataBlocks,
            "Blocks that keep all of their data when moved by GravityGun. Supports * as a wildcard.\nDon't add multiblocks or anything that remembers where it or other blocks are, moving those will break them.");
        gravityGunFixGrabbableBlocks = configuration.getStringList(
            "grabbableBlocks",
            "modules.gravitygunfix",
            gravityGunFixGrabbableBlocks,
            "Blocks that GravityGun can grab even though they aren't full blocks. Supports * as a wildcard.\nThey only keep their data if they're in keepDataBlocks too.");

        if (configuration.hasChanged()) {
            configuration.save();
        }
    }

    public static boolean isModuleEnabled(String moduleId) {
        switch (moduleId) {
            case "commandcompletion":
                return moduleCommandCompletionEnabled;
            case "coloredhearts":
                return moduleColoredHeartsEnabled;
            case "betterplacement":
                return moduleBetterPlacementEnabled;
            case "gravitygunfix":
                return moduleGravityGunFixEnabled;
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
