package org.mahefa.component;

import org.mahefa.common.enumerator.Direction;
import org.mahefa.common.enumerator.Rotate;

import java.util.List;

public class Cost {

    private double value;
    private List<Rotate> moves;
    private Direction currentDirection;

    public Cost(double value, List<Rotate> moves, Direction currentDirection) {
        this.value = value;
        this.moves = moves;
        this.currentDirection = currentDirection;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Rotate> getMoves() {
        return moves;
    }

    public void setMoves(List<Rotate> moves) {
        this.moves = moves;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
    }
}
