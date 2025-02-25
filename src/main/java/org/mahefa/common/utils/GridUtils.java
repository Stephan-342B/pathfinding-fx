package org.mahefa.common.utils;

import org.mahefa.common.enumerator.Direction;
import org.mahefa.component.*;

import java.util.*;

public final class GridUtils {

    private static final Random random = new Random();

    public static Cell pickRandomCell(Grid grid) {
        int rowLen = grid.getRowLen();
        int colLen = grid.getColLen();

        // Ensure the random position is not at the edge
        int x = 1 + random.nextInt((rowLen - 3) / 2) * 2;
        int y = 1 + random.nextInt((colLen - 3) / 2) * 2;

        return grid.getCellAt(x, y);
    }

    public static Cell getRandomNeighbour(Grid grid, Cell currentCell, boolean thickedWall) {
        // Current location
        Location currentLocation = currentCell.getLocation();

        // Choose a random neighbor to move
        Direction currentDirection = Direction.getRandomDirection();

        currentLocation = currentLocation.move(currentDirection);

        if (thickedWall) {
            // Move twice
            currentLocation = currentLocation.move(currentDirection);
        }

        return (checkBoundaries(grid, currentLocation)) ? grid.getCellAt(currentLocation)
                : getRandomNeighbour(grid, currentCell, thickedWall);
    }

    public static List<Cell> getNeighbors(Grid grid, Cell currentCell) {
        List<Cell> neighbours = new LinkedList<>();

        // Current location
        final Location currentLocation = currentCell.getLocation();

        Location up = currentLocation.move(Direction.UP);
        Location down = currentLocation.move(Direction.DOWN);
        Location left = currentLocation.move(Direction.LEFT);
        Location right = currentLocation.move(Direction.RIGHT);

        if (checkBoundaries(grid, right))
            neighbours.add(grid.getCellAt(right));

        if (checkBoundaries(grid, up))
            neighbours.add(grid.getCellAt(up));

        if (checkBoundaries(grid, down))
            neighbours.add(grid.getCellAt(down));

        if (checkBoundaries(grid, left))
            neighbours.add(grid.getCellAt(left));

        return neighbours;
    }

    public static boolean checkBoundaries(Grid grid, Location location) {
        int x = location.getRow();
        int y = location.getCol();

        return x >= 0 && x < grid.getRowLen() && y >= 0 && y < grid.getColLen();
    }

    public static boolean isOnTheEdge(Grid grid, Cell cell) {
        return isOnTheEdge(grid, cell.getLocation());
    }

    public static boolean isOnTheEdge(Grid grid, Location location) {
        int x = location.getRow();
        int y = location.getCol();

        return (x == 0 || x == grid.getRowLen() - 1) || (y == 0 || y == grid.getColLen() - 1);
    }

    public static boolean isValidMove(Grid grid, Location location) {
        return checkBoundaries(grid, location) && !isOnTheEdge(grid, location);
    }

    public static Cost getCost(Cell currentCell, Cell targetCell, Direction currentDirection) {
        Location currentLocation = currentCell.getLocation();
        Location targetLocation = targetCell.getLocation();
        int row1 = currentLocation.getRow();
        int col1 = currentLocation.getCol();
        int row2 = targetLocation.getRow();
        int col2 = targetLocation.getCol();

        if (row2 < row1 && col1 == col2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(1, Arrays.asList(Direction.FORWARD), Direction.UP);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(2, Arrays.asList(Direction.LEFT, Direction.FORWARD), Direction.UP);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(2, Arrays.asList(Direction.RIGHT, Direction.FORWARD), Direction.UP);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(3, Arrays.asList(Direction.RIGHT, Direction.RIGHT, Direction.FORWARD), Direction.UP);
            }
        } else if (row2 > row1 && col1 == col2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(3, Arrays.asList(Direction.RIGHT, Direction.RIGHT, Direction.FORWARD), Direction.DOWN);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(2, Arrays.asList(Direction.RIGHT, Direction.FORWARD), Direction.DOWN);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(2, Arrays.asList(Direction.LEFT, Direction.FORWARD), Direction.DOWN);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(1, Arrays.asList(Direction.FORWARD), Direction.DOWN);
            }
        }

        if (col2 < col1 && row1 == row2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(2, Arrays.asList(Direction.LEFT, Direction.FORWARD), Direction.LEFT);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(3, Arrays.asList(Direction.LEFT, Direction.LEFT, Direction.FORWARD), Direction.LEFT);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(1, Arrays.asList(Direction.FORWARD), Direction.LEFT);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(2, Arrays.asList(Direction.RIGHT, Direction.FORWARD), Direction.LEFT);
            }
        } else if (col2 > col1 && row1 == row2) {
            if (currentDirection.equals(Direction.UP)) {
                return new Cost(2, Arrays.asList(Direction.RIGHT, Direction.FORWARD), Direction.RIGHT);
            } else if (currentDirection.equals(Direction.RIGHT)) {
                return new Cost(1, Arrays.asList(Direction.FORWARD), Direction.RIGHT);
            } else if (currentDirection.equals(Direction.LEFT)) {
                return new Cost(3, Arrays.asList(Direction.RIGHT, Direction.RIGHT, Direction.FORWARD), Direction.RIGHT);
            } else if (currentDirection.equals(Direction.DOWN)) {
                return new Cost(2, Arrays.asList(Direction.LEFT, Direction.FORWARD), Direction.RIGHT);
            }
        }

        return new Cost(0, null, currentDirection);
    }
}
