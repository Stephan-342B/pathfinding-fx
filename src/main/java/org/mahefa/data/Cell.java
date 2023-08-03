package org.mahefa.data;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import org.mahefa.common.CellStyle.*;
import org.mahefa.common.enumerator.Direction;

import java.util.*;
import java.util.stream.Collectors;

import static org.mahefa.common.CellStyle.Flag.UNVISITED;
import static org.mahefa.common.CellStyle.*;

public class Cell extends Pane {

    private Location location;
    private Stack<Flag> oldFlag = new Stack<>();
    private ObjectProperty<Flag> flag = new SimpleObjectProperty<>(UNVISITED) {
        @Override
        public void invalidated() {
            pseudoClassStateChanged(START_PSEUDO_CLASS, false);
            pseudoClassStateChanged(TARGET_PSEUDO_CLASS, false);
            pseudoClassStateChanged(WALL_PSEUDO_CLASS, false);
            pseudoClassStateChanged(POINTER_PSEUDO_CLASS, false);
            pseudoClassStateChanged(PATH_PSEUDO_CLASS, false);
            pseudoClassStateChanged(VISITED_PSEUDO_CLASS, false);

            switch (get()) {
                case START:
                    pseudoClassStateChanged(START_PSEUDO_CLASS, true);
                    break;
                case TARGET:
                    pseudoClassStateChanged(TARGET_PSEUDO_CLASS, true);
                    break;
                case WALL:
                    pseudoClassStateChanged(WALL_PSEUDO_CLASS, true);
                    break;
                case POINTER:
                    pseudoClassStateChanged(POINTER_PSEUDO_CLASS, true);
                    break;
                case PATH:
                    pseudoClassStateChanged(PATH_PSEUDO_CLASS, true);
                    break;
                case VISITED:
                    pseudoClassStateChanged(VISITED_PSEUDO_CLASS, true);
                    break;
                default:
                    pseudoClassStateChanged(UNVISITED_PSEUDO_CLASS, true);
                    break;
            }
        }
    };

    public Cell(Location location) {
        super();

        this.location = location;
    }

    public Cell(int row, int col, int squareSize) {
        super();
        getStyleClass().add("cell");

        this.location = new Location(row / squareSize, col / squareSize);

        setId("cell_" + col + "_" + row);
        setLayoutX(col);
        setLayoutY(row);
        setPrefWidth(squareSize);
        setPrefHeight(squareSize);
        setPickOnBounds(true);
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

    public ObjectProperty<Flag> flagProperty() {
        return flag;
    }

    public void setFlagProperty(ObjectProperty<Flag> flagProperty) {
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
        if (!isRevert)
            this.oldFlag.push(this.flag.get());

        this.flag.setValue(value);
    }
}
