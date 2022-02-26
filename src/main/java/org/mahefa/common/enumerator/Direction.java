package org.mahefa.common.enumerator;

import com.sun.javafx.geom.Vec2d;

public enum Direction {

    UP(0, -1), RIGHT(1, 0), DOWN(0, 1), LEFT(-1, 0);

    private final int x;
    private final int y;

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

    public Vec2d toValue() {
        return new Vec2d(x, y);
    }
}
