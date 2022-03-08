package com.mvavrill.logicGamesSolver.model.cells;

public class DoubleIntCell implements Cell {
    private final Integer hint1;
    private final Integer hint2;

    public DoubleIntCell(Integer hint1, Integer hint2) {
        this.hint1 = hint1;
        this.hint2 = hint2;
    }

    public DoubleIntCell() {
        this(-1,-1);
    }

    public DoubleIntCell copy() {
        return new DoubleIntCell(hint1,hint2);
    }

    public Integer getHint1() {
        return hint1;
    }
    public Integer getHint2() {
        return hint2;
    }

    public Cell removeFirst() {
        return hint2 == null ? new EmptyCell() : new DoubleIntCell(null, hint2);
    }
    public Cell removeSecond() {
        return hint1 == null ? new EmptyCell() : new DoubleIntCell(hint1, null);
    }

    public Cell addFirst(final int i) {
        return new DoubleIntCell(i, hint2);
    }
    public Cell addSecond(final int i) {
        return new DoubleIntCell(hint1, i);
    }

    @Override
    public String toString() {
        return "("+(hint1 != null ? hint1 : "-") + "," + (hint2 != null ? hint2 : "-") + ")";
    }
}
