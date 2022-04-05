package org.mahefa.data;

import javafx.beans.property.SimpleObjectProperty;
import org.mahefa.common.enumerator.Direction;
import org.mahefa.common.enumerator.Flag;

import java.util.Objects;
import java.util.Set;

public class Cell extends ComparableNode {

    private Location location;
    private Set<Direction> neighbours;
    private SimpleObjectProperty<Flag> flag;

    // TODO
    private int f = 0;
    private int g = 0;      // Cost of the path from the start node to n
    private int h = 0;      // Heuristic function that estimates the cost of the cheapest path from n to the goal

    public Cell(Location location, Flag flag) {
        this.location = location;
        this.flag = new SimpleObjectProperty<>(flag);
    }

    public Set<Direction> getNeighbours() {
        return neighbours;
    }

    public Location getLocation() {
        return location;
    }

    public Flag getFlag() {
        return flag.get();
    }

    public SimpleObjectProperty<Flag> flagProperty() {
        return flag;
    }

    public void setFlag(Flag flag) {
        this.flag.set(flag);
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
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
        return Objects.hash(getNeighbours(), getLocation(), getFlag(), getF(), getG(), getH());
    }
}
