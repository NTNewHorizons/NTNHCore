package com.bufka.commandcompletion.command;

import java.util.Collections;
import java.util.List;

public class LiteralNode extends CommandNode {

    String name;

    public LiteralNode(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        return Collections.singletonList(this.name);
    }
}
