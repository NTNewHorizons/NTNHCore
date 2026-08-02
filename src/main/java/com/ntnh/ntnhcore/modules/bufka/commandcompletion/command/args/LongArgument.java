package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;

import com.bufka.commandcompletion.command.CommandContext;

public class LongArgument implements IArgument<Long> {

    private static final List<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    long min = Long.MIN_VALUE;
    long max = Long.MAX_VALUE;

    protected LongArgument(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public static LongArgument value() {
        return new LongArgument(Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public static LongArgument max(long max) {
        return new LongArgument(Long.MIN_VALUE, max);
    }

    public static LongArgument min(long min) {
        return new LongArgument(min, Long.MAX_VALUE);
    }

    public static LongArgument range(long min, long max) {
        return new LongArgument(min, max);
    }

    @Override
    public Long parse(StringWalker args, CommandContext context) throws CommandException {
        long value = args.readLong();
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
