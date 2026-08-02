package com.bufka.commandcompletion.client.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class MultilineChatScreen extends GuiNewChat {

    public MultilineChatScreen(Minecraft mc) {
        super(mc);
    }

    @Override
    public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
        if (chatLineId != 0) {
            super.printChatMessageWithOptionalDeletion(chatComponent, chatLineId);
            return;
        }
        splitAndSend(chatComponent, chatLineId);
    }

    private void splitAndSend(IChatComponent component, int chatLineId) {
        String text = component.getUnformattedTextForChat();
        if (text.length() <= 200) {
            super.printChatMessageWithOptionalDeletion(component, chatLineId);
            return;
        }
        String remaining = text;
        while (remaining.length() > 200) {
            String part = remaining.substring(0, 200);
            super.printChatMessageWithOptionalDeletion(new ChatComponentText(part), chatLineId);
            remaining = remaining.substring(200);
        }
        if (!remaining.isEmpty()) {
            super.printChatMessageWithOptionalDeletion(new ChatComponentText(remaining), chatLineId);
        }
    }
}
