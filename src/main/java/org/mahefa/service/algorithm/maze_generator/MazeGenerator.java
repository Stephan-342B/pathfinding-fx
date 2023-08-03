package org.mahefa.service.algorithm.maze_generator;

import org.mahefa.data.Grid;
import org.mahefa.data.Location;

public abstract class MazeGenerator {

    protected Grid grid;
    protected boolean[][] visited;

    public abstract void generate(Grid grid);

    public boolean isVisited(Location location) {
        return this.visited[location.getX()][location.getY()];
    }

    public void setVisitedAt(Location location) {
        final int row = location.getX();
        final int col = location.getY();
        this.visited[row][col] = true;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
        this.visited = new boolean[this.grid.getRowLen()][this.grid.getColLen()];
    }
}
