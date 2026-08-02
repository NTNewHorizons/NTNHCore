package com.bufka.commandcompletion.command.args;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandException;

import com.bufka.commandcompletion.command.CommandContext;
import com.bufka.commandcompletion.misc.FilePos;

public class StructureArgument implements IArgument<FilePos> {

    public static final String DIMENSION_ARG = "dimension";
    public static final String TYPE_ARG = "type";
    public static final String POSITION_ARG = "position";
    private static final String TEMP_PREV_ARG = "structure_x";

    String argName;

    public static StructureArgument structures(String argName) {
        return new StructureArgument(argName);
    }

    public StructureArgument(String argName) {
        this.argName = argName;
    }

    @Override
    public FilePos parse(StringWalker args, CommandContext context) throws CommandException {
        int x = args.readInt();
        try {
            int z = args.readInt();
            return new FilePos(x, z);
        } catch (CommandException e) {
            args.undo();
            context.addArgument("structure_x", x);
            throw e;
        }
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        if (argumentIndex == 0) {
            return Collections.emptyList();
        }
        Integer x = getXPosition(context);
        if (x == null) {
            return Collections.emptyList();
        }
        return Collections.emptyList();
    }

    @Override
    public int getArgumentElements() {
        return 2;
    }

    private Integer getXPosition(CommandContext context) {
        if (context.hasArgument("structure_x", Integer.class)) {
            return context.getArgument("structure_x", Integer.class);
        }
        if (context.hasArgument(this.argName, FilePos.class)) {
            return context.getArgument(this.argName, FilePos.class).x;
        }
        return null;
    }
}
