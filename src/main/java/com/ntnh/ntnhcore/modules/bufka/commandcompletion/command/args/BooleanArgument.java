package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;

import com.bufka.commandcompletion.command.CommandContext;

public class BooleanArgument implements IArgument<Boolean> {

    private static final List<String> EXAMPLES = Arrays.asList("true", "false");

    public static BooleanArgument bool() {
        return new BooleanArgument();
    }

    @Override
    public Boolean parse(StringWalker args, CommandContext context) throws CommandException {
        return args.readBoolean();
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        return EXAMPLES;
    }
}
