package com.mvavrill.logicGamesSolver.model.games.rikudo;

import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RikudoSolver {

    private final RikudoGrid<Integer> rikudoGrid;

    public RikudoSolver(final RikudoGrid<Integer> rikudoGrid) {
        this.rikudoGrid = rikudoGrid;
    }

    public List<List<DigitCell>> extractInformation() {
        Pair<Model,IntVar[][]> modelAndVars = makeModel();
        Solver solver = modelAndVars.getValue0().getSolver();
        IntVar[][] vars = modelAndVars.getValue1();
        try {
            solver.propagate();
        }
        catch (Exception e) {
            return null;
        }
        List<List<DigitCell>> propagatedGrid = gridFromVars(vars);
        if (!solver.solve())
            return null;
        List<List<DigitCell>> solvedGrid = gridFromVars(vars);
        if (solver.solve())
            return propagatedGrid;
        else
            return solvedGrid;
    }

    private Pair<Model, IntVar[][]> makeModel() {
        List<List<Integer>> initialValues = rikudoGrid.getGrid();
        List<Quartet<Integer,Integer,Integer,Integer>> fixedEdges = rikudoGrid.getFixedEdges();
        int n = initialValues.get(0).size();
        Model model = new Model("Test");
        int nbVars = 3*n*(n-1);
        // Variables
        IntVar[][] vars = new IntVar[2*n-1][];
        for (int i = 0; i < 2*n-1; i++) {
            vars[i] = model.intVarArray("X-"+i, 2*n-1-Math.abs(i-n+1), 1, nbVars);
        }
        vars[n-1][n-1].eq(1); // Necessary to have a single solution, preferably it would be null
        // Creation of edges
        List<List<List<BoolVar>> > inEdges = Arrays.stream(vars).map(line -> Arrays.stream(vars).map(v -> (List<BoolVar>) new ArrayList<BoolVar>()).collect(Collectors.toList())).collect(Collectors.toList());
        List<List<List<BoolVar>> > outEdges = Arrays.stream(vars).map(line -> Arrays.stream(vars).map(v -> (List<BoolVar>) new ArrayList<BoolVar>()).collect(Collectors.toList())).collect(Collectors.toList());
        int sinkCpt = 0;
        BoolVar[] outSink = new BoolVar[nbVars];
        BoolVar[] inSink = new BoolVar[nbVars];
        for (int i = 0; i < 2*n-1; i++) {
            int lowerOffset = i >= n-1 ? -1 : 0;
            for (int j = 0; j < vars[i].length; j++) {
                if (isNotCenter(i,j,n)) {
                    if (initialValues.get(i).get(j) != 0) {
                        vars[i][j].eq(initialValues.get(i).get(j)).post();
                    }
                    outSink[sinkCpt] = model.boolVar("So-" + i + "-" + j);
                    inEdges.get(i).get(j).add(outSink[sinkCpt]);
                    inSink[sinkCpt] = model.boolVar("Si-" + i + "-" + j);
                    outEdges.get(i).get(j).add(inSink[sinkCpt]);
                    if (initialValues.get(i).get(j) == 1) {
                        outSink[sinkCpt].eq(1).post();
                    }
                    if (initialValues.get(i).get(j) == nbVars) {
                        inSink[sinkCpt].eq(1).post();
                    }
                    sinkCpt++;
                    if (j < vars[i].length-1 && isNotCenter(i, j+1,n)) {
                        createAddEdge(i, j, i, j+1, fixedEdges.contains(new Quartet<>(i,j,i,j+1)), vars, inEdges, outEdges, model, "O-", "E-");
                    }
                    if (i+1 < vars.length && j+lowerOffset+1 < vars[i+1].length && isNotCenter(i+1, j+lowerOffset+1,n)) {
                        createAddEdge(i, j, i+1, j+lowerOffset+1, fixedEdges.contains(new Quartet<>(i,j,i+1,j+lowerOffset+1)), vars, inEdges, outEdges, model, "SE-", "NO-");
                    }
                    if (i+1 < vars.length && 0 <= j+lowerOffset && isNotCenter(i+1, j+lowerOffset,n)) {
                        createAddEdge(i, j, i+1, j+lowerOffset, fixedEdges.contains(new Quartet<>(i,j,i+1,j+lowerOffset)), vars, inEdges, outEdges, model, "SO-", "NE-");
                    }
                }
            }
        }
        model.sum(inSink, "=", 1).post();
        model.sum(outSink, "=", 1).post();
        // Constraints on edges
        for (int i = 0; i < 2*n-1; i++) {
            for (int j = 0; j < vars[i].length; j++) {
                if (isNotCenter(i,j,n)) {
                    model.sum(listToArray(inEdges.get(i).get(j)), "=", 1).post();
                    model.sum(listToArray(outEdges.get(i).get(j)), "=", 1).post();
                }
            }
        }
        // Add AllDifferent constraint
        int varCpt = 0;
        IntVar[] flattendVars = new IntVar[nbVars];
        for (int i = 0; i < vars.length; i++) {
            for (int j = 0; j < vars[i].length; j++) {
                if (isNotCenter(i,j,n)) {
                    flattendVars[varCpt++] = vars[i][j];
                }
            }
        }
        model.allDifferent(flattendVars).post();

        return new Pair<>(model, vars);
    }

    private BoolVar[] listToArray(final List<BoolVar> list) {
        return list.toArray(new BoolVar[0]);
    }

    private boolean isNotCenter(final int i, final int j, final int n) {
        return i != n-1 || j != n-1;
    }

    private void createAddEdge(final int startI, final int startJ, final int endI, final int endJ, final boolean isFixed, final IntVar[][] vars, final List<List<List<BoolVar>> > inEdges, final List<List<List<BoolVar>> > outEdges, final Model model, final String outName, final String inName) {
        BoolVar leftOut = model.boolVar(outName+startI+"-"+startJ);
        outEdges.get(startI).get(startJ).add(leftOut);
        inEdges.get(endI).get(endJ).add(leftOut);
        model.reifyXeqYC(vars[startI][startJ], vars[endI][endJ], -1, leftOut); // leftOut <==> X+1 = Y
        BoolVar leftIn = model.boolVar(inName+endI+"-"+endJ);
        inEdges.get(startI).get(startJ).add(leftIn);
        outEdges.get(endI).get(endJ).add(leftIn);
        model.reifyXeqYC(vars[startI][startJ], vars[endI][endJ], 1, leftIn); // leftIn <==> X = Y+1
        model.sum(new IntVar[]{leftOut,leftIn}, isFixed ? "=" : "<=", 1).post(); // Optional, to ensure that there is not the edge on both ways for faster propagation
    }

    private List<List<DigitCell>> gridFromVars(final IntVar[][] vars) {
        List<List<DigitCell>> res = new ArrayList<>();
        for (int i = 0; i < vars.length; i++) {
            List<DigitCell> resLine = new ArrayList<>();
            for (int j = 0; j < vars[i].length; j++) {
                if (vars[i][j].isInstantiated()) {
                    resLine.add(new DigitCell(rikudoGrid.getGrid().get(i).get(j) != 0, vars[i][j].getValue()));
                }
                else {
                    resLine.add(new DigitCell(false, 0, null));
                }
            }
            res.add(resLine);
        }
        return res;
    }
}
