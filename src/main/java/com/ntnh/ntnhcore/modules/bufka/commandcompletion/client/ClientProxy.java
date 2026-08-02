package com.bufka.commandcompletion.client;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;

import com.bufka.commandcompletion.CommonProxy;
import com.bufka.commandcompletion.client.gui.chat.ChatScreen;
import com.bufka.commandcompletion.client.gui.chat.IModChat;
import com.bufka.commandcompletion.client.gui.chat.MPChatScreen;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onGuiOpen(GuiOpenEvent event) {
        GuiScreen screen = event.gui;

        if (screen instanceof GuiSleepMP && !(screen instanceof IModChat)) {
            event.gui = new MPChatScreen();
        } else if (screen instanceof GuiChat && !(screen instanceof IModChat)) {
            event.gui = new ChatScreen((GuiChat) screen);
        }
    }
}
