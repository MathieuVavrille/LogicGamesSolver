package com.mvavrill.logicGamesSolver.model.games.slitherlink;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.extension.Tuples;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.UndirectedGraphVar;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.javatuples.Triplet;

public class SlitherlinkSolver {

    private int[][] numbers; // -1 for don't know, 0-3 for value

    public SlitherlinkSolver(final int[][] numbers) {
        this.numbers = numbers;
    }

    public Triplet<int[][],int[][],boolean[][]> extractInformation() {
        Model model = new Model("Slitherlink");
        UndirectedGraphVar graph = initializeGraph(model);
        model.connected(graph).post();
        BoolVar[][] vEdges = getVerticalEdges(model, graph);
        BoolVar[][] hEdges = getHorizontalEdges(model, graph);
        // Vertex type = · | -- _| |_ |^ ^|
        IntVar[][] vertexType = model.intVarMatrix(numbers.length+1, numbers[0].length+1, 0, 6);
        postAdjacentThrees(vEdges, hEdges);
        postCellsVerticesTypes(model, vertexType);
        postVertexTypes(model, vEdges, hEdges, vertexType);
        BoolVar[][] isInside = model.boolVarMatrix("isinside", numbers.length, numbers[0].length);
        postInsideConstraints(model, isInside, vEdges, hEdges);
        Solver solver = model.getSolver();
        try {
            solver.propagate();
        } catch (Exception e) {
            return null;
        }
        Triplet<int[][],int[][],boolean[][]>  propagatedGrid = gridFromVars(vEdges, hEdges, isInside);
        if (!solver.solve())
            return null;
        Triplet<int[][],int[][],boolean[][]>  solvedGrid = gridFromVars(vEdges, hEdges, isInside);
        if (solver.solve())
            return propagatedGrid;
        else
            return solvedGrid;
    }

    private void postInsideConstraints(final Model model, final BoolVar[][] isInside, final BoolVar[][] vEdges, final BoolVar[][] hEdges) {
        for (int i = 0; i < numbers.length; i++) {
            isInside[i][0].eq(vEdges[0][i]).post();
            isInside[i][isInside[i].length-1].eq(vEdges[vEdges.length-1][i]).post();
        }
        for (int j = 0; j < numbers[0].length; j++) {
            isInside[0][j].eq(hEdges[0][j]).post();
            isInside[isInside.length-1][j].eq(hEdges[hEdges.length-1][j]).post();
        }
        for (int i = 0; i < numbers.length-1; i++) {
            for (int j = 0; j < numbers[0].length-1; j++) {
                model.table(new IntVar[]{isInside[i][j], vEdges[j+1][i], isInside[i][j+1]}, xor3).post();
                model.table(new IntVar[]{isInside[i][j], hEdges[i+1][j], isInside[i+1][j]}, xor3).post();
            }
        }
    }

    private int nodeOfPair(final int i, final int j) {
        return i*(numbers[0].length+1)+j;
    }

    private UndirectedGraphVar initializeGraph(final Model model) {
        UndirectedGraph gub = new UndirectedGraph(model, (numbers.length+1)*(numbers[0].length+1), SetType.BITSET, false);
        for (int i = 0; i < numbers.length+1; i++) {
            for (int j = 0; j < numbers[0].length+1; j++) {
                gub.addNode(nodeOfPair(i,j));
            }
        }
        for (int i = 0; i < numbers.length+1; i++) {
            for (int j = 0; j < numbers[0].length+1; j++) {
                if (i+1 < numbers.length+1)
                    gub.addEdge(nodeOfPair(i,j), nodeOfPair(i+1,j));
                if (j+1 < numbers[0].length+1)
                    gub.addEdge(nodeOfPair(i,j), nodeOfPair(i,j+1));
            }
        }
        return model.graphVar("mainGraph", new UndirectedGraph(model, (numbers.length+1)*(numbers[0].length+1), SetType.BITSET, false), gub);
    }

