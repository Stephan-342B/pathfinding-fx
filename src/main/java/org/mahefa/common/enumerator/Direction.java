package org.mahefa.common.enumerator;

import java.util.Random;

public enum Direction {

    FORWARD(0, 0), UP(-1, 0), RIGHT(0, 1), DOWN(1, 0), LEFT(0, -1);

    private final int x;
    private final int y;

    private static final Random RANDOM = new Random();
    private static final Direction[] VALUES = values();

    Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static Direction valueFor(Orientation orientation) {
        return valueOf(orientation.name());
    }

    public static Direction getRandomDirection() {
        return VALUES[RANDOM.nextInt(VALUES.length)];
    }
}