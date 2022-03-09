package com.mvavrill.logicGamesSolver.model.games.slitherlink;

import com.mvavrill.logicGamesSolver.model.cells.Cell;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.javatuples.Triplet;

public class SlitherlinkSolver {

    private int[][] numbers; // -1 for don't know, 0-3 for value
    private int[][] verticalEdges; // -1 for no edge, 1 for edge, 0 for don't know
    private int[][] horizontalEdges;

    public SlitherlinkSolver(final int[][] numbers, final int[][] verticalEdges, final int[][] horizontalEdges) {
        this.numbers = numbers;
        this.verticalEdges = verticalEdges;
        this.horizontalEdges = horizontalEdges;
    }

    public Triplet<int[][],int[][],int[][]> extractInformation() {
        Model model = new Model("Slitherlink");
        BoolVar[][] vEdges = model.boolVarMatrix(verticalEdges.length, verticalEdges[0].length);
        BoolVar[][] hEdges = model.boolVarMatrix(verticalEdges.length, verticalEdges[0].length);
        // Vertex type = · | -- _| |_ |^ ^|
        IntVar[][] vertexType = model.intVarMatrix(numbers.length+1, numbers[0].length+1, 0, 6);
        postCellsSums(model, vEdges, hEdges);
        postVertexTypes(model, vEdges, hEdges, vertexType);
        Solver solver = model.getSolver();
        try {
            solver.propagate();
        } catch (Exception e) {
            return null;
        }
        Triplet<int[][],int[][],int[][]>  propagatedGrid = gridFromVars(vEdges, hEdges);
        if (!solver.solve())
            return null;
        Triplet<int[][],int[][],int[][]>  solvedGrid = gridFromVars(vEdges, hEdges);
        if (solver.solve())
            return propagatedGrid;
        else
            return solvedGrid;
    }

    private void postCellsSums(final Model model, final BoolVar[][] vEdges, final BoolVar[][] hEdges) {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                if (numbers[i][j] != -1) {
                    model.sum(new IntVar[]{vEdges[j][i],vEdges[j+1][i],hEdges[i][j],hEdges[i+1][j]},"=",numbers[i][j]).post();
                }
            }
        }
    }

    private void postVertexTypes(final Model model, final BoolVar[][] vEdges, final BoolVar[][] hEdges, final IntVar[][] vertexType) {
        // 4 corners
        model.table(new IntVar[]{vertexType[0][0], vEdges[0][0], hEdges[0][0]}, topLeftType).post();
        model.table(new IntVar[]{vertexType[0][vertexType[0].length-1], vEdges[vEdges.length-1][0], hEdges[0][hEdges[0].length-1]}, topRightType).post();
        model.table(new IntVar[]{vertexType[vertexType.length-1][0], vEdges[0][vEdges[0].length-1], hEdges[hEdges.length-1][0]}, bottomLeftType).post();
        model.table(new IntVar[]{vertexType[vertexType.length-1][vertexType[0].length-1], vEdges[vEdges.length-1][vEdges[0].length-1], hEdges[hEdges.length-1][hEdges[0].length-1]}, topLeftType).post();
        for (int i = 1; i < vertexType.length-1; i++) {
            model.table(new IntVar[]{vertexType[i][0], vEdges[0][i-1], hEdges[i][0], vEdges[0][i]}, leftType).post();
            model.table(new IntVar[]{vertexType[i][vertexType[0].length-1], vEdges[vEdges.length-1][i-1], hEdges[i][hEdges[0].length-1], vEdges[vEdges.length-1][i]}, rightType).post();
        }
        for (int j = 1; j < vertexType[0].length-1; j++) {
            model.table(new IntVar[]{vertexType[0][j], hEdges[0][j-1], vEdges[j][0], hEdges[0][j]}, topType).post();
            model.table(new IntVar[]{vertexType[vertexType.length-1][j], hEdges[hEdges.length-1][j-1], vEdges[j][vEdges[0].length-1], hEdges[hEdges.length-1][j]}, bottomType).post();
        }
        for (int i = 1; i < vertexType.length-1; i++) {
            for (int j = 1; j < vertexType[i].length-1; j++) {
                model.table(new IntVar[]{vertexType[i][j], hEdges[i][j-1], vEdges[j][i-1], hEdges[i][j], vEdges[j][i]}, middleType).post();
            }
        }
    }

    private Triplet<int[][],int[][],int[][]> gridFromVars(final IntVar[][] vEdges, final IntVar[][] hEdges) {
        int[][] vInt = new int[vEdges.length][vEdges[0].length];
        for (int i = 0; i < vEdges.length; i++) {
            for (int j = 0; j < vEdges[0].length; j++) {
                if (!vEdges[i][j].isInstantiated())
                    vInt[i][j] = 0;
                else if (vEdges[i][j].isInstantiatedTo(0))
                    vInt[i][j] = -1;
                else
                    vInt[i][j] = 1;
            }
        }
        int[][] hInt = new int[hEdges.length][hEdges[0].length];
        for (int i = 0; i < hEdges.length; i++) {
            for (int j = 0; j < hEdges[0].length; j++) {
                if (!hEdges[i][j].isInstantiated())
                    hInt[i][j] = 0;
                else if (hEdges[i][j].isInstantiatedTo(0))
                    hInt[i][j] = -1;
                else
                    hInt[i][j] = 1;
            }
        }
        return new Triplet<int[][],int[][],int[][]>(numbers, vInt, hInt);
    }

    // Tuples definition
    private final static Tuples bottomRightType = new Tuples(new int[][]{new int[]{0,0,0}, new int[]{3,1,1}}, true);
    private final static Tuples bottomLeftType = new Tuples(new int[][]{new int[]{0,0,0}, new int[]{4,1,1}}, true);
    private final static Tuples topLeftType = new Tuples(new int[][]{new int[]{0,0,0}, new int[]{5,1,1}}, true);
    private final static Tuples topRightType = new Tuples(new int[][]{new int[]{0,0,0}, new int[]{6,1,1}}, true);
    private final static Tuples topType = new Tuples(new int[][]{ // Left Down Right
            new int[]{0,0,0,0},
            new int[]{2,1,0,1},
            new int[]{5,0,1,1},
            new int[]{6,1,1,0}}, true);
    private final static Tuples bottomType = new Tuples(new int[][]{ // Left Up Right
            new int[]{0,0,0,0},
            new int[]{2,1,0,1},
            new int[]{3,1,1,0},
            new int[]{4,0,1,1}}, true);
    private final static Tuples leftType = new Tuples(new int[][]{ // Up Right Down
            new int[]{0,0,0,0},
            new int[]{1,1,0,1},
            new int[]{4,1,1,0},
            new int[]{5,0,1,1}}, true);
    private final static Tuples rightType = new Tuples(new int[][]{ // Up Left Down
            new int[]{0,0,0,0},
            new int[]{1,1,0,1},
            new int[]{3,1,1,0},
            new int[]{6,0,1,1}}, true);
    private final static Tuples middleType = new Tuples(new int[][]{ // Left Up Right Down
            new int[]{0,0,0,0,0},
            new int[]{1,0,1,0,1},
            new int[]{2,1,0,1,0},
            new int[]{3,1,1,0,0},
            new int[]{4,0,1,1,0},
            new int[]{5,0,0,1,1},
            new int[]{6,1,0,0,1}}, true);
    // Vertex type = · | -- _| |_ |^ ^|
}
