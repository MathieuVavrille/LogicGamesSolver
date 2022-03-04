package com.mvavrill.logicGamesSolver.model.cells;

public class DoubleHintCell implements Cell {
    private final int hint1;
    private final int hint2;

    public DoubleHintCell(int hint1, int hint2) {
        this.hint1 = hint1;
        this.hint2 = hint2;
    }

    public int getHint1() {
        return hint1;
    }

    public int getHint2() {
        return hint2;
    }
}
