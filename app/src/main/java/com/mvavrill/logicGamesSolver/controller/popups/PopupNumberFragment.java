package com.mvavrill.logicGamesSolver.controller.popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.mvavrill.logicGamesSolver.R;

/**
 * An implementation of a DialogFragment to input a number.
 */
public class PopupNumberFragment extends DialogFragment {

    private final Bundle callbackData;
    private final CallbackWithInteger callback;

    public PopupNumberFragment(final Bundle callbackData, final CallbackWithInteger callback) {
        super();
        this.callbackData = callbackData;
        this.callback = callback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View editTextView = inflater.inflate(R.layout.popup_edittext, null);
        EditText editText = editTextView.findViewById(R.id.popup_edittext_edittext);
        if(editText.getParent() != null) {
            ((ViewGroup)editText.getParent()).removeView(editText); // <- fix
        }
        editText.setTransformationMethod(null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog = builder.setNegativeButton("Cancel", (dialog12, id) -> dialog12.dismiss())
                .setPositiveButton("Ok", (dialog1, id) -> {
                    String editedText = editText.getText().toString();
                    try {
                        int value = Integer.parseInt(editedText);
                        callback.callbackWithInteger(callbackData, value);
                    }
                    catch (Exception ignored) {}
                        dialog1.dismiss();
                })
                .create();
        dialog.setView(editText);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

}
