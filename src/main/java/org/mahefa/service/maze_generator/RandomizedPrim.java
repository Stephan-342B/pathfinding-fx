package org.mahefa.service.maze_generator;

import org.springframework.stereotype.Component;

@Component
public class RandomizedPrim /*extends MazeGenerator*/ {

//    @Override
//    public void generate(Grid grid) {
//
//        Cell[][] cells = grid.getCells();
//
//        final int rowLen = cells.length;
//        final int colLen = cells[0].length;
//        this.visited = new boolean[rowLen][colLen];
//
//        // Pick a cell
//        Cell currentWall = ArrayUtils.pickRandomly(cells);
//        Location location = currentWall.getLocation();
//
//        currentWall.setFlag(Flag.PATH);
//        setAsVisited(location);
//        func.applyAsInt(location.getX(), location.getY());
//
//        // Add the walls of the cell to the wall list
//        List<Location> wallLocations = currentWall.getNeighbours();
//
//        int duration = 10;
//
//        while(!wallLocations.isEmpty()) {
//            // Pick a random wall from the list
//            location = ArrayUtils.pickRandomly(wallLocations);
//            currentWall = cells[location.getX()][location.getY()];
//
//            // If only one of the cells that the wall divides is visited
//            long count = currentWall.getNeighbours()
//                    .stream()
//                    .filter(l -> this.visited[l.getX()][l.getY()]).count();
//
//            if(count == 1L) {
//                // Make the wall a passage and mark the unvisited cell as part of the maze
//                currentWall.setFlag(Flag.PATH);
//                setAsVisited(location);
//                func.applyAsInt(location.getX(), location.getY());
//
////                addKeyFrame(duration, currentWall, func);
////                duration += duration;
//
//                // Add the neighboring walls of the cell to the wall list
//                wallLocations.addAll(currentWall.getNeighbours());
//            }
//
//            wallLocations.remove(location);
//        }
//    }
}
