package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;

import com.bufka.commandcompletion.command.CommandContext;

public class DoubleArgument implements IArgument<Double> {

    private static final List<String> EXAMPLES = Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");

    double min = -Double.MAX_VALUE;
    double max = Double.MAX_VALUE;

    protected DoubleArgument(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static DoubleArgument value() {
        return new DoubleArgument(-Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public static DoubleArgument max(double max) {
        return new DoubleArgument(-Double.MAX_VALUE, max);
    }

    public static DoubleArgument min(double min) {
        return new DoubleArgument(min, Double.MAX_VALUE);
    }

    public static DoubleArgument range(double min, double max) {
        return new DoubleArgument(min, max);
    }

    @Override
    public Double parse(StringWalker args, CommandContext context) throws CommandException {
        double value = args.readDouble();
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
