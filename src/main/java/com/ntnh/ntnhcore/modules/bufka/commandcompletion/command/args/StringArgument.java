package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.List;

import com.bufka.commandcompletion.command.CommandContext;

public class StringArgument implements IArgument<String> {

    public static StringArgument text() {
        return new StringArgument();
    }

    @Override
    public String parse(StringWalker args, CommandContext context) {
        return args.poll();
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        return Arrays.asList("word", "words_with_underline");
    }
}
