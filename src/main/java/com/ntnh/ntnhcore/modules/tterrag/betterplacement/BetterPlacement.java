package com.tterrag.betterplacement;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import com.ntnh.ntnhcore.Config;
import com.ntnh.ntnhcore.modules.IModule;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraftforge.common.util.ForgeDirection;

public class BetterPlacement implements IModule {

    public static final String MODULE_ID = "betterplacement";

    @Override
    public String getModuleId() {
        return MODULE_ID;
    }

    private static BlockCoord lastTargetPos;
    private static ForgeDirection lastTargetSide;

    private static final Field _rightClickDelayTimer = ReflectionHelper.findField(Minecraft.class, "field_71467_ac", "rightClickDelayTimer");

    private static final MethodHandle getDelayTimer, setDelayTimer;
    static {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            getDelayTimer = lookup.unreflectGetter(_rightClickDelayTimer);
            setDelayTimer = lookup.unreflectSetter(_rightClickDelayTimer);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(this);
    }

    @Override
    public void init(FMLInitializationEvent event) {}

    @Override
    public void postInit(FMLPostInitializationEvent event) {}

    @Override
    public void serverStarting(FMLServerStartingEvent event) {}

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) throws Throwable {
        if (event.phase != Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();

        if (!Config.betterPlacementCreativeOnly || (mc.thePlayer != null && mc.thePlayer.capabilities.isCreativeMode)) {
            int timer = (int) getDelayTimer.invoke(Minecraft.getMinecraft());
            MovingObjectPosition hover = Minecraft.getMinecraft().objectMouseOver;
            if (hover != null && hover.typeOfHit == MovingObjectType.BLOCK) {
                BlockCoord pos = new BlockCoord(hover);
                if (timer > 0 && !pos.equals(lastTargetPos) && (lastTargetPos == null || !pos.equals(lastTargetPos.getLocation(lastTargetSide)))) {
                    setDelayTimer.invoke(Minecraft.getMinecraft(), 0);
                } else if (Config.betterPlacementForceNewLoc && timer == 0 && pos.equals(lastTargetPos) && hover.sideHit == lastTargetSide.ordinal()) {
                    setDelayTimer.invoke(Minecraft.getMinecraft(), 4);
                }
                lastTargetPos = pos;
                lastTargetSide = ForgeDirection.getOrientation(hover.sideHit);
            }
        }
    }
}