    private BoolVar[][] getVerticalEdges(final Model model, final UndirectedGraphVar graph) {
        BoolVar[][] vEdges = model.boolVarMatrix("vEdges", numbers[0].length+1, numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[0].length+1; j++) {
                model.edgeChanneling(graph, vEdges[j][i], nodeOfPair(i,j), nodeOfPair(i+1,j)).post();
            }
        }
        return vEdges;
    }

    private BoolVar[][] getHorizontalEdges(final Model model, final UndirectedGraphVar graph) {
        BoolVar[][] hEdges = model.boolVarMatrix("hEdges", numbers.length+1, numbers[0].length);
        for (int i = 0; i < numbers.length+1; i++) {
            for (int j = 0; j < numbers[0].length; j++) {
                model.edgeChanneling(graph, hEdges[i][j], nodeOfPair(i,j), nodeOfPair(i,j+1)).post();
            }
        }
        return hEdges;
    }

    private void postCellsVerticesTypes(final Model model, final IntVar[][] vertexType) {
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                if (numbers[i][j] != -1) {
                    model.table(new IntVar[]{vertexType[i][j],vertexType[i][j+1],vertexType[i+1][j+1],vertexType[i+1][j]}, new Tuples[]{corners0, corners1, corners2, corners3}[numbers[i][j]]).post();
                }
            }
        }
    }

    private void postAdjacentThrees(final BoolVar[][] vEdges, final BoolVar[][] hEdges) {
        for (int i = 0; i < numbers.length-1; i++) {
            for (int j = 0; j < numbers[i].length-1; j++) {
                if (numbers[i][j] == 3 && numbers[i][j+1] == 3) {
                    vEdges[j][i].eq(1).post();
                    vEdges[j+1][i].eq(1).post();
                    if (i > 0)
                        vEdges[j+1][i-1].eq(0).post();
                    if (i+1 < vEdges[j+1].length)
                    vEdges[j+1][i+1].eq(0).post();
                    vEdges[j+2][i].eq(1).post();
                }
                if (numbers[i][j] == 3 && numbers[i+1][j] == 3) {
                    hEdges[i][j].eq(1).post();
                    hEdges[i+1][j].eq(1).post();
                    if (j > 0)
                        hEdges[i+1][j-1].eq(0).post();
                    if (j+1 < hEdges[i+1].length)
                        hEdges[i+1][j+1].eq(0).post();
                    hEdges[i+2][j].eq(1).post();
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

    private Triplet<int[][],int[][],boolean[][]> gridFromVars(final IntVar[][] vEdges, final IntVar[][] hEdges, final BoolVar[][] isInside) {
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
        boolean[][] inside = new boolean[numbers.length][numbers[0].length];
        for (int i = 0; i < inside.length; i++) {
            for (int j = 0; j < inside[0].length; j++) {
                if (isInside[i][j].isInstantiatedTo(1))
                    inside[i][j] = true;
            }
        }
        return new Triplet<int[][],int[][],boolean[][]>(vInt, hInt, inside);
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
    // For corners, the order is topLeft, topRight, bottomRight, bottomLeft
    private final static Tuples corners0 = new Tuples();
    static {
        for (int tl : new int[]{0,3}) {
            for (int tr : new int[]{0,4}) {
                for (int br : new int[]{0,5}) {
                    for (int bl : new int[]{0,6}) {
                        corners0.add(tl,tr,br,bl);}}}}
    }
    // Vertex type = · | -- _| |_ |^ ^|
    //               0 1 2  3  4  5  6
    private final static Tuples corners1 = new Tuples();
    static {
        // top edge
        for (int tl : new int[]{2,4}) {
            for (int tr : new int[]{2,3}) {
                for (int br : new int[]{0,5}) {
                    for (int bl : new int[]{0,6}) {
                        corners1.add(tl,tr,br,bl);}}}}
        // right edge
        for (int tl : new int[]{0,3}) {
            for (int tr : new int[]{1,5}) {
                for (int br : new int[]{1,4}) {
                    for (int bl : new int[]{0,6}) {
                        corners1.add(tl,tr,br,bl);}}}}
        // bottom edge
        for (int tl : new int[]{0,3}) {
            for (int tr : new int[]{0,4}) {
                for (int br : new int[]{2,6}) {
                    for (int bl : new int[]{2,5}) {
                        corners1.add(tl,tr,br,bl);}}}}
        // left edge
        for (int tl : new int[]{1,6}) {
            for (int tr : new int[]{0,4}) {
                for (int br : new int[]{0,5}) {
                    for (int bl : new int[]{1,3}) {
                        corners1.add(tl,tr,br,bl);}}}}
    }
    private final static Tuples corners2 = new Tuples();
    static {
        // =
        for (int tl : new int[]{2,4}) {
            for (int tr : new int[]{2,3}) {
                for (int br : new int[]{2,6}) {
                    for (int bl : new int[]{2,5}) {
                        corners2.add(tl,tr,br,bl);}}}}
        // ||
        for (int tl : new int[]{1,6}) {
            for (int tr : new int[]{1,5}) {
                for (int br : new int[]{1,4}) {
                    for (int bl : new int[]{1,3}) {
                        corners2.add(tl,tr,br,bl);}}}}
        // 3 _|
        for (int tl : new int[]{0,3}) {
            for (int tr : new int[]{1,5}) {
                for (int bl : new int[]{2,5}) {
                    corners2.add(tl,tr,3,bl);}}}
        // 4 |_
        for (int tl : new int[]{1,6}) {
            for (int tr : new int[]{0,4}) {
                for (int br : new int[]{2,6}) {
                        corners2.add(tl,tr,br,4);}}}
        // 5 |^
        for (int tr : new int[]{2,3}) {
            for (int br : new int[]{0,5}) {
                for (int bl : new int[]{1,3}) {
                    corners2.add(5,tr,br,bl);}}}
        // 6 ^|
        for (int tl : new int[]{2,4}) {
                for (int br : new int[]{1,4}) {
                    for (int bl : new int[]{0,6}) {
                        corners2.add(tl,6,br,bl);}}}
    }
    private final static Tuples corners3 = new Tuples(new int[][]{
            new int[]{1,1,3,4},
            new int[]{6,1,3,4},
            new int[]{1,5,3,4},
            new int[]{6,5,3,4},
            new int[]{5,2,2,4},
            new int[]{5,3,2,4},
            new int[]{5,2,6,4},
            new int[]{5,3,6,4},
            new int[]{5,6,1,1},
            new int[]{5,6,4,1},
            new int[]{5,6,1,3},
            new int[]{5,6,4,3},
            new int[]{2,6,3,2},
            new int[]{4,6,3,2},
            new int[]{2,6,3,5},
            new int[]{4,6,3,5}},true);
    private final static Tuples xor3 = new Tuples(new int[][]{
            new int[]{0,0,0},
            new int[]{0,1,1},
            new int[]{1,0,1},
            new int[]{1,1,0}},true);
}
