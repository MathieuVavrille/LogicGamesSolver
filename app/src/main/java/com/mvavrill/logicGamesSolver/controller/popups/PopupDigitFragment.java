package com.mvavrill.logicGamesSolver.controller.popups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.mvavrill.logicGamesSolver.R;

public class PopupDigitFragment extends DialogFragment {

    private final Bundle callbackData;
    private final CallbackWithInteger callback;
    private final int[] buttonsIds;
    private final int layoutId;
    private final static int[] buttons9Ids = new int[]{R.id.layout_popup_9digits_1,
            R.id.layout_popup_9digits_2,
            R.id.layout_popup_9digits_3,
            R.id.layout_popup_9digits_4,
            R.id.layout_popup_9digits_5,
            R.id.layout_popup_9digits_6,
            R.id.layout_popup_9digits_7,
            R.id.layout_popup_9digits_8,
            R.id.layout_popup_9digits_9};
    private final static int[] buttons4Ids = new int[]{R.id.layout_popup_4digits_1,
            R.id.layout_popup_4digits_2,
            R.id.layout_popup_4digits_3,
            R.id.layout_popup_4digits_4};
    private final int[] buttonsValues;

    public PopupDigitFragment(final Bundle callbackData, final CallbackWithInteger callback, final int nbDigits) {
        super();
        this.callbackData = callbackData;
        this.callback = callback;
        if (nbDigits == 4) {
            layoutId = R.layout.popup_4digits;
            buttonsIds = buttons4Ids;
            buttonsValues = new int[]{0,1,2,3};
        }
        else {
            layoutId = R.layout.popup_9digits;
            buttonsIds = buttons9Ids;
            buttonsValues = new int[]{1,2,3,4,5,6,7,8,9};
        }
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
        View buttonsView =  inflater.inflate(layoutId,null);
        for (int i = 0; i < buttonsIds.length; i++) {
            final int fi = i;
            Button button = buttonsView.findViewById(buttonsIds[fi]);
            button.setText(""+buttonsValues[fi]);
            button.setEnabled(hints[fi+1]); // TODO maybe improve this
            button.setOnClickListener(view -> {
                callback.callbackWithInteger(callbackData, buttonsValues[fi]);
                dialog.dismiss();
            });
        }
        dialog.setView(buttonsView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
