package com.bufka.commandcompletion.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandContext {

    ICommandSender sender;
    List<CommandNode> nodes = new ArrayList<>();
    Map<String, Object> args = new HashMap<>();
    String argumentId;
    IChatComponent exception;
    int argumentIndex;

    public CommandContext(ICommandSender sender) {
        this.sender = sender;
    }

    public void setException(String argumentId, IChatComponent exception) {
        this.argumentId = argumentId;
        this.exception = exception;
    }

    public IChatComponent getException() {
        return this.exception;
    }

    public void setArgumentIndex(int index) {
        this.argumentIndex = index;
    }

    public int getArgumentIndex() {
        return this.argumentIndex;
    }

    public void addNode(CommandNode node) {
        this.nodes.add(node);
        this.exception = null;
    }

    public boolean addArgument(String name, Object arg) {
        this.args.put(name, arg);
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgument(String name, Class<T> clz) {
        Object arg = this.args.get(name);
        if (arg == null) {
            throw new IllegalStateException("Argument [" + name + "] is missing");
        }
        if (clz.isInstance(arg)) {
            return (T) arg;
        }
        throw new IllegalStateException(
            "Argument [" + name
                + "] is of type ["
                + arg.getClass()
                    .getSimpleName()
                + "], but was expected to be ["
                + clz
                + "]");
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgumentOrDefault(String name, Class<T> clz, T defaultValue) {
        Object arg = this.args.get(name);
        return (arg != null && clz.isInstance(arg)) ? (T) arg : defaultValue;
    }

    public <T> boolean hasArgument(String name, Class<T> clz) {
        Object arg = this.args.get(name);
        return (arg != null && clz.isInstance(arg));
    }

    public ICommandSender getSender() {
        return this.sender;
    }

    public IChatComponent getSenderName() {
        return new ChatComponentText(this.sender.getCommandSenderName());
    }

    public void printException() {
        sendFailure(
            new ChatComponentTranslation(
                "commands.completion.error.command.argument_failed",
                this.argumentId,
                this.exception));
    }

    public void sendSuccess(IChatComponent text) {
        this.sender.addChatMessage(text);
    }

    public void sendFailure(IChatComponent text) {
        IChatComponent colored = text.createCopy();
        colored.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));
        this.sender.addChatMessage(colored);
    }
}
