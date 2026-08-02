package com.bufka.commandcompletion.client.gui.chat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;

public abstract class AdvancedTabCompleter extends TabCompleter {

    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");

    protected Minecraft mc = Minecraft.getMinecraft();
    protected ClickBox box = new ClickBox(0, 0, -1, -1);
    protected int offset;
    protected boolean cycle = false;
    protected boolean keepSuggestions = false;
    protected boolean wasFirst = false;

    public AdvancedTabCompleter(GuiTextField textField, boolean hasTargetBlock) {
        super(textField, hasTargetBlock);
    }

    public boolean onKeyPress(int key) {
        if (hasSuggestions()) {
            if (key == 15) {
                complete();
                return true;
            }
            if (key == 200) {
                previous();
                return true;
            }
            if (key == 208) {
                next();
                return true;
            }
        }
        return false;
    }

    public boolean onClick(int mouseX, int mouseY) {
        if (this.box.isInsideBox(mouseX, mouseY)) {
            select(this.offset + (mouseY - this.box.getY()) / 12);
            return true;
        }
        return false;
    }

    public boolean onScroll(int mouseX, int mouseY, int scroll) {
        if (this.box.isInsideBox(mouseX, mouseY)) {
            this.offset += scroll;
            if (this.offset < 0) {
                this.offset = 0;
            } else if (this.offset + 10 > this.completions.size()) {
                this.offset = Math.max(0, this.completions.size() - 10);
            }
            return true;
        }
        return false;
    }

    public void render(int mouseX, int mouseY, FontRenderer font) {
        int limit = Math.min(this.completions.size() - this.offset, 10);
        if (limit > 0) {
            String s = this.textField.getText();
            int x = Math.min(
                (s.length() <= 0) ? 0
                    : font.getStringWidth(
                        s.substring(
                            0,
                            Math.max(
                                0,
                                this.textField.getText()
                                    .lastIndexOf(" "))
                                + 1)),
                this.textField.getWidth()) + this.textField.xPosition - 0;
            int width = 0;
            for (int i = 0, m = this.completions.size(); i < m; i++) {
                width = Math.max(width, font.getStringWidth(this.completions.get(i)));
            }
            int baseY = this.textField.yPosition - 12 * limit - 3;
            int index = (mouseX >= x && mouseX <= x + width) ? ((mouseY - baseY) / 12) : -1;
            Gui.drawRect(x, baseY, x + width + 5, this.textField.yPosition - 3, -805306368);
            for (int j = 0; j < limit; j++) {
                font.drawStringWithShadow(
                    this.completions.get(j + this.offset),
                    x + 2,
                    baseY + j * 12 + 2,
                    (j == index || j + this.offset == this.completionIdx) ? -256 : -5592406);
            }
            this.box.set(x, baseY, width, this.textField.yPosition - 3 - baseY);
        }
    }

    public int getWordIndex(String text) {
        if (text != null && text.length() > 0) {
            int i = 0;
            Matcher matcher = WHITESPACE_PATTERN.matcher(text);
            while (matcher.find()) {
                i = matcher.end();
            }
            return i;
        }
        return 0;
    }

    public boolean hasSuggestions() {
        return this.completions.size() > 0;
    }

    public void complete() {
        if (this.completions.isEmpty()) return;
        if (this.wasFirst) {
            select(this.completionIdx);
            this.wasFirst = false;
            return;
        }
        if (GuiScreen.isShiftKeyDown()) {
            previous();
        } else {
            next();
        }
    }

    public void previous() {
        select((this.completionIdx == 0) ? (this.completions.size() - 1) : (this.completionIdx - 1));
    }

    public void next() {
        select(++this.completionIdx % this.completions.size());
    }

    public void select(int index) {
        if (index < 0 || index >= this.completions.size()) return;
        setSelection(index);
        updateWord(this.completions.get(this.completionIdx));
    }

    protected void setSelection(int index) {
        this.completionIdx = index;
        if (index - 9 >= this.offset) {
            this.offset = index - 9;
        } else if (index < this.offset) {
            this.offset = index;
        }
    }

    public void updateWord(String value) {
        this.textField.deleteFromCursor(0);
        int end = this.textField.getCursorPosition();
        String text = this.textField.getText()
            .substring(0, end);
        int start = getWordIndex(text);
        if (start == 0) start = 1;
        this.cycle = true;
        StringBuilder builder = new StringBuilder(this.textField.getText());
        builder.replace(start, end, value);
        value = builder.toString();
        this.keepSuggestions = true;
        this.textField.setText(EnumChatFormatting.getTextWithoutFormattingCodes(value));
        this.textField.setCursorPosition(start + value.length());
        this.keepSuggestions = false;
    }

    public void requestUpdate() {
        if (this.textField.getText()
            .isEmpty() || this.textField.getCursorPosition() == 0) {
            this.completionIdx = 0;
            this.completions.clear();
            return;
        }
        String prefix = this.textField.getText()
            .substring(0, this.textField.getCursorPosition());
        if (prefix.isEmpty()) prefix = "/";
        int i = this.textField.getNthWordFromPos(-1, this.textField.getCursorPosition());
        String s = this.textField.getText()
            .substring(i)
            .toLowerCase();
        try {
            ClientCommandHandler.instance.autoComplete(prefix, s);
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(prefix));
        } catch (Exception e) {
            // keep existing suggestions on error
        }
        this.cycle = false;
    }

    @Override
    public void setCompletions(String... newCompl) {
        boolean allStart = true;
        for (int i = 0, m = newCompl.length; i < m; i++) {
            if (!newCompl[i].isEmpty() && !newCompl[i].startsWith("/")) {
                allStart = false;
                break;
            }
        }
        if (allStart) {
            for (int i = 0, m = newCompl.length; i < m; i++) {
                if (!newCompl[i].isEmpty()) {
                    newCompl[i] = newCompl[i].substring(1);
                }
            }
        }
        this.wasFirst = (!this.cycle || this.completions.isEmpty());
        this.completions.clear();
        this.completionIdx = 0;
        String currentWord = "";
        if (!this.textField.getText()
            .isEmpty()) {
            int end = this.textField.getCursorPosition();
            currentWord = this.textField.getText()
                .substring(0, end);
            int start = getWordIndex(currentWord);
            if (start == 0) start = 1;
            currentWord = currentWord.substring(start);
        }
        for (int j = 0, k = newCompl.length; j < k; j++) {
            String value = newCompl[j];
            if (!value.isEmpty() && !value.equals(currentWord)) {
                this.completions.add(value);
            }
        }
        this.offset = 0;
    }

    public static class ClickBox {

        int x, y, width, height;

        public ClickBox(int x, int y, int width, int height) {
            set(x, y, width, height);
        }

        public void set(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean isInsideBox(int xPos, int yPos) {
            return xPos >= this.x && xPos <= this.x + this.width && yPos >= this.y && yPos <= this.y + this.height;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }
    }
}
