package com.mvavrill.logicGamesSolver.controller;

import androidx.constraintlayout.widget.ConstraintLayout;

public interface PopupCallback {
    void callback(int i, int j, int v);
    ConstraintLayout getGridConstraintLayout();

    Object getSystemService(String layoutInflaterService);
}
