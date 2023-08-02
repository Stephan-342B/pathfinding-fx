package org.mahefa.data;

import org.mahefa.common.enumerator.Direction;

import java.util.Objects;

public class Location {

    private int x;
    private int y;

    public Location() {}

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Location move(Direction direction) {
        return new Location(this.x + direction.getX(), this.y + direction.getY());
    }

    public static Location first() {
        return new Location(0, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return getX() == location.getX() &&
                getY() == location.getY();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY());
    }
}
