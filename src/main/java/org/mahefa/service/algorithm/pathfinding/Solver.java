package org.mahefa.service.algorithm.pathfinding;

import org.mahefa.component.Cell;

import java.util.List;

public abstract class Solver {

    public void drawback(List<Cell> path) {
        // TODO
    }

    public abstract void solve(Cell start, Cell end);
}
