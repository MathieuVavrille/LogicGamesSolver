package com.mvavrill.logicGamesSolver.model.games.kakuro;

import android.util.Log;

import com.mvavrill.logicGamesSolver.model.cells.Cell;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.cells.DoubleIntCell;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.iterators.DisposableValueIterator;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class KakuroSolver {

    private final Cell[][] grid;
    private final static Map<Pair<Integer,Integer>,Tuples> memoizedTuples = new HashMap<Pair<Integer,Integer>,Tuples>();

    public KakuroSolver(final Cell[][] grid) {
        this.grid = grid;
    }

    public Cell[][] extractInformation() {
        Model model = new Model("sudoku");
        IntVar[][] vars = new IntVar[grid.length][grid.length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid.length; j++) {
                if (grid[i][j] instanceof DigitCell) {
                    //DigitCell gv = (DigitCell) grid[i][j];
                    vars[i][j] = model.intVar("V(" + i + "," + j + ")", 1, 9);
                    /*if (gv.isFixed())
                        vars[i][j].eq(gv.getValue()).post();*/
                }
            }
        }
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] instanceof DoubleIntCell) {
                    DoubleIntCell cell = (DoubleIntCell) grid[i][j];
                    if (cell.getHint1() != null) {
                        if (cell.getHint1() == -1)
                            model.allDifferent(getHorizontalVars(vars, i, j + 1)).post();
                        else
                            postAlldiffSumConstraint(model, getVerticalVars(vars, i, j+1), cell.getHint1());
                    }
                    if (cell.getHint2() != null) {
                        if (cell.getHint2() == -1)
                            model.allDifferent(getHorizontalVars(vars, i+1, j)).post();
                        else
                            postAlldiffSumConstraint(model, getVerticalVars(vars, i+1, j), cell.getHint2());
                    }
                }
            }
        }
        Solver solver = model.getSolver();
        try {
            solver.propagate();
        } catch (Exception e) {
            return null;
        }
        Cell[][] propagatedGrid = gridFromVars(vars);
        if (!solver.solve())
            return null;
        Cell[][] solvedGrid = gridFromVars(vars);
        if (solver.solve())
            return propagatedGrid;
        else
            return solvedGrid;
    }

    private IntVar[] getHorizontalVars(IntVar[][] vars, int i, int j) {
        List<IntVar> line = new ArrayList<IntVar>();
        while (j < grid[i].length && grid[i][j] instanceof DigitCell) {
            line.add(vars[i][j]);
            j++;
        }
        return line.toArray(new IntVar[0]);
    }

    private IntVar[] getVerticalVars(IntVar[][] vars, int i, int j) {
        List<IntVar> line = new ArrayList<IntVar>();
        while (i < grid.length && grid[i][j] instanceof DigitCell) {
            line.add(vars[i][j]);
            i++;
        }
        return line.toArray(new IntVar[0]);
    }

    /*private void postAlldiffSumConstraint(final Model model, final IntVar[] vars, final int sum) {
        model.allDifferent(vars).post();
        model.sum(vars,"=",sum).post();
    }*/

    private void postAlldiffSumConstraint(final Model model, final IntVar[] vars, final int sum) {
        Pair<Integer,Integer> currentPair = new Pair<Integer,Integer>(vars.length, sum);
        if (!memoizedTuples.containsKey(currentPair)) {
            Tuples allowedTuples = new Tuples(true);
            fillTuples(allowedTuples, 0, new int[vars.length], sum);
            memoizedTuples.put(currentPair, allowedTuples);
        }
        model.table(vars, memoizedTuples.get(currentPair)).post();
    }

    private final static int[] ubs = new int[]{-1, 9, 17, 24, 30, 35, 39, 42, 44, 45};
    private final static int[] lbs = new int[]{-1, 1, 3, 6, 10, 15, 21, 28, 36, 45};
    private void fillTuples(final Tuples allowedTuples, final int index, final int[] currentInstantiation, final int remainingSum) {
        int remainingSize = currentInstantiation.length-index;
        if (remainingSum < lbs[remainingSize] || remainingSum > ubs[remainingSize])
            return;
        if (index == currentInstantiation.length-1) {
            currentInstantiation[index] = remainingSum;
            if (allDifferentArray(currentInstantiation))
                allowedTuples.add(currentInstantiation);
        } else {
            for (int v = 1; v < 10; v++) {
                currentInstantiation[index] = v;
                fillTuples(allowedTuples, index + 1, currentInstantiation, remainingSum-v);
            }
        }
    }

    private boolean allDifferentArray(final int[] a) {
        Set<Integer> distinct = new HashSet<Integer>();
        for (int i: a)
            distinct.add(i);
        return distinct.size() == a.length;
    }

    private Cell[][] gridFromVars(final IntVar[][] vars) {
        Cell[][] res = new Cell[grid.length][grid.length];
        for (int i = 0; i < res.length; i++) {
            for (int j = 0; j < res.length; j++) {
                if (grid[i][j] instanceof DigitCell) {
                    if (vars[i][j].isInstantiated())
                        res[i][j] = new DigitCell(false, vars[i][j].getValue());
                    else {
                        boolean[] hints = new boolean[10];
                        DisposableValueIterator iterator = vars[i][j].getValueIterator(true);
                        while (iterator.hasNext()) {
                            hints[iterator.next()] = true;
                        }
                        res[i][j] = new DigitCell(hints);
                    }
                } else
                    res[i][j] = grid[i][j];
            }
        }
        return res;
    }
}
