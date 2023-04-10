package com.mvavrill.logicGamesSolver.game.futoshiki;

public class FutoshikiGrid<T> {

    private final T[][] grid;
    private final int[][] lineIneq;
    private final int[][] columnIneq;

    public FutoshikiGrid(final T[][] grid, final int[][] lineIneq, final int[][] columnIneq) {
        this.grid = grid;
        this.lineIneq = lineIneq;
        this.columnIneq = columnIneq;
    }

    public T[][] getGrid() {
        return grid;
    }

    public int[][] getLineIneq() {
        return lineIneq;
    }

    public int[][] getColumnIneq() {
        return columnIneq;
    }
}
