package com.mvavrill.logicGamesSolver.controller.popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.CallbackWithInteger;

public class PopupDigitFragment extends DialogFragment {

    private final Bundle callbackData;
    private final CallbackWithInteger callback;
    private final static int[] buttonsIds = new int[]{R.id.layout_popup_digit19_1,
            R.id.layout_popup_digit19_2,
            R.id.layout_popup_digit19_3,
            R.id.layout_popup_digit19_4,
            R.id.layout_popup_digit19_5,
            R.id.layout_popup_digit19_6,
            R.id.layout_popup_digit19_7,
            R.id.layout_popup_digit19_8,
            R.id.layout_popup_digit19_9};

    public PopupDigitFragment(final Bundle callbackData, final CallbackWithInteger callback) {
        super();
        this.callbackData = callbackData;
        this.callback = callback;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        View buttonsView =  inflater.inflate(R.layout.popup_digit1_9,null);
        for (int i = 0; i < 9; i++) {
            final int fi = i;
            Button button = buttonsView.findViewById(buttonsIds[i]);
            button.setOnClickListener(view -> {
                callback.callbackWithInteger(callbackData, fi+1);
                dialog.dismiss();
            });
        }
        dialog.setView(buttonsView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
