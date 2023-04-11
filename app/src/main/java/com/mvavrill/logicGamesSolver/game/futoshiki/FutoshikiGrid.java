package com.mvavrill.logicGamesSolver.game.futoshiki;

import android.util.Log;

import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

import java.util.Arrays;

public class FutoshikiGrid<T> {

    private final static int INITIAL_SIZE = 3;
    private final T[][] grid;
    private final int[][] lineIneq;
    private final int[][] columnIneq;

    public FutoshikiGrid(final T[][] grid, final int[][] lineIneq, final int[][] columnIneq) {
        this.grid = grid;
        this.lineIneq = lineIneq;
        this.columnIneq = columnIneq;
    }

    public static FutoshikiGrid<DigitCell> generateInitialGrid() {
        DigitCell[][] grid = new DigitCell[INITIAL_SIZE][INITIAL_SIZE];
        boolean[] hints = new boolean[INITIAL_SIZE+1];
        Arrays.fill(hints, true);
        for (int i = 0; i < INITIAL_SIZE; i++) {
            for (int j = 0; j < INITIAL_SIZE; j++) {
                grid[i][j] = new DigitCell(hints);
            }
        }
        return new FutoshikiGrid<>(grid, new int[INITIAL_SIZE][INITIAL_SIZE - 1], new int[INITIAL_SIZE - 1][INITIAL_SIZE]);
    }

    public static FutoshikiGrid<DigitCell> changeSize(final FutoshikiGrid<DigitCell> inputGrid, final int increase) {
        DigitCell[][] newGrid = new DigitCell[inputGrid.grid.length+increase][inputGrid.grid.length+increase];
        boolean[] hints = new boolean[newGrid.length];
        Arrays.fill(hints, true);
        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid.length; j++) {
                if (i < inputGrid.grid.length && j < inputGrid.grid[i].length && inputGrid.grid[i][j].isFixed()) {
                    newGrid[i][j] = new DigitCell(true, inputGrid.grid[i][j].getValue());
                }
                else {
                    newGrid[i][j] = new DigitCell(hints);
                }
            }
        }
        int[][] lineIneq = new int[newGrid.length][];
        for (int i = 0; i < newGrid.length; i++) {
            if (i < inputGrid.lineIneq.length) {
                lineIneq[i] = Arrays.copyOf(inputGrid.lineIneq[i], newGrid.length-1);
            }
            else {
                lineIneq[i] = new int[newGrid.length-1];
            }
        }
        int[][] columnIneq = new int[newGrid.length-1][];
        for (int i = 0; i < newGrid.length-1; i++) {
            if (i < inputGrid.columnIneq.length) {
                columnIneq[i] = Arrays.copyOf(inputGrid.columnIneq[i], newGrid.length);
            }
            else {
                columnIneq[i] = new int[newGrid.length];
            }
        }
        return new FutoshikiGrid<>(newGrid, lineIneq, inputGrid.getColumnIneq());
    }

    public static FutoshikiGrid<Integer> extractFixedCells(final FutoshikiGrid<DigitCell> inputGrid) {
        Integer[][] fixedGrid = new Integer[inputGrid.grid.length][inputGrid.grid.length];
        for (int i = 0; i < inputGrid.grid.length; i++) {
            for (int j = 0; j < inputGrid.grid.length; j++) {
                if (inputGrid.grid[i][j].isFixed()) {
                    fixedGrid[i][j] = inputGrid.grid[i][j].getValue();
                }
                else {
                    fixedGrid[i][j] = 0;
                }
            }
        }
        int[][] lineIneq = new int[inputGrid.lineIneq.length][];
        for (int i = 0; i < inputGrid.lineIneq.length; i++) {
            lineIneq[i] = Arrays.copyOf(inputGrid.lineIneq[i], inputGrid.lineIneq[i].length);
        }
        int[][] columnIneq = new int[inputGrid.columnIneq.length][];
        for (int i = 0; i < inputGrid.columnIneq.length; i++) {
            columnIneq[i] = Arrays.copyOf(inputGrid.columnIneq[i], inputGrid.columnIneq[i].length);
        }
        return new FutoshikiGrid<>(fixedGrid, lineIneq, columnIneq);
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

    public String toString() {
        StringBuilder s = new StringBuilder();
        for (T[] line : grid) {
            s.append(Arrays.toString(line)).append("\n");
        }
        for (int[] line : lineIneq) {
            s.append(Arrays.toString(line)).append("\n");
        }
        for (int[] column : columnIneq) {
            s.append(Arrays.toString(column)).append("\n");
        }
        return s.toString();
    }
}
