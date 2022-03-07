package com.mvavrill.logicGamesSolver.model.games.kakuro;

import com.mvavrill.logicGamesSolver.model.cells.Cell;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

public class KakuroSolver {

    private final Cell[][] grid;

    public KakuroSolver(final Cell[][] grid) {
        this.grid = grid;
    }

    public Cell[][] extractInformation() {
        return grid;
    }
}
