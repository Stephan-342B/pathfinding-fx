package org.mahefa.service.pathfinding;

import javafx.animation.AnimationTimer;
import org.mahefa.component.Cell;
import org.mahefa.component.Grid;
import org.mahefa.component.RouteNode;
import org.mahefa.service.State;

import java.util.Map;
import java.util.function.Supplier;

public abstract class Solver extends State {

    protected Grid grid;
    protected RouteNode currentRouteNode;
    protected Map<Cell, RouteNode> nodes;

    protected Long currentSpeed;
    protected long lastToggle = 0L;

    public Solver(Grid grid) {
        this.grid = grid;
    }

    public abstract Supplier<AnimationTimer> solve();

    public void setCurrentSpeed(Long speed) {
        this.currentSpeed = speed;
    }
}
