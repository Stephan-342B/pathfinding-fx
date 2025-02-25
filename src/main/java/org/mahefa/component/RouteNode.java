package org.mahefa.component;

import org.mahefa.common.enumerator.Direction;

import java.util.List;

public class RouteNode implements Comparable<RouteNode> {

    private Cell current;
    private Cell previous;
    private Direction direction;
    private List<Direction> actions;

    // A* variables
    private double g;      // Cost of the path from the start node to n
    private double f;
    private double h;

    public RouteNode(Cell current) {
        this(current, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public RouteNode(Cell current, double g, double f) {
        this.current = current;
        this.g = g;
        this.f = f;
    }

    public Cell getCurrent() {
        return current;
    }

    public void setCurrent(Cell current) {
        this.current = current;
    }

    public Cell getPrevious() {
        return previous;
    }

    public void setPrevious(Cell previous) {
        this.previous = previous;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<Direction> getActions() {
        return actions;
    }

    public void setActions(List<Direction> actions) {
        this.actions = actions;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getF() {
        return f;
    }

    public void setF(double f) {
        this.f = f;
    }

    public double getH() {
        return h;
    }

    public void setH(double h) {
        this.h = h;
    }

    @Override
    public int compareTo(RouteNode other) {
        if (this.f == other.f) {
            // Tie-breaking: H Cost
            return Double.compare(this.h, other.h);
        }

        return Double.compare(this.f, other.f);
    }
}
