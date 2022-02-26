package org.mahefa.data;

import java.util.function.IntBinaryOperator;

public class Grid {

    private Cell[][] cells;
    private boolean[][] visited;

    private int rowLen;
    private int colLen;

    public Grid(int CANVAS_WIDTH, int CANVAS_HEIGHT, int squareSize, IntBinaryOperator func) {
        final int gapRow = CANVAS_HEIGHT % squareSize;
        final int gapCol = CANVAS_WIDTH % squareSize;
        CANVAS_WIDTH -= gapCol;
        CANVAS_HEIGHT -= gapRow;

        this.rowLen = CANVAS_HEIGHT / squareSize;
        this.colLen = CANVAS_WIDTH / squareSize;

        this.cells = new Cell[rowLen][colLen];
        this.visited = new boolean[rowLen][colLen];

        int x = 0;
        int y = 0;

        for(int i = gapRow / 2; i < CANVAS_HEIGHT; i += squareSize) {
            for(int j = gapCol / 2; j < CANVAS_WIDTH; j += squareSize) {

                this.cells[x][y] = new Cell(new Location(x, y));
                func.applyAsInt(j, i);
                y++;
            }

            x++;
            y = 0;
        }
    }

    public void clear() {
        this.cells = new Cell[rowLen][colLen];
        this.visited = new boolean[rowLen][colLen];
    }
}
