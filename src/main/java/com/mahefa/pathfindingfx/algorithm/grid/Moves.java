package com.mahefa.pathfindingfx.algorithm.grid;

import com.mahefa.pathfindingfx.domain.Cost;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.domain.enumerator.Direction;
import com.mahefa.pathfindingfx.domain.enumerator.Rotate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Pure movement helpers over a {@link GridModel}: the 4-neighbourhood and the turn/step cost used by
 * A*. Ported from the (UI-coupled) {@code GridUtils} to operate on {@link Location}s only, so the
 * pathfinding steppers stay JavaFX-free. Logic is unchanged from {@code GridUtils.getCost}.
 */
public final class Moves {

    private Moves() {
    }

    /** In-bounds cardinal neighbours, in the original order (right, up, down, left). */
    public static List<Location> neighbours(GridModel model, Location location) {
        List<Location> result = new ArrayList<>(4);
        Location right = location.move(Direction.RIGHT);
        Location up = location.move(Direction.UP);
        Location down = location.move(Direction.DOWN);
        Location left = location.move(Direction.LEFT);

        if (model.inBounds(right)) result.add(right);
        if (model.inBounds(up)) result.add(up);
        if (model.inBounds(down)) result.add(down);
        if (model.inBounds(left)) result.add(left);
        return result;
    }

    /** Step cost + turn sequence to move from {@code current} to {@code neighbour} given the heading. */
    public static Cost cost(Location current, Location neighbour, Direction currentDirection) {
        int row1 = current.getRow();
        int col1 = current.getCol();
        int row2 = neighbour.getRow();
        int col2 = neighbour.getCol();

        if (row2 < row1 && col1 == col2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(1, Arrays.asList(Rotate.FORWARD), Direction.UP);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(2, Arrays.asList(Rotate.LEFT, Rotate.FORWARD), Direction.UP);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(2, Arrays.asList(Rotate.RIGHT, Rotate.FORWARD), Direction.UP);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(3, Arrays.asList(Rotate.RIGHT, Rotate.RIGHT, Rotate.FORWARD), Direction.UP);
            }
        } else if (row2 > row1 && col1 == col2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(3, Arrays.asList(Rotate.RIGHT, Rotate.RIGHT, Rotate.FORWARD), Direction.DOWN);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(2, Arrays.asList(Rotate.RIGHT, Rotate.FORWARD), Direction.DOWN);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(2, Arrays.asList(Rotate.LEFT, Rotate.FORWARD), Direction.DOWN);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(1, Arrays.asList(Rotate.FORWARD), Direction.DOWN);
            }
        }

        if (col2 < col1 && row1 == row2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(2, Arrays.asList(Rotate.LEFT, Rotate.FORWARD), Direction.LEFT);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(3, Arrays.asList(Rotate.LEFT, Rotate.LEFT, Rotate.FORWARD), Direction.LEFT);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(1, Arrays.asList(Rotate.FORWARD), Direction.LEFT);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(2, Arrays.asList(Rotate.RIGHT, Rotate.FORWARD), Direction.LEFT);
            }
        } else if (col2 > col1 && row1 == row2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(2, Arrays.asList(Rotate.RIGHT, Rotate.FORWARD), Direction.RIGHT);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(1, Arrays.asList(Rotate.FORWARD), Direction.RIGHT);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(3, Arrays.asList(Rotate.RIGHT, Rotate.RIGHT, Rotate.FORWARD), Direction.RIGHT);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(2, Arrays.asList(Rotate.LEFT, Rotate.FORWARD), Direction.RIGHT);
            }
        }

        return new Cost(0, null, currentDirection);
    }
}
