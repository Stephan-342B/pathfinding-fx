package org.mahefa.data;

public class Location {

    private final int x;
    private final int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getLayoutX(int CELL_SIZE) {
        if(x == 0) {
            return CELL_SIZE / 2;
        }

        return (x * CELL_SIZE) + CELL_SIZE / 2;
    }

    public double getLayoutY(int CELL_SIZE) {
        if(y == 0) {
            return CELL_SIZE / 2;
        }

        return (y * CELL_SIZE) + CELL_SIZE / 2;
    }
}
