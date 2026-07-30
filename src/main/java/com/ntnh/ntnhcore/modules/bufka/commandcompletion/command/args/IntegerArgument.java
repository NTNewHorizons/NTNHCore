package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;

import com.bufka.commandcompletion.command.CommandContext;

public class IntegerArgument implements IArgument<Integer> {

    private static final List<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    int min = Integer.MIN_VALUE;
    int max = Integer.MAX_VALUE;

    protected IntegerArgument(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static IntegerArgument value() {
        return new IntegerArgument(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static IntegerArgument max(int max) {
        return new IntegerArgument(Integer.MIN_VALUE, max);
    }

    public static IntegerArgument min(int min) {
        return new IntegerArgument(min, Integer.MAX_VALUE);
    }

    public static IntegerArgument range(int min, int max) {
        return new IntegerArgument(min, max);
    }

    @Override
    public Integer parse(StringWalker args, CommandContext context) throws CommandException {
        int value = args.readInt();
        if (value < this.min) {
            throw new CommandException("commands.completion.error.parse.to_small", value, this.min);
        }
        if (value > this.max) {
            throw new CommandException("commands.completion.error.parse.to_big", value, this.max);
        }
        return value;
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        return EXAMPLES;
    }
}
