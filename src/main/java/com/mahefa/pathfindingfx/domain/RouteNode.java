package com.mahefa.pathfindingfx.domain;

import com.mahefa.pathfindingfx.domain.enumerator.Direction;
import com.mahefa.pathfindingfx.domain.enumerator.Rotate;

import java.util.List;

public class RouteNode implements Comparable<RouteNode> {

    private Location current;
    private Location previous;
    private Direction direction;
    private List<Rotate> moves;

    // A* variables
    private double g;      // Cost of the path from the start node to n
    private double f;
    private double h;

    public RouteNode(Location current) {
        this(current, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public RouteNode(Location current, double g, double f) {
        this.current = current;
        this.g = g;
        this.f = f;
    }

    public Location getCurrent() {
        return current;
    }

    public void setCurrent(Location current) {
        this.current = current;
    }

    public Location getPrevious() {
        return previous;
    }

    public void setPrevious(Location previous) {
        this.previous = previous;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public List<Rotate> getMoves() {
        return moves;
    }

    public void setMoves(List<Rotate> moves) {
        this.moves = moves;
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
