package com.mvavrill.logicGamesSolver.model.games.sudoku;

import android.util.Log;

import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.chocosolver.util.tools.ArrayUtils;

public class SudokuSolver {

    private final DigitCell[][] grid;

    public SudokuSolver(final DigitCell[][] grid) {
        this.grid = grid;
    }

    public DigitCell[][] extractInformation() {
        Model model = new Model("sudoku");
        IntVar[][] vars = model.intVarMatrix(9, 9 , 0, 8);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                restrictVarFromCell(grid[i][j], vars[i][j]);
            }
        }
        for (int line = 0; line < 9; line++)
            model.allDifferent(vars[line]).post();
        for (int column = 0; column < 9; column++)
            model.allDifferent(ArrayUtils.getColumn(vars, column)).post();
        for (int I = 0; I < 3; I++) {
            for (int J = 0; J < 3; J++) {
                model.allDifferent(vars[3 * I][3 * J], vars[3 * I + 1][3 * J], vars[3 * I + 2][3 * J], vars[3 * I][3 * J + 1], vars[3 * I + 1][3 * J + 1], vars[3 * I + 2][3 * J + 1], vars[3 * I][3 * J + 2], vars[3 * I + 1][3 * J + 2], vars[3 * I + 2][3 * J + 2]).post();
            }
        }
        Solver solver = model.getSolver();
        Log.d("Mat",""+vars[0][0]);
        try {
            solver.propagate();
        }
        catch (Exception e) {
            return null;
        }
        Log.d("Mat",""+vars[0][0]);

        DigitCell[][] propagatedGrid = gridFromVars(vars);
        if (!solver.solve())
            return null;
        DigitCell[][] solvedGrid = gridFromVars(vars);
        if (solver.solve())
            return propagatedGrid;
        else
            return solvedGrid;
    }

    private void restrictVarFromCell(final DigitCell cell, final IntVar var) {
        if (cell.getHints() == null) {
            Log.d("Mat","restrict");
            var.eq(cell.getValue()).post();
        }
        /*else {
            boolean[] hints = cell.getHints();
            for (int i = 0; i < 9; i++) {
                if (!hints[i])
                    var.ne(i).post();
            }
        }*/
    }

    private DigitCell[][] gridFromVars(final IntVar[][] vars) {
        DigitCell[][] res = new DigitCell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (vars[i][j].isInstantiated())
                    res[i][j] = new DigitCell(false, vars[i][j].getValue());
                else {
                    boolean[] hints = new boolean[9];
                    DisposableValueIterator iterator = vars[i][j].getValueIterator(true);
                    while(iterator.hasNext()){
                        hints[iterator.next()] = true;
                    }
                    res[i][j] = new DigitCell(hints);
                }
            }
        }
        return res;
    }
}
