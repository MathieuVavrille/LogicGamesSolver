package com.mvavrill.logicGamesSolver.game.futoshiki;

import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.chocosolver.util.tools.ArrayUtils;
import org.javatuples.Pair;

public class FutoshikiSolver {

    private final FutoshikiGrid<Integer> fullGrid;

    public FutoshikiSolver(final FutoshikiGrid<Integer> fullGrid) {
        this.fullGrid = fullGrid;
    }

    public DigitCell[][] extractInformation() {
        Pair<Model, IntVar[][]> modelAndVars = makeModel();
        Solver solver = modelAndVars.getValue0().getSolver();
        IntVar[][] vars = modelAndVars.getValue1();
        try {
            solver.propagate();
        }
        catch (Exception e) {
            return null;
        }
        DigitCell[][] propagatedGrid = gridFromVars(vars);
        if (!solver.solve())
            return null;
        DigitCell[][] solvedGrid = gridFromVars(vars);
        if (solver.solve()) {
            return propagatedGrid;
        }
        else
            return solvedGrid;
    }

    private Pair<Model, IntVar[][]> makeModel() {
        Model model = new Model("Futoshiki");
        Integer[][] initialGrid = fullGrid.getGrid();
        int[][] lineIneq = fullGrid.getLineIneq();
        int[][] columnIneq = fullGrid.getColumnIneq();
        IntVar[][] vars = model.intVarMatrix("X", initialGrid.length, initialGrid.length, 1, initialGrid.length);
        // Variables
        for (int i = 0; i < initialGrid.length; i++) {
            model.allDifferent(vars[i]).post();
            model.allDifferent(ArrayUtils.getColumn(vars, i)).post();
            for (int j = 0; j < initialGrid.length; j++) {
                if (initialGrid[i][j] > 0) {
                    vars[i][j].eq(initialGrid[i][j]).post();
                }
            }
        }
        // Line inequalities
        for (int i = 0; i < initialGrid.length; i++) {
            for (int j = 0; j < initialGrid.length-1; j++) {
                if (lineIneq[i][j] == 1) {
                    vars[i][j].lt(vars[i][j+1]).post();
                }
                if (lineIneq[i][j] == -1) {
                    vars[i][j].gt(vars[i][j+1]).post();
                }
            }
        }
        // Column inequalities
        for (int i = 0; i < initialGrid.length-1; i++) {
            for (int j = 0; j < initialGrid.length; j++) {
                if (columnIneq[i][j] == 1) {
                    vars[i][j].lt(vars[i+1][j]).post();
                }
                if (columnIneq[i][j] == -1) {
                    vars[i][j].gt(vars[i+1][j]).post();
                }
            }
        }
        return new Pair<>(model, vars);
    }

    private DigitCell[][] gridFromVars(final IntVar[][] vars) {
        Integer[][] initialGrid = fullGrid.getGrid();
        DigitCell[][] res = new DigitCell[initialGrid.length][initialGrid.length];
        for (int i = 0; i < vars.length; i++) {
            for (int j = 0; j < vars[i].length; j++) {
                if (vars[i][j].isInstantiated()) {
                    res[i][j] = new DigitCell(initialGrid[i][j] != 0, vars[i][j].getValue());
                }
                else {
                    boolean[] hints = new boolean[initialGrid.length+1];
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
