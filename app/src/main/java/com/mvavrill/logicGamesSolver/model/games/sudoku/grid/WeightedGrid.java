package com.mvavrill.logicGamesSolver.model.games.sudoku.grid;


import com.mvavrill.logicGamesSolver.model.games.sudoku.DigitProbabilities;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeightedGrid implements SudokuGrid {

    private final DigitProbabilities[][] grid;
    private final static double certaintyBound = 0.9;

    public WeightedGrid(final DigitProbabilities[][] grid) {
        this.grid = grid;
    }

    @Override
    public int get(int i, int j) {
        if (grid[i][j] == null || !grid[i][j].isConstant(certaintyBound))
            return 0;
        return grid[i][j].getBest();
    }

    @Override
    public void set(int i, int j, int v) {
        grid[i][j] = DigitProbabilities.constant(v);
    }

    @Override
    public int[][] solve() {
        Model model = new Model("Sudoku");
        IntVar[][] vars = model.intVarMatrix(9,9,1,9);
        List<IntVar> objectiveVars = new ArrayList<IntVar>();
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++) {
                objectiveVars.add(constraintCell(model, vars[i][j], grid[i][j]));
            }
        IntVar objective = model.intVar(0, 8100);
        model.sum(objectiveVars.toArray(new IntVar[0]), "=", objective);
        for (int line = 0; line < 9; line++)
            model.allDifferent(vars[line]).post();
        for (int column = 0; column < 9; column++)
            model.allDifferent(ArrayUtils.getColumn(vars, column)).post();
        for (int I = 0; I < 3; I++)
            for (int J = 0; J < 3; J++) {
                model.allDifferent(vars[3*I][3*J], vars[3*I+1][3*J], vars[3*I+2][3*J],vars[3*I][3*J+1], vars[3*I+1][3*J+1], vars[3*I+2][3*J+1],vars[3*I][3*J+2], vars[3*I+1][3*J+2], vars[3*I+2][3*J+2]).post();
            }
        Solution solution = model.getSolver().findOptimalSolution(objective, true);
        int[][] solArray = new int[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                solArray[i][j] = solution.getIntVal(vars[i][j]);
        return solArray;
    }

    private IntVar constraintCell(final Model model, final IntVar v, final DigitProbabilities d) {
        if (d == null)
            return null;
        else if (d.isConstant()) {
            v.eq(d.getBest());
            return null;
        }
        else {
            IntVar objAdd = model.intVar(0,100);
            Tuples costAssociation = new Tuples();
            for (int i = 1; i <= 9; i++)
                costAssociation.add(i, (int) Math.round(100*d.getProba(i)));
            model.table(new IntVar[]{v, objAdd}, costAssociation);
            return objAdd;
        }
    }

    @Override
    public String toString() {
        String res = "";
        for (DigitProbabilities[] dp : grid) {
            res += Arrays.toString(dp) + "\n";
        }
        return res;
    }
}
