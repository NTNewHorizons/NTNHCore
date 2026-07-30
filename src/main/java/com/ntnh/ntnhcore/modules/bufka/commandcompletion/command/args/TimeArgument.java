package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandException;
import net.minecraft.command.NumberInvalidException;

import com.bufka.commandcompletion.command.CommandContext;

public class TimeArgument implements IArgument<Long> {

    private static final List<String> EXAMPLES = Arrays.asList("1d", "1s", "1t", "1", "0");
    private static final Map<Character, Long> TYPES = createMultipliers();

    public static TimeArgument time() {
        return new TimeArgument();
    }

    @Override
    public Long parse(StringWalker args, CommandContext context) throws CommandException {
        if (!args.canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        try {
            String s = args.peek();
            char multi = s.charAt(s.length() - 1);
            if (!isNumber(multi)) {
                double value = Double.parseDouble(s.substring(0, s.length() - 1));
                long multiplier = TYPES.getOrDefault(multi, 1L);
                args.skip();
                return (long) (value * multiplier);
            }
            return (long) args.readDouble();
        } catch (Exception e) {
            throw new NumberInvalidException("commands.completion.error.parse.double", args.poll());
        }
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        return EXAMPLES;
    }

    private static boolean isNumber(char character) {
        return (character >= '0' && character <= '9') || character == '.' || character == '-';
    }

    private static Map<Character, Long> createMultipliers() {
        Map<Character, Long> data = new HashMap<>();
        data.put('d', 24000L);
        data.put('s', 20L);
        data.put('t', 1L);
        return data;
    }
}
