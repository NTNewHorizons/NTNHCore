package com.bufka.commandcompletion.misc;

public class FilePos {

    public final int x;
    public final int z;

    public FilePos(long longIn) {
        this.x = (int) longIn;
        this.z = (int) (longIn >> 32);
    }

    public FilePos(int xPos, int zPos) {
        this.x = xPos;
        this.z = zPos;
    }

    public FilePos offset(int xOff, int zOff) {
        return new FilePos(this.x + xOff, this.z + zOff);
    }

    public static long add(long pos, int x, int z) {
        return asLong(x + getX(pos), z + getZ(pos));
    }

    public static long asLong(int x, int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

    public static int getX(long pos) {
        return (int) pos;
    }

    public static int getZ(long pos) {
        return (int) (pos >> 32);
    }

    @Override
    public int hashCode() {
        int i = 1664525 * this.x + 1013904223;
        int j = 1664525 * (this.z ^ 0xDEADBEEF) + 1013904223;
        return i ^ j;
    }

    @Override
    public boolean equals(Object arg) {
        if (arg instanceof FilePos) {
            FilePos pos = (FilePos) arg;
            return pos.x == this.x && pos.z == this.z;
        }
        return false;
    }

    public long asLong() {
        return asLong(this.x, this.z);
    }

    @Override
    public String toString() {
        return "X: " + this.x + " Z: " + this.z;
    }

    public FilePos toChunkFile() {
        return new FilePos(this.x >> 5, this.z >> 5);
    }

    public FilePos toChunkPos() {
        return new FilePos(this.x >> 4, this.z >> 4);
    }

    public int getDistance(int xPos, int zPos) {
        int xDis = this.x - xPos;
        int zDis = this.z - zPos;
        return xDis * xDis + zDis * zDis;
    }

    public double getSqDistance(int xPos, int zPos) {
        return Math.sqrt(getDistance(xPos, zPos));
    }

    public int getSquDistane(FilePos pos) {
        return (int) getSqDistance(pos.x, pos.z);
    }
}
