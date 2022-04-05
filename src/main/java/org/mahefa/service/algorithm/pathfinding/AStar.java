package org.mahefa.service.algorithm.pathfinding;

import org.mahefa.common.enumerator.Flag;
import org.mahefa.data.Cell;
import org.mahefa.data.Location;
import org.mahefa.data.structure.MinHeap;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class AStar extends Solver {

    private MinHeap<Cell> openSet;
    private MinHeap<Cell> closedSet;
    private List<Cell> path;

    @PostConstruct
    public void init() {
        this.openSet = new MinHeap<>();
        this.closedSet = new MinHeap<>();
        this.path = new ArrayList<>();
    }

    @Override
    public void solve(Cell start, Cell end) {
        // Add start in the open set
        this.openSet.insert(start);

        // While the open set is not empty
        while (!this.openSet.isEmpty()) {
            // Get cell having the lowest f score value
            Cell currentCell = this.openSet.get();

            // In case of the end has been reached
            if(currentCell == end) {
                // TODO: Drawback path
                return;
            }

            this.openSet.remove(currentCell);
            this.closedSet.insert(currentCell);

            // Get neighbors
            final Cell[] neighbors = new Cell[1];

            for(Cell neighbor : neighbors) {
                if(!this.closedSet.contains(neighbor) && !neighbor.getFlag().equals(Flag.WALL)) {
                    final int tentative_gScore = currentCell.getG() + 1;

                    if(this.openSet.contains(neighbor)) {
                        if(tentative_gScore < neighbor.getG()) {
                            neighbor.setG(tentative_gScore);
                        }
                    } else {
                        neighbor.setG(tentative_gScore);
                        this.openSet.insert(neighbor);
                    }

                    neighbor.setH(heuristic(neighbor, end));
                    neighbor.setF(neighbor.getG() + neighbor.getH());
                    neighbor.setValue(neighbor.getF());
                    path.add(currentCell);
                }
            }
        }
    }

    int heuristic(Cell current, Cell end) {
        final Location currentCoordinates = current.getLocation();
        final Location endCoordinates = end.getLocation();

        return Math.abs(currentCoordinates.getX() - endCoordinates.getX())
                + Math.abs(currentCoordinates.getY() - endCoordinates.getY());
    }
}
