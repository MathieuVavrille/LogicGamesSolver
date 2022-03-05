package com.mvavrill.logicGamesSolver.model.cells;

public class DoubleIntCell implements Cell {
    private final int hint1;
    private final int hint2;

    public DoubleIntCell(int hint1, int hint2) {
        this.hint1 = hint1;
        this.hint2 = hint2;
    }

    public DoubleIntCell() {
        this(-1,-1);
    }

    public DoubleIntCell copy() {
        return new DoubleIntCell(hint1,hint2);
    }

    public int getHint1() {
        return hint1;
    }

    public int getHint2() {
        return hint2;
    }
}
