package org.mahefa.component;

import org.mahefa.common.enumerator.Direction;

import java.util.Objects;

public class Location {

    private int row;
    private int col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Location move(Direction direction) {
        return new Location(this.row + direction.getX(), this.col + direction.getY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return getRow() == location.getRow() &&
                getCol() == location.getCol();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getCol());
    }
}
