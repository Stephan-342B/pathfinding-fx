package org.mahefa.data;

import javafx.beans.property.SimpleObjectProperty;
import org.mahefa.common.enumerator.Direction;
import org.mahefa.common.enumerator.Flag;

import java.util.*;
import java.util.stream.Collectors;

public class Cell extends Cost {

    public int row;
    public int col;

    private Location location;
    private Stack<Flag> oldFlag = new Stack<>();
    private SimpleObjectProperty<Flag> flag;

    public Cell() {}

    public Cell(Location location) {
        this.location = location;
    }

    public Cell(int row, int col, int squareSize) {
        this.row = row;
        this.col = col;

        this.location = new Location(row / squareSize, col / squareSize);
    }

    public Set<Cell> getNeighbours(Grid grid) {
        List<Location> locations = Arrays.asList(
                location.move(Direction.UP), location.move(Direction.LEFT),
                location.move(Direction.DOWN), location.move(Direction.RIGHT)
        );

        return locations.stream()
                .filter(location -> (location.getX() >= 0 && location.getX() < grid.getRowLen()) && (location.getY() >= 0 && location.getY() < grid.getColLen()))
                .map(location -> grid.getCellAt(location.getX(), location.getY()))
                .collect(Collectors.toSet());
    }

    public Location getLocation() {
        return location;
    }

    public Stack<Flag> getOldFlag() {
        return oldFlag;
    }

    public void setOldFlag(Stack<Flag> oldFlag) {
        this.oldFlag = oldFlag;
    }

    public Flag getFlag() {
        return flag.get();
    }

    public SimpleObjectProperty<Flag> flagProperty() {
        return flag;
    }

    public void setFlagProperty(SimpleObjectProperty<Flag> flagProperty) {
        this.flag = flagProperty;
    }

    public void setFlag(Flag flag) {
        setFlag(flag, false);
    }

    public void revertFlag() {
        Flag oldValue = Flag.UNVISITED;

        if (!oldFlag.isEmpty())
            oldValue = oldFlag.pop();

        setFlag(oldValue, true);
    }

    public void resetFlag(Flag defaultFlag) {
        setFlag(defaultFlag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return Objects.equals(getLocation(), cell.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation(), getFlag());
    }

    private void setFlag(Flag value, boolean isRevert) {
        if (this.flag == null) {
            this.flag = new SimpleObjectProperty<>(value);

            return;
        }

        if (!isRevert)
            this.oldFlag.push(this.flag.get());

        this.flag.setValue(value);
    }
}
