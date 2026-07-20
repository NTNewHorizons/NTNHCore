package com.thomass47.fastequip;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.IModule;

import net.minecraftforge.common.MinecraftForge;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class FastEquip implements IModule {

    public static final String MODULE_ID = "fastequip";
    public static final Logger LOG = LogManager.getLogger(MODULE_ID);
    public static SimpleNetworkWrapper network;

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("fastequip");
        network.registerMessage(
            com.thomass47.fastequip.network.PacketFastEquip.Handler.class,
            com.thomass47.fastequip.network.PacketFastEquip.class,
            0, Side.SERVER);

        int features = (Config.fastEquipHotbarEnabled ? 1 : 0) + (Config.fastEquipInventoryEnabled ? 1 : 0);
        LOG.info(
            "Time spent equipping armor reduced by "
                + (features == 0 ? 0 : (int) (Math.random() * 50) + (features - 1) * 50)
                + "%");
    }

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CommonFastEquipHandler());
        if (cpw.mods.fml.common.FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            MinecraftForge.EVENT_BUS.register(new ClientFastEquipHandler());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {}
}
