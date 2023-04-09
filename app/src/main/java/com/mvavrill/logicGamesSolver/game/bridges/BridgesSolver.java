package com.mvavrill.logicGamesSolver.game.bridges;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.javatuples.Pair;
import org.javatuples.Quartet;

import java.util.HashSet;
import java.util.Set;

public class BridgesSolver {

    private final int[][] islands;

    public BridgesSolver(final int[][] islands) {
        this.islands = islands;
    }

    public Pair<int[][], int[][]> extractInformation() {
        Model model = new Model("Slitherlink");
        UndirectedGraphVar graph = initializeGraph(model);
        model.connected(graph).post();
        IntVar[][] horizontalEdgesVars = new IntVar[islands.length][islands.length];
        IntVar[][] verticalEdgesVars = new IntVar[islands.length][islands.length];
        ArExpression[][] edgesSum = new ArExpression[islands.length][islands.length];
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (islands[i][j] != 0)
                    edgesSum[i][j] = model.intVar(0);
            }
        }
        postConstraints(model, graph, horizontalEdgesVars, verticalEdgesVars, edgesSum);
        Solver solver = model.getSolver();
        try {
            solver.propagate();
        } catch (Exception e) {
            return null;
        }
        Pair<int[][],int[][]>  propagatedGrid = gridFromVars(horizontalEdgesVars, verticalEdgesVars);
        if (!solver.solve())
            return null;
        Pair<int[][],int[][]>  solvedGrid = gridFromVars(horizontalEdgesVars, verticalEdgesVars);
        if (solver.solve())
            return propagatedGrid;
        else
            return solvedGrid;
    }


    private UndirectedGraphVar initializeGraph(final Model model) {
        UndirectedGraph gub = new UndirectedGraph(model, islands.length*islands.length, SetType.BITSET, false);
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (islands[i][j] != 0)
                    gub.addNode(nodeOfPair(i, j));
            }
        }
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (islands[i][j] != 0) {
                    //Horizontal
                    for (int k = j+1; k < islands[i].length; k++) {
                        if (islands[i][k] != 0) {
                            gub.addEdge(nodeOfPair(i,j),nodeOfPair(i,k));
                            break;
                        }
                    }
                    //Vertical
                    for (int k = i+1; k < islands.length; k++) {
                        if (islands[k][j] != 0) {
                            gub.addEdge(nodeOfPair(i,j),nodeOfPair(k,j));
                            break;
                        }
                    }
                }
            }
        }
        return model.graphVar("mainGraph", new UndirectedGraph(model, islands.length*islands.length, SetType.BITSET, false), gub);
    }

    private int nodeOfPair(final int i, final int j) {
        return i*islands.length+j;
    }

    private void postConstraints(final Model model, final UndirectedGraphVar graph, final IntVar[][] horizontalEdges, final IntVar[][] verticalEdges, final ArExpression[][] edgesSum) {
        Set<Quartet<Integer,Integer,Integer,BoolVar>> horizontalVars = new HashSet<>();
        Set<Quartet<Integer,Integer,Integer,BoolVar>> verticalVars = new HashSet<>();
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (islands[i][j] != 0) {
                    //Horizontal
                    for (int k = j+1; k < islands[i].length; k++) {
                        if (islands[i][k] != 0) {
                            BoolVar hasEdge = model.boolVar();
                            model.edgeChanneling(graph, hasEdge, nodeOfPair(i,j),nodeOfPair(i,k)).post();
                            IntVar value = model.intVar(0,2);
                            hasEdge.eq(0).iff(value.eq(0)).post();
                            horizontalEdges[i][j] = value;
                            edgesSum[i][j] = edgesSum[i][j].add(value);
                            edgesSum[i][k] = edgesSum[i][k].add(value);
                            horizontalVars.add(new Quartet<>(i,j,k,hasEdge));
                            break;
                        }
                    }
                    //Vertical
                    for (int k = i+1; k < islands.length; k++) {
                        if (islands[k][j] != 0) {
                            BoolVar hasEdge = model.boolVar();
                            model.edgeChanneling(graph, hasEdge, nodeOfPair(i,j),nodeOfPair(k,j)).post();
                            IntVar value = model.intVar(0,2);
                            hasEdge.eq(0).iff(value.eq(0)).post();
                            verticalEdges[i][j] = value;
                            edgesSum[i][j] = edgesSum[i][j].add(value);
                            edgesSum[k][j] = edgesSum[k][j].add(value);
                            verticalVars.add(new Quartet<>(j,i,k,hasEdge));
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (islands[i][j] != 0)
                    edgesSum[i][j].eq(islands[i][j]).post();
            }
        }
        verticalVars.forEach(v -> horizontalVars.forEach(h -> postIntersectionConstraint(v, h)));
    }

    private void postIntersectionConstraint(final Quartet<Integer,Integer,Integer,BoolVar> v, final Quartet<Integer,Integer,Integer,BoolVar> h) {
        if (h.getValue1() < v.getValue0() && v.getValue0() < h.getValue2()
        && v.getValue1() < h.getValue0() && h.getValue0() < v.getValue2())
            h.getValue3().and(v.getValue3()).not().post();
    }

    private Pair<int[][],int[][]> gridFromVars(final IntVar[][] horizontalEdges, final IntVar[][] verticalEdges) {
        int[][] horizontalValues = new int[islands.length][islands.length];
        int[][] verticalValues = new int[islands.length][islands.length];
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (horizontalEdges[i][j] != null && horizontalEdges[i][j].isInstantiated())
                    horizontalValues[i][j] = horizontalEdges[i][j].getValue();
                if (verticalEdges[i][j] != null && verticalEdges[i][j].isInstantiated())
                    verticalValues[i][j] = verticalEdges[i][j].getValue();
                }
            }
        return new Pair<int[][],int[][]>(horizontalValues, verticalValues);
    }

}
