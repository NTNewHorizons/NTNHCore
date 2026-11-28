package com.ntnh.ntnhcore.modules.ntnh.harvestthenetherfix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.IModule;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

/**
 * Harvest the Nether registers HarvestCraft's Thaumcraft and MFR compatibility instead of its own. When it loads before
 * HarvestCraft, those registries are built from crop blocks that don't exist yet, and Thaumcraft crashes on the empty
 * stacks in postInit. HarvestCraft already registers its own, so Harvest the Nether is made to register its own
 * instead.
 *
 * Both mods are called by name, so neither has to be on the compile classpath.
 */
public class HarvestTheNetherFix implements IModule {

    public static final String MODULE_ID = "harvestthenetherfix";
    public static final Logger LOG = LogManager.getLogger(MODULE_ID);

    private static final String OWN_PACKAGE = "com.pam.harvestthenether.";
    private static final String HARVESTCRAFT_PACKAGE = "com.pam.harvestcraft.";

    public static final String THAUMCRAFT_REGISTRY = "PamThaumcraftAspectsList";
    public static final String MFR_REGISTRY = "PamMFRCompatibility";

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    /** Called in place of Harvest the Nether's call to HarvestCraft's version of the registry. */
    public static void registerCompat(String registry) {
        String owner = Config.moduleHarvestTheNetherFixEnabled ? OWN_PACKAGE : HARVESTCRAFT_PACKAGE;
        callGetRegistry(owner + registry);
    }

    private static void callGetRegistry(String className) {
        Method getRegistry;
        try {
            getRegistry = Class.forName(className)
                .getMethod("getRegistry");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOG.error("Couldn't find " + className + ".getRegistry(), skipping it", e);
            return;
        }
        try {
            getRegistry.invoke(null);
        } catch (IllegalAccessException e) {
            LOG.error("Couldn't call " + className + ".getRegistry(), skipping it", e);
        } catch (InvocationTargetException e) {
            // Don't hide errors from the registry itself, it would have crashed the same way without this
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException(cause);
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {}

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public void serverStarting(FMLServerStartingEvent event) {}
}
