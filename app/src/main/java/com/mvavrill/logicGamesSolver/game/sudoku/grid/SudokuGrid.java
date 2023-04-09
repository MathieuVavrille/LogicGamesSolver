package com.mvavrill.logicGamesSolver.game.sudoku.grid;

import java.io.Serializable;

public interface SudokuGrid extends Serializable {
    public int get(int i, int j);
    public void set(int i, int j, int v);
    public int[][] solve();
}
