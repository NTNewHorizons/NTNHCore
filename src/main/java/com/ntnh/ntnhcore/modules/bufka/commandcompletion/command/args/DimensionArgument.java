package com.bufka.commandcompletion.command.args;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraftforge.common.DimensionManager;

import com.bufka.commandcompletion.command.CommandContext;

public class DimensionArgument implements IArgument<Integer> {

    boolean loaded;

    protected DimensionArgument(boolean loaded) {
        this.loaded = loaded;
    }

    public static DimensionArgument loaded() {
        return new DimensionArgument(true);
    }

    public static DimensionArgument any() {
        return new DimensionArgument(false);
    }

    @Override
    public Integer parse(StringWalker args, CommandContext context) throws CommandException {
        int value = args.readInt();
        if (this.loaded ? (DimensionManager.getWorld(value) == null) : !DimensionManager.isDimensionRegistered(value)) {
            throw new CommandException("commands.completion.error.parse.dimension_missing", value);
        }
        return value;
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        List<String> list = new ArrayList<>();
        for (int entry : (this.loaded ? DimensionManager.getIDs() : DimensionManager.getStaticDimensionIDs())) {
            list.add(Integer.toString(entry));
        }
        return list;
    }
}
