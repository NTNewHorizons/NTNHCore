package com.bufka.commandcompletion.command.args;

import net.minecraft.command.CommandException;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.SyntaxErrorException;

public class StringWalker {

    String[] arguments;
    int max;
    int index;

    public StringWalker(String[] arguments) {
        this.arguments = arguments;
        this.max = arguments.length;
    }

    public StringWalker(String[] arguments, int index) {
        this.arguments = arguments;
        this.max = arguments.length;
        this.index = index;
    }

    public void apply(StringWalker other) {
        this.index = other.index;
    }

    public StringWalker copy() {
        return new StringWalker(this.arguments, this.index);
    }

    public void capTop() {
        this.max--;
    }

    public void resetCap() {
        this.max = this.arguments.length;
    }

    public boolean canRead() {
        return this.index < this.max;
    }

    public String peek() {
        return this.arguments[this.index];
    }

    public String poll() {
        return this.arguments[this.index++];
    }

    public void advance(int amount) {
        this.index += amount;
    }

    public void skip() {
        this.index++;
    }

    public void undo() {
        this.index--;
    }

    public int getIndex() {
        return this.index;
    }

    public int parsePosition(int playerPos, int worldPos, boolean chunk) throws CommandException {
        if (!canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        String value = peek();
        try {
            int result = 0;
            int offset = 0;
            if (value.charAt(0) == '^') {
                result += worldPos;
                offset++;
            } else if (value.charAt(0) == '~') {
                result += playerPos;
                offset++;
            }
            boolean isBlock = value.charAt(value.length() - 1) == 'b';
            String subString = value.substring(offset, value.length() - (isBlock ? 1 : 0));
            if (!subString.isEmpty()) {
                result += Integer.parseInt(subString) / ((isBlock && chunk) ? 16 : 1);
            }
            skip();
            return result;
        } catch (Exception e) {
            throw new NumberInvalidException("commands.completion.error.parse.int", value);
        }
    }

    public int parseChunkOrBlock(boolean chunk) throws CommandException {
        if (!canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        String value = peek();
        try {
            boolean isBlock = value.charAt(value.length() - 1) == 'b';
            int result = Integer.parseInt(value.substring(0, value.length() - (isBlock ? 1 : 0)))
                / ((isBlock && chunk) ? 16 : 1);
            skip();
            return result;
        } catch (Exception e) {
            throw new NumberInvalidException("commands.completion.error.parse.int", value);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Enum<T>> T readEnum(Class<T> clz) throws CommandException {
        if (!canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        try {
            return Enum.valueOf(clz, poll());
        } catch (Exception e) {
            throw new NumberInvalidException("commands.completion.error.parse.int", this.arguments[this.index - 1]);
        }
    }

    public boolean readBoolean() throws CommandException {
        if (!canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        String arg = poll();
        if (arg.equalsIgnoreCase("true")) return true;
        if (arg.equalsIgnoreCase("false")) return false;
        throw new SyntaxErrorException("commands.completion.error.parse.boolean", this.arguments[this.index - 1]);
    }

    public int readInt() throws CommandException {
        if (!canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        try {
            return Integer.parseInt(poll());
        } catch (Exception e) {
            throw new NumberInvalidException("commands.completion.error.parse.int", this.arguments[this.index - 1]);
        }
    }

    public long readLong() throws CommandException {
        if (!canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        try {
            return Long.parseLong(poll());
        } catch (Exception e) {
            throw new NumberInvalidException("commands.completion.error.parse.long", this.arguments[this.index - 1]);
        }
    }

    public double readDouble() throws CommandException {
        if (!canRead()) {
            throw new CommandException("commands.completion.error.parse.missing.args");
        }
        try {
            return Double.parseDouble(poll());
        } catch (Exception e) {
            throw new NumberInvalidException("commands.completion.error.parse.double", this.arguments[this.index - 1]);
        }
    }
}
