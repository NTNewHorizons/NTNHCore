package com.bufka.commandcompletion.demo;

import net.minecraft.util.ChatComponentTranslation;

import com.bufka.commandcompletion.command.CommandBuilder;
import com.bufka.commandcompletion.command.CommandContext;
import com.bufka.commandcompletion.command.CommandNode;
import com.bufka.commandcompletion.command.RootCommand;
import com.bufka.commandcompletion.command.args.BooleanArgument;
import com.bufka.commandcompletion.command.args.EnumArgument;
import com.bufka.commandcompletion.command.args.IntegerArgument;
import com.bufka.commandcompletion.command.args.StringArgument;

public class DemoCommand {

    public enum ConfigOption {
        GREETING,
        FORMAT,
        LANGUAGE
    }

    public static void build(RootCommand root) {
        CommandBuilder builder = new CommandBuilder("temp");

        builder.literal("help", DemoCommand::help)
            .pop();

        builder.literal("echo");
        builder.arg("message", StringArgument.text(), DemoCommand::echo)
            .popTop();

        builder.literal("add");
        builder.arg("a", IntegerArgument.value());
        builder.arg("b", IntegerArgument.value(), DemoCommand::add)
            .popTop();

        builder.literal("greet");
        builder.arg("name", StringArgument.text());
        builder.arg("formal", BooleanArgument.bool(), DemoCommand::greet)
            .popTop();

        builder.literal("config");
        builder.arg("option", EnumArgument.value(ConfigOption.class));
        builder.arg("value", StringArgument.text(), DemoCommand::config)
            .popTop();

        CommandNode demoNode = builder.build();
        root.addChild(demoNode);
    }

    private static void help(CommandContext context) {
        context.sendSuccess(new ChatComponentTranslation("commands.completion.demo.help.header"));
        context.sendSuccess(
            new ChatComponentTranslation("commands.completion.demo.help.entry", "cmdcomplete help", "Shows this help"));
        context.sendSuccess(
            new ChatComponentTranslation(
                "commands.completion.demo.help.entry",
                "cmdcomplete echo <message>",
                "Echoes back a message"));
        context.sendSuccess(
            new ChatComponentTranslation(
                "commands.completion.demo.help.entry",
                "cmdcomplete add <a> <b>",
                "Adds two numbers"));
        context.sendSuccess(
            new ChatComponentTranslation(
                "commands.completion.demo.help.entry",
                "cmdcomplete greet <name> [formal]",
                "Greets a person"));
        context.sendSuccess(
            new ChatComponentTranslation(
                "commands.completion.demo.help.entry",
                "cmdcomplete config <option> <value>",
                "Sets a config option"));
    }

    private static void echo(CommandContext context) {
        String message = context.getArgument("message", String.class);
        context.sendSuccess(new ChatComponentTranslation("commands.completion.demo.echo", message));
    }

    private static void add(CommandContext context) {
        int a = context.getArgument("a", Integer.class);
        int b = context.getArgument("b", Integer.class);
        context.sendSuccess(new ChatComponentTranslation("commands.completion.demo.add_result", a, b, a + b));
    }

    private static void greet(CommandContext context) {
        String name = context.getArgument("name", String.class);
        boolean formal = context.getArgumentOrDefault("formal", Boolean.class, false);
        if (formal) {
            context.sendSuccess(new ChatComponentTranslation("commands.completion.demo.greet_formal", name));
        } else {
            context.sendSuccess(new ChatComponentTranslation("commands.completion.demo.greet_hello", name));
        }
    }

    private static void config(CommandContext context) {
        ConfigOption option = context.getArgument("option", ConfigOption.class);
        String value = context.getArgument("value", String.class);
        context.sendSuccess(new ChatComponentTranslation("commands.completion.demo.config_set", option.name(), value));
    }
}
