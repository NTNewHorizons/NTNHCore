package com.bufka.commandcompletion.command.args;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

import com.bufka.commandcompletion.command.CommandContext;
import com.bufka.commandcompletion.misc.FilePos;

public class PositionArgument implements IArgument<FilePos> {

    private static final List<String> FIRST = Arrays.asList("0", "~", "^", "^1", "~1", "b2");
    private static final List<String> SECOND = Arrays.asList("0", "~", "^", "^-5", "~-5", "b-5");

    boolean chunkPos;

    protected PositionArgument(boolean chunkPos) {
        this.chunkPos = chunkPos;
    }

    public static PositionArgument blockPos() {
        return new PositionArgument(false);
    }

    public static PositionArgument chunkPos() {
        return new PositionArgument(true);
    }

    @Override
    public FilePos parse(StringWalker args, CommandContext context) throws CommandException {
        int playerX = 0;
        int playerZ = 0;
        int spawnX = 0;
        int spawnZ = 0;

        if (context.getSender() instanceof Entity) {
            Entity entity = (Entity) context.getSender();
            playerX = (int) entity.posX;
            playerZ = (int) entity.posZ;
            ChunkCoordinates spawn = entity.worldObj.getSpawnPoint();
            spawnX = spawn.posX;
            spawnZ = spawn.posZ;
        }

        if (!this.chunkPos) {
            playerX >>= 4;
            playerZ >>= 4;
            spawnX >>= 4;
            spawnZ >>= 4;
        }

        return new FilePos(
            args.parsePosition(playerX, spawnX, this.chunkPos),
            args.parsePosition(playerZ, spawnZ, this.chunkPos));
    }

    @Override
    public int getArgumentElements() {
        return 2;
    }

    @Override
    public List<String> getExamples(CommandContext context, int argumentIndex) {
        return argumentIndex == 0 ? FIRST : SECOND;
    }
}
