package com.mvavrill.logicGamesSolver.controller;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;

public class PopupDigit {
    private final int i;
    private final int j;
    private final SudokuActivity grid;

    public PopupDigit(final int i, final int j, final SudokuActivity grid) {
        this.i = i;
        this.j = j;
        this.grid = grid;
    }

    public void run() {
        LayoutInflater layoutInflater = (LayoutInflater) grid.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = layoutInflater.inflate(R.layout.popup_numbers, null);

        //instantiate popup window
        PopupWindow popupWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //display the popup window
        popupWindow.showAtLocation(grid.getGridConstraintLayout(), Gravity.CENTER, 0, 0);

        //close the popup window on button click
        Button button1 = (Button) customView.findViewById(R.id.layout_popup_button_1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,1);
                popupWindow.dismiss();
            }
        });
        Button button2 = (Button) customView.findViewById(R.id.layout_popup_button_2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,2);
                popupWindow.dismiss();
            }
        });
        Button button3 = (Button) customView.findViewById(R.id.layout_popup_button_3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,3);
                popupWindow.dismiss();
            }
        });
        Button button4 = (Button) customView.findViewById(R.id.layout_popup_button_4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,4);
                popupWindow.dismiss();
            }
        });
        Button button5 = (Button) customView.findViewById(R.id.layout_popup_button_5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,5);
                popupWindow.dismiss();
            }
        });
        Button button6 = (Button) customView.findViewById(R.id.layout_popup_button_6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,6);
                popupWindow.dismiss();
            }
        });
        Button button7 = (Button) customView.findViewById(R.id.layout_popup_button_7);
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,7);
                popupWindow.dismiss();
            }
        });
        Button button8 = (Button) customView.findViewById(R.id.layout_popup_button_8);
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,8);
                popupWindow.dismiss();
            }
        });
        Button button9 = (Button) customView.findViewById(R.id.layout_popup_button_9);
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grid.setGridValue(i,j,9);
                popupWindow.dismiss();
            }
        });
    }
}
