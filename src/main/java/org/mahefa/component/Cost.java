package org.mahefa.component;

import org.mahefa.common.enumerator.Direction;

import java.util.List;

public class Cost {

    private double value;
    private List<Direction> actions;
    private Direction currentDirection;

    public Cost(double value, List<Direction> actions, Direction currentDirection) {
        this.value = value;
        this.actions = actions;
        this.currentDirection = currentDirection;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Direction> getActions() {
        return actions;
    }

    public void setActions(List<Direction> actions) {
        this.actions = actions;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }
}
