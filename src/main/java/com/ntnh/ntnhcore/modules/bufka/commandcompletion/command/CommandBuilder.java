package com.bufka.commandcompletion.command;

import java.util.Stack;

import com.bufka.commandcompletion.command.args.IArgument;

public class CommandBuilder {

    Stack<CommandNode> queue = new Stack<>();
    CommandNode current;

    public CommandBuilder(String literal) {
        this.current = new LiteralNode(literal);
    }

    public CommandBuilder(String name, IArgument<?> argument) {
        this.current = new ArgumentNode<>(name, argument);
    }

    public CommandBuilder literal(String literal) {
        return push(new LiteralNode(literal));
    }

    public CommandBuilder literal(String literal, CommandExecutor command) {
        return push(new LiteralNode(literal).withCommand(command));
    }

    public CommandBuilder arg(String name, IArgument<?> type) {
        return push(new ArgumentNode<>(name, type));
    }

    public CommandBuilder arg(String name, IArgument<?> type, CommandExecutor command) {
        return push(new ArgumentNode<>(name, type).withCommand(command));
    }

    public CommandBuilder arg(String name, IArgument<?> type, ArgumentNode.SuggestionProvider suggestion) {
        return push(new ArgumentNode<>(name, type).withSuggestion(suggestion));
    }

    public CommandBuilder arg(String name, IArgument<?> type, ArgumentNode.SuggestionProvider suggestion,
        CommandExecutor command) {
        return push(
            new ArgumentNode<>(name, type).withSuggestion(suggestion)
                .withCommand(command));
    }

    private CommandBuilder push(CommandNode command) {
        this.current.addChild(command);
        this.queue.push(this.current);
        this.current = command;
        return this;
    }

    public CommandBuilder popTop() {
        while (!this.queue.isEmpty()) {
            this.current = this.queue.pop();
        }
        return this;
    }

    public CommandBuilder pop() {
        this.current = this.queue.pop();
        return this;
    }

    public CommandBuilder pop(int layers) {
        while (layers > 0) {
            this.current = this.queue.pop();
            layers--;
        }
        return this;
    }

    public CommandNode build() {
        while (!this.queue.isEmpty()) {
            this.current = this.queue.pop();
        }
        return this.current;
    }
}
