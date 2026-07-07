package org.mahefa.service.pathfinding;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.mahefa.common.enumerator.Direction;
import org.mahefa.common.enumerator.LaunchAnimationSpeed;
import org.mahefa.common.enumerator.NodeType;
import org.mahefa.common.utils.GridUtils;
import org.mahefa.common.utils.ImageUtils;
import org.mahefa.component.*;
import org.mahefa.component.collection.Heap;
import org.mahefa.component.collection.MinHeap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;

import static org.mahefa.common.CellStyle.Flag;

public class AStar extends Solver {

    private static final Logger LOGGER = LoggerFactory.getLogger(AStar.class);

    public AStar(Grid grid) {
        super(grid);
    }

    @Override
    public Supplier<AnimationTimer> solve() {
        return () -> new AnimationTimer() {

            private Cell currentCell, targetCell;
            private Heap<RouteNode> openSet;
            private Set<Cell> closedSet;

            @Override
            public void start() {
                openSet = new MinHeap<>();
                closedSet = new HashSet<>(0);
                targetCell = grid.getTargetCell();
                nodes = new HashMap<>(0);
                lastToggle = 0;

                // Reset the metrics surfaced to the narrative for this run.
                cellsExplored = 0;
                openSetSize = 0;
                pathFound = false;
                pathLength = 0;

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
            public void handle(long now) {
                if ((now - lastToggle) >= currentSpeed) {
                    if (!openSet.isEmpty()) {
                        openSetSize = openSet.size();
                        if (currentCell == null) {
                            // Get cell having the lowest f score value
                            currentRouteNode = openSet.get();
                            currentCell = currentRouteNode.getCurrent();
                            currentCell.setFlag(Flag.CURRENT);
                        } else {

                            Location cursor = currentCell.getLocation();
                            LOGGER.debug(
                                    "expand ({},{}) f={} g={} h={} · frontier open={} closed={}",
                                    cursor.getRow(), cursor.getCol(),
                                    currentRouteNode.getF(), currentRouteNode.getG(), currentRouteNode.getH(),
                                    openSet.size(), closedSet.size()
                            );

                            // Target reached
                            if (currentCell.equals(targetCell)) {
                                pathFound = true;
                                drawback().start();
                                super.stop();
                            }

                            openSet.remove(currentRouteNode);
                            closedSet.add(currentCell);
                            cellsExplored++;

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
                                    neighborRouteNode.setMoves(cost.getMoves());
                                    neighborRouteNode.setDirection(cost.getCurrentDirection());

                                    Location at = neighbor.getLocation();
                                    if (!openSet.contains(neighborRouteNode)) {
                                        openSet.add(neighborRouteNode);
                                        LOGGER.debug(
                                                "   ↳ push ({},{}) f={} g={} h={} dir={}",
                                                at.getRow(), at.getCol(),
                                                neighborRouteNode.getF(), neighborRouteNode.getG(),
                                                neighborRouteNode.getH(), cost.getCurrentDirection()
                                        );
                                    } else {
                                        LOGGER.debug(
                                                "   ↳ relax ({},{}) f={} g={} h={} dir={}",
                                                at.getRow(), at.getCol(),
                                                neighborRouteNode.getF(), neighborRouteNode.getG(),
                                                neighborRouteNode.getH(), cost.getCurrentDirection()
                                        );
                                    }
                                }
                            }

                            currentCell.revertFlag();
                            currentCell.setFlag(Flag.VISITED);
                            currentCell = null;
                        }
                    } else {
                        setIsRunning(false);
                        super.stop();
                    }

                    lastToggle = now;
                }
            }
        };
    }

    private AnimationTimer drawback() {
        return new AnimationTimer() {

            private Cell currentCell, priorCell;
            private ImageView currentImageView;
            private double currentAngle;

            private List<Cell> shortestPath = new ArrayList<>();
            private int i;

            @Override
            public void start() {
                i = 0;
                currentAngle = 0d;
                RouteNode node = currentRouteNode;

                while (node != null) {
                    shortestPath.add(node.getCurrent());
                    node = nodes.get(node.getPrevious());
                }

                // Reverse to get the path from start to target
                Collections.reverse(shortestPath);
                pathLength = shortestPath.size();

                // Get current cell
                currentCell = shortestPath.get(0);
                currentCell.getStyleClass().add("transparent");

                // Define current image
                ImageView imageView = (ImageView) currentCell.getChildren().get(0);
                currentImageView = new ImageView(imageView.getImage());
                currentImageView.setImage(new Image("/icons/triangletwo-up.png"));

                super.start();
            }

            @Override
            public void handle(long now) {
                if ((now - lastToggle) >= LaunchAnimationSpeed.SHORTEST_PATH.getInterval()) {
                    RouteNode currentRouteNode = nodes.get(currentCell);

                    // Get rotation angle
                    currentAngle += ImageUtils.getRotationAngle(currentRouteNode.getMoves());

                    if (priorCell != null && priorCell.getNodeType().equals(NodeType.NONE))
                        priorCell.getChildren().remove(0);

                    currentImageView.setRotate(currentAngle);
                    currentCell.setFlag(Flag.SHORTEST_PATH_NODE);

                    if (currentCell.getNodeType().equals(NodeType.TARGET)) {
                        ImageView imageView = (ImageView) currentCell.getChildren().get(0);
                        imageView.setImage(currentImageView.getImage());
                        imageView.setRotate(currentAngle);
                    } else {
                        if (!currentCell.isSpecialNode())
                            currentCell.getChildren().add(currentImageView);
                    }

                    if (++i == shortestPath.size()) {
                        setIsRunning(false);
                        super.stop();
                    } else {
                        priorCell = currentCell;
                        currentCell = shortestPath.get(i);

                        lastToggle = now;
                    }
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
