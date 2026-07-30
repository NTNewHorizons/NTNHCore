package com.bufka.commandcompletion.client.gui.chat;

import java.util.List;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.relauncher.ReflectionHelper;

public class ChatScreen extends GuiChat implements IModChat {

    Completor completer;
    private int sentHistoryCursor = -1;
    private String savedText = "";

    public ChatScreen(GuiChat chat) {
        super(getInitialText(chat));
    }

    private static String getInitialText(GuiChat chat) {
        try {
            return ReflectionHelper.getPrivateValue(GuiChat.class, chat,
                new String[] { "defaultInputFieldText", "field_146409_v", "field_146410_g" });
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.completer = new Completor(this.inputField);
        if (!this.inputField.getText().isEmpty()) {
            this.completer.requestUpdate();
        }
    }

    @Override
    public void func_146406_a(String[] newCompletions) {
        this.completer.setCompletions(newCompletions);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.completer.render(mouseX, mouseY, this.fontRendererObj);
    }

    @Override
    public void handleMouseInput() {
        int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int scroll = Mouse.getDWheel() / 120;
        if (scroll != 0 && !this.completer.onScroll(mouseX, mouseY, -scroll)) {
            if (!isShiftKeyDown()) scroll *= 7;
            this.mc.ingameGUI.getChatGUI()
                .scroll(scroll);
        }
        super.handleMouseInput();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.completer.onClick(mouseX, mouseY)) return;
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        boolean update = false;
        if (this.completer.onKeyPress(keyCode)) return;

        update = true;
        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                navigateSentHistory(-1);
            } else if (keyCode == 208) {
                navigateSentHistory(1);
            } else if (keyCode == 201) {
                this.mc.ingameGUI.getChatGUI()
                    .scroll(
                        this.mc.ingameGUI.getChatGUI()
                            .func_146232_i() - 1);
            } else if (keyCode == 209) {
                this.mc.ingameGUI.getChatGUI()
                    .scroll(
                        -this.mc.ingameGUI.getChatGUI()
                            .func_146232_i() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
            if (update) this.completer.requestUpdate();
        } else {
            String s = this.inputField.getText()
                .trim();
            if (!s.isEmpty()) func_146403_a(s);
            this.mc.displayGuiScreen(null);
        }
    }

    private void navigateSentHistory(int direction) {
        List<String> sentMessages = this.mc.ingameGUI.getChatGUI().getSentMessages();
        int len = sentMessages.size();
        if (len == 0) return;

        if (sentHistoryCursor == -1) {
            savedText = this.inputField.getText();
            sentHistoryCursor = direction < 0 ? len - 1 : -1;
        } else {
            int newCursor = sentHistoryCursor + direction;
            if (newCursor < 0 || newCursor >= len) {
                sentHistoryCursor = -1;
                this.inputField.setText(savedText);
                return;
            }
            sentHistoryCursor = newCursor;
        }

        if (sentHistoryCursor >= 0) {
            this.inputField.setText(sentMessages.get(sentHistoryCursor));
        }
    }

    public static class Completor extends AdvancedTabCompleter {

        public Completor(GuiTextField textFieldIn) {
            super(textFieldIn, false);
        }
    }
}
