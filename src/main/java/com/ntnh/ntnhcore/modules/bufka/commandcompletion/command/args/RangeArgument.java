package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;

import com.bufka.commandcompletion.command.CommandContext;

public class RangeArgument implements IArgument<Integer> {

    public static final List<String> RADIUSES = Arrays.asList(
        "50",
        "100",
        "200",
        "300",
        "500",
        "1000",
        "4000",
        "10000",
        "25000",
        "800b",
        "1600b",
        "3200b",
        "4800b",
        "8000b",
        "16000b",
        "32000b",
        "64000b");

    boolean isChunk;
    int min;
    int max;

    private RangeArgument(boolean isChunk, int min, int max) {
        this.isChunk = isChunk;
        this.min = min;
        this.max = max;
    }

    public static RangeArgument block() {
        return new RangeArgument(false, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static RangeArgument blockMin(int min) {
        return new RangeArgument(false, min, Integer.MAX_VALUE);
    }

    public static RangeArgument blockMax(int max) {
        return new RangeArgument(false, Integer.MIN_VALUE, max);
    }

    public static RangeArgument blockRange(int min, int max) {
        return new RangeArgument(false, min, max);
    }

    public static RangeArgument chunk() {
        return new RangeArgument(true, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static RangeArgument chunkMin(int min) {
        return new RangeArgument(true, min, Integer.MAX_VALUE);
    }

    public static RangeArgument chunkMax(int max) {
        return new RangeArgument(true, Integer.MIN_VALUE, max);
    }

    public static RangeArgument chunkRange(int min, int max) {
        return new RangeArgument(true, min, max);
    }

    @Override
    public Integer parse(StringWalker args, CommandContext context) throws CommandException {
        int value = args.parseChunkOrBlock(this.isChunk);
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
        return RADIUSES;
    }
}
