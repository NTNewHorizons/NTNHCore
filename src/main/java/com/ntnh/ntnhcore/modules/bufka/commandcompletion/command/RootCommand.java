package com.bufka.commandcompletion.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentTranslation;

import com.bufka.commandcompletion.command.args.StringWalker;
import com.bufka.commandcompletion.demo.DemoCommand;

public class RootCommand extends CommandBase {

    CommandNode root = new LiteralNode("cmdcomplete");

    public RootCommand() {
        DemoCommand.build(this);
    }

    public void addChild(CommandNode child) {
        this.root.addChild(child);
    }

    @Override
    public String getCommandName() {
        return "cmdcomplete";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/cmdcomplete - Command Completion demo command";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        CommandContext context = new CommandContext(sender);
        CommandNode node = this.root.findCurrentNode(new StringWalker(args), context);
        if (context.getException() != null) {
            context.sendFailure(context.getException());
            return;
        }
        if (node == null) {
            context.sendFailure(
                new ChatComponentTranslation(
                    "commands.completion.error.command.notfound",
                    "cmdcomplete " + String.join(" ", args)));
            return;
        }
        if (node.getCommand() == null) {
            context.sendFailure(new ChatComponentTranslation("commands.completion.error.command.missingargs"));
            return;
        }
        node.getCommand()
            .execute(context);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        CommandContext context = new CommandContext(sender);
        StringWalker walker = new StringWalker(args);
        walker.capTop();
        CommandNode node = this.root.findCurrentNode(walker, context);
        walker.resetCap();
        return getBestMatch(
            walker.canRead() ? walker.peek() : null,
            (node == null ? this.root : node).getSuggestions(context.getArgumentIndex(), context));
    }

    public static List<String> getBestMatch(String argument, Collection<String> args) {
        List<String> results = new ArrayList<>();
        for (String arg : args) {
            if (argument == null || arg.regionMatches(true, 0, argument, 0, argument.length())) {
                results.add(arg);
            }
        }
        return results;
    }
}
