package com.mvavrill.logicGamesSolver.controller.popups.integer;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** This is a factory creating the desired popup to input numbers.
 * Depending on the
 */
public class PopupNumberFactory {

    private final Bundle callbackData;
    private final CallbackWithInteger callback;

    public PopupNumberFactory(final Bundle callbackData, final CallbackWithInteger callback) {
        this.callbackData = callbackData;
        this.callback = callback;
    }

    public DialogFragment valueRange(final int lb, final int ub) {
        int[] values = new int[ub-lb+1];
        for (int i = 0; i < values.length; i++) {
            values[i] = lb+i;
        }
        return valueArray(values);
    }

    public DialogFragment valueArray(final int[] values) {
        if (values.length <= 20) {
            boolean[] allowed = new boolean[values.length];
            Arrays.fill(allowed, true);
            return new PopupButtons(callbackData, callback, values, allowed);
        }
        else {
            return new PopupSpinner(callbackData, callback, values);
        }
    }

    public DialogFragment valueArrayAllowedRange(final int[] values, final int lb, final int ub) {
        if (ub-lb+1 <= 20) {
            int[] printedValues = new int[ub-lb+1];
            for (int i = 0; i < printedValues.length; i++) {
                printedValues[i] = lb+i;
            }
            boolean[] allowed = new boolean[ub-lb+1];
            for (int v : values) {
                allowed[v-lb] = true;
            }
            return new PopupButtons(callbackData, callback, printedValues, allowed);
        }
        else {
            return new PopupSpinner(callbackData, callback, values);
        }
    }

    public DialogFragment valueArrayAllowedArray(final int[] values, final boolean[] allowed) {
        if (values.length <= 20) {
            return new PopupButtons(callbackData, callback, values, allowed);
        }
        else {
            List<Integer> allowedValues = new ArrayList<>();
            for (int i = 0; i < values.length; i++) {
                if (allowed[i]) {
                    allowedValues.add(values[i]);
                }
            }
            return new PopupSpinner(callbackData, callback, allowedValues.stream().mapToInt(v -> v).toArray());
        }
    }
}
