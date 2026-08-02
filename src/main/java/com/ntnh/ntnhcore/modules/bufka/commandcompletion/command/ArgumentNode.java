package com.bufka.commandcompletion.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.command.CommandException;
import net.minecraft.util.ChatComponentTranslation;

import com.bufka.commandcompletion.command.args.IArgument;
import com.bufka.commandcompletion.command.args.StringWalker;

public class ArgumentNode<T> extends CommandNode {

    String name;
    IArgument<T> argument;
    SuggestionProvider customSuggestions;

    public ArgumentNode(String name, IArgument<T> argument) {
        this.name = name;
        this.argument = argument;
    }

    public ArgumentNode<T> withSuggestion(SuggestionProvider customSuggestions) {
        this.customSuggestions = customSuggestions;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @SuppressWarnings("unchecked")
    public boolean isValid(StringWalker walker, CommandContext context) {
        try {
            T arg = this.argument.parse(walker, context);
            if (arg != null) {
                return context.addArgument(this.name, arg);
            }
        } catch (CommandException e) {
            context
                .setException(this.name, new ChatComponentTranslation(e.getMessage(), (Object[]) e.getErrorOjbects()));
        }
        return false;
    }

    public int getArgumentElements() {
        return this.argument.getArgumentElements();
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        if (this.customSuggestions != null) {
            List<String> result = new ArrayList<>();
            this.customSuggestions.collectSuggestions(context, argumentIndex, result::add);
            return result;
        }
        return this.argument.getExamples(context, argumentIndex);
    }

    public interface SuggestionProvider {

        void collectSuggestions(CommandContext context, int argumentIndex, Consumer<String> output);
    }
}
