package com.mvavrill.logicGamesSolver.controller.popups;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;

import com.mvavrill.logicGamesSolver.R;

/**
 * Popup to enter digits. The grid contains 12 buttons in a 4x3 grid.
 * When there are an even number of buttons one column is removed.
 * Otherwise the three columns are used.
 */
public class PopupSpinner extends DialogFragment implements AdapterView.OnItemSelectedListener {

    private final Bundle callbackData;
    private final CallbackWithInteger callback;
    private final Context context;

    private AlertDialog dialog;

    private final int[] values;

    /**
     * @param callbackData, data returned to the caller
     * @param callback, caller
     * @param context, the caller context
     */
    public PopupSpinner(final Bundle callbackData, final CallbackWithInteger callback, final Context context, int[] values) {
        super();
        this.callbackData = callbackData;
        this.callback = callback;
        this.context = context;
        this.values = values;
    }

    // end included
    public static PopupSpinner fromRange(final Bundle callbackData, final CallbackWithInteger callback, final Context context, final int start, final int end) {
        int[] values = new int[end-start+1];
        for (int i = 0; i < values.length; i++) {
            values[i] = start+i;
        }
        return new PopupSpinner(callbackData, callback, context, values);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        dialog = new AlertDialog.Builder(getActivity()).create();
        View spinnerView =  inflater.inflate(R.layout.popup_spinner,null);
        Spinner spinner = spinnerView.findViewById(R.id.popup_spinner);
        String[] spinnerValues = new String[values.length+1];
        spinnerValues[0] = "Input";
        for (int i = 1; i < spinnerValues.length; i++) {
            spinnerValues[i] = Integer.toString(values[i-1]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, spinnerValues);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        dialog.setView(spinnerView);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != 0) {
            callback.callbackWithInteger(callbackData, values[i-1]);
            dialog.dismiss();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}