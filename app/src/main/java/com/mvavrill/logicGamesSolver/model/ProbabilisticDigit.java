package com.mvavrill.logicGamesSolver.model;

import java.io.Serializable;

public class ProbabilisticDigit implements Serializable {

    private final double proportionToKeep = 0.4;
    private final int[] seenDigits = new int[10];

    public void addDigit(final int d) {
        seenDigits[d]++;
    }

    public int extractDigit(final int totalNbTested) {
        int bestDigit = -1;
        int mostDigit = 0;
        for (int i = 0; i < 10; i++) {
            if (seenDigits[i] > mostDigit && totalNbTested*proportionToKeep < seenDigits[i]) {
                bestDigit = i;
                mostDigit = seenDigits[i];
            }
        }
        return bestDigit;
    }
}
