package com.mvavrill.logicGamesSolver.model.cells;

import java.util.Arrays;

public class DigitCell implements Cell {

    private boolean isFixed;
    private final int value;
    private final boolean[] hints;

    public DigitCell() {
        this(false, -1, new boolean[]{true,true,true,true,true,true,true,true,true});
    }

    public DigitCell(final boolean isFixed, final int v) {
        this(isFixed, v, null);
    }

    public DigitCell(final boolean[] hints) {
        this(false, -1, hints);
    }

    public DigitCell(final boolean isFixed, final int value, final boolean[] hints) {
        this.isFixed = isFixed;
        this.value = value;
        this.hints = hints;
    }

    public DigitCell(final DigitCell d) {
        this.isFixed = d.isFixed;
        this.value = d.value;
        this.hints = d.hints == null ? null : Arrays.copyOf(d.hints, 9);
    }

    public DigitCell copy() {
        return new DigitCell(this);
    }

    public void fix(final boolean b) {isFixed = b;}

    public boolean isFixed() {
        return isFixed;
    }

    public int getValue() {
        return value;
    }

    public boolean[] getHints() {
        return hints;
    }

    @Override
    public String toString() {
        if (hints == null)
            return ""+value;
        return Arrays.toString(hints);
    }
}
