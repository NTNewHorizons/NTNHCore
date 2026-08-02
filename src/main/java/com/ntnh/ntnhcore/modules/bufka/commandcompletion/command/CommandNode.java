package com.bufka.commandcompletion.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.bufka.commandcompletion.command.args.StringWalker;

public abstract class CommandNode {

    Map<String, CommandNode> children = new HashMap<>();
    Map<String, LiteralNode> literals = new LinkedHashMap<>();
    Map<String, ArgumentNode<?>> arguments = new LinkedHashMap<>();

    CommandExecutor command;

    public abstract String getName();

    public abstract List<String> getExamples(CommandContext context, int argumentIndex);

    public Collection<CommandNode> getChildren() {
        return this.children.values();
    }

    public CommandExecutor getCommand() {
        return this.command;
    }

    public List<String> getSuggestions(int argumentIndex, CommandContext context) {
        List<String> possible = new ArrayList<>();
        for (CommandNode node : this.children.values()) {
            possible.addAll(node.getExamples(context, argumentIndex));
        }
        return possible;
    }

    public CommandNode withCommand(CommandExecutor command) {
        this.command = command;
        return this;
    }

    public void addChild(CommandNode node) {
        CommandNode child = this.children.get(node.getName());
        if (child != null) {
            if (node.command != null) {
                child.command = node.command;
            }
            node.getChildren()
                .forEach(child::addChild);
            return;
        }
        this.children.put(node.getName(), node);
        if (node instanceof LiteralNode) {
            this.literals.put(node.getName(), (LiteralNode) node);
        }
        if (node instanceof ArgumentNode) {
            this.arguments.put(node.getName(), (ArgumentNode<?>) node);
        }
    }

    public CommandNode findCurrentNode(StringWalker walker, CommandContext context) {
        context.addNode(this);
        if (!walker.canRead()) {
            return this;
        }
        if (this.literals.size() > 0) {
            LiteralNode node = this.literals.get(walker.peek());
            if (node != null) {
                walker.skip();
                return node.findCurrentNode(walker, context);
            }
        }
        if (this.arguments.size() > 0) {
            for (ArgumentNode<?> node : this.arguments.values()) {
                StringWalker subWalker = walker.copy();
                if (node.isValid(subWalker, context)) {
                    walker.apply(subWalker);
                    return node.findCurrentNode(walker, context);
                }
                int diff = subWalker.getIndex() - walker.getIndex();
                if (diff > 0 && diff < node.getArgumentElements()) {
                    walker.advance(diff);
                    context.setArgumentIndex(diff);
                    return this;
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
