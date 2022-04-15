package com.mvavrill.logicGamesSolver.model.games.sudoku;

import org.javatuples.Pair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DigitProbabilities implements Serializable {

    private final List<Pair<Double,Integer>> digits;

    public DigitProbabilities(final List<Pair<Double,Integer>> digits) {
        this.digits = digits;
    }

    public static DigitProbabilities constant(final int v) {
        List<Pair<Double,Integer>> l = new ArrayList<Pair<Double,Integer>>();
        l.add(new Pair<Double,Integer>(1., v));
        return new DigitProbabilities(l);
    }

    public boolean isConstant() {
        return isConstant(0.9);
    }

    public boolean isConstant(final double certaintyBound) {
        return digits.get(0).getValue0() > certaintyBound;
    }

    public int getBest() {
        return digits.get(0).getValue1();
    }

    public double getProba(final int v) {
        for (Pair<Double,Integer> d : digits) {
            if (d.getValue1() == v)
                return d.getValue0();
        }
        return 0;
    }

    @Override
    public String toString() {
        return digits.toString();
    }
}
