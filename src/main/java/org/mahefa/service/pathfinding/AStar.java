package org.mahefa.service.pathfinding;

import javafx.animation.AnimationTimer;
import org.mahefa.common.enumerator.Direction;
import org.mahefa.common.utils.GridUtils;
import org.mahefa.component.*;
import org.mahefa.component.collection.Heap;
import org.mahefa.component.collection.MinHeap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.mahefa.common.CellStyle.Flag;

public class AStar extends Solver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AStar.class);

    public AStar(Grid grid) {
        super(grid);
    }

    @Override
    public AnimationTimer solve() {
        return new AnimationTimer() {

            private Cell currentCell, targetCell;
            private Heap<RouteNode> openSet;
            private Set<Cell> closedSet;

            @Override
            public void start() {
                lastToggle = 0;
                openSet = new MinHeap<>();
                closedSet = new HashSet<>(0);
                targetCell = grid.getTargetCell();
                nodes = new HashMap<>(0);

                // Add start node in the open set
                RouteNode start = new RouteNode(
                        grid.getStartCell(), 0d, distance(grid.getStartCell(), targetCell)
                );
                start.setDirection(Direction.UP);
                openSet.add(start);
                nodes.put(grid.getStartCell(), start);

                setIsRunning(true);
                super.start();
            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void handle(long now) {
                if ((now - lastToggle) >= currentSpeed) {
                    if (!openSet.isEmpty()) {
                        // Get cell having the lowest f score value
                        currentRouteNode = openSet.get();
                        currentCell = currentRouteNode.getCurrent();
                        currentCell.setFlag(Flag.POINTER);

                        LOGGER.debug(
                                "Row: {} Col: {} [F: {}, G: {}, H: {}]",
                                currentCell.getLocation().getRow(), currentCell.getLocation().getCol(),
                                currentRouteNode.getF(), currentRouteNode.getG(), currentRouteNode.getH()
                        );

                        // Target reached
                        if (currentCell.equals(targetCell)) {
                            drawback().start();
                            stop();
                        }

                        openSet.remove(currentRouteNode);
                        closedSet.add(currentCell);

                        List<Cell> neighbors = GridUtils.getNeighbors(grid, currentCell);
                        Iterator<Cell> iterator = neighbors.iterator();

                        while (iterator.hasNext()) {
                            Cell neighbor = iterator.next();
                            Flag neighborFlag = neighbor.getFlag();

                            // Skip if neighbor is a wall or already in the closedSet
                            if (neighborFlag.equals(Flag.WALL_NODE) || closedSet.contains(neighbor))
                                continue;

                            RouteNode neighborRouteNode = nodes.getOrDefault(neighbor, new RouteNode(neighbor));
                            nodes.put(neighbor, neighborRouteNode);

                            /**
                             * d(current, neighbor) is the weight of the edge from current to neighbor
                             * tentativeGScore is the distance from start to the neighbor through current
                             * Uniform grid: d(current, neighbor) = 1
                             *
                             * Breaking ties: Adjust the G value based on the cost of moving to another tile
                             */
                            Cost cost = GridUtils.getCost(currentCell, neighbor, currentRouteNode.getDirection());
                            double tentativeGScore = currentRouteNode.getG() + neighbor.getWeight() + cost.getValue();

                            // This path to neighbor is better than any previous one
                            if (tentativeGScore < neighborRouteNode.getG()) {
                                neighborRouteNode.setG(tentativeGScore);
                                neighborRouteNode.setH(distance(neighbor, targetCell));
                                neighborRouteNode.setF(tentativeGScore + neighborRouteNode.getH());
                                neighborRouteNode.setPrevious(currentCell);
                                neighborRouteNode.setActions(cost.getActions());
                                neighborRouteNode.setDirection(cost.getCurrentDirection());

                                if (!openSet.contains(neighborRouteNode)) {
                                    openSet.add(neighborRouteNode);
                                }
                            }
                        }

                        currentCell.revertFlag();
                        currentCell.setFlag(Flag.VISITED);
                    } else {
                        stop();
                        setIsRunning(false);
                    }

                    lastToggle = now;
                }
            }
        };
    }

    @Override
    public AnimationTimer drawback() {
        return new AnimationTimer() {

            private Cell currentCell;
            private List<Cell> path = new ArrayList<>();
            private int i;

            @Override
            public void start() {
                i = 0;
                RouteNode node = currentRouteNode;

                while (node != null) {
                    path.add(node.getCurrent());
                    node = nodes.get(node.getPrevious());
                }

                // Reverse to get the path from start to target
                Collections.reverse(path);
                currentCell = path.get(0);

                super.start();
            }

            @Override
            public void handle(long now) {
                if ((now - lastToggle) >= currentSpeed) {
                    if (currentCell != null) {
                        currentCell.setFlag(Flag.POINTER);

                        i++;
                        if (i == path.size()) {
                            setIsRunning(false);
                            super.stop();
                        } else {
                            currentCell = path.get(i);
                        }
                    }

                    lastToggle = now;
                }
            }
        };
    }

    /**
     * Heuristic function that estimates the cost of the cheapest path from n to the goal
     * The following method use the Manhattan distance
     *
     * @param currentCell
     * @param targetCell
     * @return
     */
    private double distance(Cell currentCell, Cell targetCell) {
        Location currentLocation = currentCell.getLocation();
        Location targetLocation = targetCell.getLocation();

        double row = Math.abs(currentLocation.getRow() - targetLocation.getRow());
        double col = Math.abs(currentLocation.getCol() - targetLocation.getCol());

        return row + col;
    }
}
