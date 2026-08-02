package com.bufka.commandcompletion.client.gui.chat;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

public abstract class TabCompleter {

    protected final GuiTextField textField;
    protected final boolean hasTargetBlock;
    protected boolean didComplete;
    protected boolean requestedCompletions;
    protected int completionIdx;
    protected List<String> completions = Lists.newArrayList();

    public TabCompleter(GuiTextField textFieldIn, boolean hasTargetBlockIn) {
        this.textField = textFieldIn;
        this.hasTargetBlock = hasTargetBlockIn;
    }

    public void complete() {
        if (this.didComplete) {
            this.textField.deleteFromCursor(0);
            this.textField.deleteFromCursor(
                this.textField.getNthWordFromPos(-1, this.textField.getCursorPosition())
                    - this.textField.getCursorPosition());
            if (this.completionIdx >= this.completions.size()) {
                this.completionIdx = 0;
            }
        } else {
            int i = this.textField.getNthWordFromPos(-1, this.textField.getCursorPosition());
            this.completions.clear();
            this.completionIdx = 0;
            String s = this.textField.getText()
                .substring(i)
                .toLowerCase();
            String s1 = this.textField.getText()
                .substring(0, this.textField.getCursorPosition());
            requestCompletions(s1, s);
            if (this.completions.isEmpty()) return;
            this.didComplete = true;
            this.textField.deleteFromCursor(i - this.textField.getCursorPosition());
        }
        this.textField
            .setText(EnumChatFormatting.getTextWithoutFormattingCodes(this.completions.get(this.completionIdx++)));
    }

    private void requestCompletions(String prefix, String full) {
        if (prefix.length() >= 1) {
            ClientCommandHandler.instance.autoComplete(prefix, full);
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(prefix));
            this.requestedCompletions = true;
        }
    }

    public void setCompletions(String... newCompl) {
        if (this.requestedCompletions) {
            this.didComplete = false;
            this.completions.clear();
            String[] complete = ClientCommandHandler.instance.latestAutoComplete;
            if (complete != null) {
                newCompl = ObjectArrays.concat(complete, newCompl, String.class);
            }
            for (String s : newCompl) {
                if (!s.isEmpty()) {
                    this.completions.add(s);
                }
            }
            String s1 = this.textField.getText()
                .substring(this.textField.getNthWordFromPos(-1, this.textField.getCursorPosition()));
            String s2 = org.apache.commons.lang3.StringUtils.getCommonPrefix(newCompl);
            s2 = EnumChatFormatting.getTextWithoutFormattingCodes(s2);
            if (!s2.isEmpty() && !s1.equalsIgnoreCase(s2)) {
                this.textField.deleteFromCursor(0);
                this.textField.deleteFromCursor(
                    this.textField.getNthWordFromPos(-1, this.textField.getCursorPosition())
                        - this.textField.getCursorPosition());
                this.textField.setText(s2);
            } else if (!this.completions.isEmpty()) {
                this.didComplete = true;
                complete();
            }
        }
    }

    public void resetDidComplete() {
        this.didComplete = false;
    }

    public void resetRequested() {
        this.requestedCompletions = false;
    }
}
