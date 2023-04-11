package com.mvavrill.logicGamesSolver.controller.popups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.button.MaterialButton;
import com.mvavrill.logicGamesSolver.R;

public class PopupButtons extends DialogFragment {

    public PopupButtons() {
        super();
    }

    @SuppressLint("ResourceType")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        View constraintView =  inflater.inflate(R.layout.popup_constraint_layout,null);
        ConstraintLayout layout = (ConstraintLayout) constraintView.findViewById(R.id.constraint_layout_main_layout);
        Button button = (Button) layout.findViewById(R.id.constraint_layout_button);
        //set the properties for button
        Button btnTag = new MaterialButton(getContext());
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
        params.leftToRight = button.getId();
        params.rightToRight = layout.getId();
        btnTag.setTextSize(30);
        btnTag.setLayoutParams(params);
        btnTag.setText("New");
        btnTag.setId(11);
        btnTag.setOnClickListener(v -> {
            System.out.println("test");
            dialog.dismiss();
        });
        layout.addView(btnTag);
        dialog.setView(constraintView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }
}
