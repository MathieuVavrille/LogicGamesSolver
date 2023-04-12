package com.mvavrill.logicGamesSolver.controller.popups.integer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.mvavrill.logicGamesSolver.R;

public class PopupButtons extends DialogFragment {

    private final Bundle callbackData;
    private final CallbackWithInteger callback;
    private final int[] values;
    private final boolean[] activated;

    public PopupButtons(final Bundle callbackData, final CallbackWithInteger callback, final int[] values, final boolean[] activated) {
        super();
        this.callbackData = callbackData;
        this.callback = callback;
        this.values = values;
        this.activated = activated;
    }

    @NonNull
    @SuppressLint("ResourceType")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        View constraintView =  inflater.inflate(R.layout.popup_constraint_layout,null);
        ConstraintLayout layout = (ConstraintLayout) constraintView.findViewById(R.id.constraint_layout_main_layout);
        Button[][] buttons = createButtons(layout.getId(), dialog);
        for (Button[] buttonLines : buttons) {
            for (Button button : buttonLines) {
                layout.addView(button);
            }
        }
        dialog.setView(constraintView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    private Button[][] createButtons(final int parentId, final Dialog dialog) {
        int mod = intSqrt(values.length);
        if (values.length == 8) {
            mod = 4;
        }
        else if (values.length == 10 || 12 < values.length && values.length <= 15) {
            mod = 5;
        }
        int idCpt = 0;
        Button[][] buttons = new Button[mod][];
        for (int i = 0; i < mod; i++) {
            buttons[i] = new Button[values.length/mod + ((i < values.length%mod) ? 1 : 0)];
            for (int j = 0; j < buttons[i].length; j++) {
                final int currCpt = idCpt;
                buttons[i][j] = new MaterialButton(getContext());
                buttons[i][j].setText(Integer.toString(values[idCpt]));
                buttons[i][j].setEnabled(activated[idCpt]);
                buttons[i][j].setTextSize(40);
                buttons[i][j].setOnClickListener(v -> {
                    callback.callbackWithInteger(callbackData, values[currCpt]);
                    dialog.dismiss();
                });
                buttons[i][j].setId(++idCpt);
            }
        }
        for (int i = 0; i < buttons.length; i++) {
            buttons[i][0].setLayoutParams(getStartConstraintParams(i == 0 ? parentId : buttons[i-1][0].getId(),i == buttons.length-1 ? parentId : buttons[i+1][0].getId(), buttons[i].length == 1 ? parentId : buttons[i][1].getId(), parentId));
            for (int j = 1; j < buttons[i].length; j++) {
                buttons[i][j].setLayoutParams(getLineConstraintParams(buttons[i][0].getId(), buttons[i][j-1].getId(), j == buttons[i].length-1 ? parentId : buttons[i][j+1].getId(), parentId));
            }
        }
        return buttons;
    }

    private ConstraintLayout.LayoutParams getStartConstraintParams(final int up, final int down, final int right, final int parent) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        if (up == parent) params.topToTop = parent;
        else params.topToBottom = up;
        if (down == parent) params.bottomToBottom = parent;
        else params.bottomToTop = down;
        params.leftToLeft = parent;
        if (right == parent) params.rightToRight = parent;
        else params.rightToLeft = right;
        params.horizontalChainStyle = Constraints.LayoutParams.CHAIN_PACKED;
        params.verticalChainStyle = Constraints.LayoutParams.CHAIN_PACKED;
        return params;
    }

    private ConstraintLayout.LayoutParams getLineConstraintParams(final int leftMost, final int left, final int right, final int parent) {
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.topToTop = leftMost;
        params.bottomToBottom = leftMost;
        params.leftToRight = left;
        if (right == parent) params.rightToRight = parent;
        else params.rightToLeft = right;
        params.horizontalChainStyle = Constraints.LayoutParams.CHAIN_PACKED;
        return params;
    }

    private int intSqrt(final int n) {
        int i = 0;
        while(i*i < n) {
            i++;
        }
        return i;
    }
}
