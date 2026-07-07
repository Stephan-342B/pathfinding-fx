package com.mahefa.pathfindingfx.algorithm.pathfinding;

import com.mahefa.pathfindingfx.algorithm.collection.Heap;
import com.mahefa.pathfindingfx.algorithm.collection.MinHeap;
import com.mahefa.pathfindingfx.algorithm.grid.GridModel;
import com.mahefa.pathfindingfx.algorithm.grid.Moves;
import com.mahefa.pathfindingfx.algorithm.step.AbstractStepper;
import com.mahefa.pathfindingfx.algorithm.step.Step;
import com.mahefa.pathfindingfx.algorithm.step.StepType;
import com.mahefa.pathfindingfx.domain.Cost;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.domain.RouteNode;
import com.mahefa.pathfindingfx.domain.enumerator.Direction;
import com.mahefa.pathfindingfx.util.ImageUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A* as a pure {@link com.mahefa.pathfindingfx.algorithm.step.Stepper}: one node expansion per
 * {@link #advance()}, emitting {@code CURRENT}/{@code VISITED} steps, then the {@code PATH} steps
 * (each carrying its cumulative arrow angle) once the target is reached. Same logic and heuristic
 * (Manhattan) as the original {@code AStar}, reusing {@link RouteNode}/{@link MinHeap}/{@link Cost},
 * but reading a {@link GridModel} and emitting steps instead of mutating cells.
 */
public class AStarStepper extends AbstractStepper {

    private final Heap<RouteNode> openSet = new MinHeap<>();
    private final Set<Location> closed = new HashSet<>();
    private final Map<Location, RouteNode> nodes = new HashMap<>();
    private final Location target;

    private Location previousCurrent;
    private boolean finished;

    public AStarStepper(GridModel model) {
        super(model);
        this.target = model.getTarget();

        RouteNode start = new RouteNode(model.getStart(), 0d, distance(model.getStart(), target));
        start.setDirection(Direction.UP);
        openSet.add(start);
        nodes.put(model.getStart(), start);
    }

    @Override
    protected boolean isComplete() {
        return finished;
    }

    @Override
    protected void advance() {
        if (openSet.isEmpty()) {
            finished = true; // no path
            return;
        }

        // Settle the previously highlighted cell, then highlight the new lowest-f cell.
        RouteNode currentNode = openSet.get();
        Location current = currentNode.getCurrent();
        if (previousCurrent != null && !previousCurrent.equals(current)) {
            emit(previousCurrent, StepType.VISITED);
        }
        emit(current, StepType.CURRENT);
        previousCurrent = current;

        if (current.equals(target)) {
            emitPath(currentNode);
            finished = true;
            return;
        }

        openSet.remove(currentNode);
        closed.add(current);

        for (Location neighbour : Moves.neighbours(model, current)) {
            if (model.isWall(neighbour) || closed.contains(neighbour)) {
                continue;
            }

            RouteNode neighbourNode = nodes.getOrDefault(neighbour, new RouteNode(neighbour));
            nodes.put(neighbour, neighbourNode);

            Cost cost = Moves.cost(current, neighbour, currentNode.getDirection());
            double tentativeG = currentNode.getG() + model.weightAt(neighbour) + cost.getValue();

            if (tentativeG < neighbourNode.getG()) {
                neighbourNode.setG(tentativeG);
                neighbourNode.setH(distance(neighbour, target));
                neighbourNode.setF(tentativeG + neighbourNode.getH());
                neighbourNode.setPrevious(current);
                neighbourNode.setMoves(cost.getMoves());
                neighbourNode.setDirection(cost.getCurrentDirection());

                if (!openSet.contains(neighbourNode)) {
                    openSet.add(neighbourNode);
                }
            }
        }
    }

    /** Rebuild start→target and emit a PATH step per cell with its cumulative rotation angle. */
    private void emitPath(RouteNode targetNode) {
        List<RouteNode> path = new ArrayList<>();
        RouteNode node = targetNode;
        while (node != null) {
            path.add(node);
            node = (node.getPrevious() != null) ? nodes.get(node.getPrevious()) : null;
        }
        Collections.reverse(path);

        double angle = 0d;
        for (RouteNode step : path) {
            angle += ImageUtils.getRotationAngle(step.getMoves());
            emit(Step.path(step.getCurrent(), angle));
        }
    }

    // Manhattan distance heuristic (matches the original AStar).
    private double distance(Location a, Location b) {
        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
    }
}
