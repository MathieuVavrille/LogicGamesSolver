package com.mvavrill.logicGamesSolver.controller.popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.mvavrill.logicGamesSolver.R;

/**
 * Popup to enter digits. The grid contains 12 buttons in a 4x3 grid.
 * When there are an even number of buttons one column is removed.
 * Otherwise the three columns are used.
 */
public class PopupDigits extends DialogFragment {

    private final Bundle callbackData;
    private final CallbackWithInteger callback;
    private final static int[] buttonsIds = new int[]{R.id.layout_popup_4x3_digits_1,
            R.id.layout_popup_4x3_digits_2,
            R.id.layout_popup_4x3_digits_3,
            R.id.layout_popup_4x3_digits_4,
            R.id.layout_popup_4x3_digits_5,
            R.id.layout_popup_4x3_digits_6,
            R.id.layout_popup_4x3_digits_7,
            R.id.layout_popup_4x3_digits_8,
            R.id.layout_popup_4x3_digits_9,
            R.id.layout_popup_4x3_digits_10,
            R.id.layout_popup_4x3_digits_11,
            R.id.layout_popup_4x3_digits_12};
    private final int[] buttonsValues;

    /**
     * @param callbackData, data returned to the caller
     * @param callback, caller
     * @param nbButtons, is the number of buttons. This number defines the values of the buttons. When there are 4 buttons 0-3 is used, otherwise 1-nbButtons is used.
     */
    public PopupDigits(final Bundle callbackData, final CallbackWithInteger callback, final int nbButtons) {
        super();
        this.callbackData = callbackData;
        this.callback = callback;
        buttonsValues = new int[nbButtons];
        for (int i = 0; i < nbButtons; i++)
            buttonsValues[i] = i+ (nbButtons == 4? 0 : 1);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Object nullHints = callbackData.get("hints");
        boolean[] hints = new boolean[10];
        for (int i = 0; i < 10; i++) {hints[i] = true;}
        if (nullHints != null) {
            hints = (boolean[]) nullHints;
        }
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        View buttonsView =  inflater.inflate(R.layout.popup_4x3_digits,null);
        int numberId = 0;
        for (int i = 0; i < buttonsIds.length; i++) {
            Button button = buttonsView.findViewById(buttonsIds[i]);
            if (buttonsValues.length%2 == 0 && i%3 == 2 || numberId >= buttonsValues.length)
                button.setVisibility(View.GONE);
            else {
                button.setText("" + buttonsValues[numberId]);
                button.setEnabled(hints[numberId + 1]);
                final int fi = numberId;
                button.setOnClickListener(view -> {
                    callback.callbackWithInteger(callbackData, buttonsValues[fi]);
                    dialog.dismiss();
                });
                numberId++;
            }
        }
        dialog.setView(buttonsView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }//
}