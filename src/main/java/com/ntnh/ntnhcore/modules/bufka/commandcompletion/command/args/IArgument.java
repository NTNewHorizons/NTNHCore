package com.bufka.commandcompletion.command.args;

import java.util.List;

import net.minecraft.command.CommandException;

import com.bufka.commandcompletion.command.CommandContext;

public interface IArgument<T> {

    T parse(StringWalker walker, CommandContext context) throws CommandException;

    List<String> getExamples(CommandContext context, int argumentIndex);

    default int getArgumentElements() {
        return 1;
    }
}
