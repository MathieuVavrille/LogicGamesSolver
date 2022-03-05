package com.mvavrill.logicGamesSolver.controller;

import android.widget.Button;

import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

import java.util.ArrayList;
import java.util.List;

public class GridHistory<T> {

    private final Button undo;
    private final Button redo;
    private final UpdatableView<T> updatableView;

    private final List<T> history = new ArrayList<T>();
    private int currentElement = 0;

    public GridHistory(final Button undo, final Button redo, final T initialElement, final UpdatableView<T> updatableView) {
        this.undo = undo;
        this.redo = redo;
        this.updatableView = updatableView;
        history.add(initialElement);
        undo.setEnabled(false);
        redo.setEnabled(false);
        // Listeners
        undo.setOnClickListener(view -> {
            if (currentElement == 0)
                throw new IllegalStateException("cannot undo");
            currentElement--;
            redo.setEnabled(true);
            if (currentElement == 0)
                undo.setEnabled(false);
            updatableView.update(history.get(currentElement));
        });
        redo.setOnClickListener(view -> {
            if (currentElement == history.size()-1)
                throw new IllegalStateException("cannot redo");
            currentElement++;
            undo.setEnabled(true);
            if (currentElement == history.size()-1)
                redo.setEnabled(false);
            updateView();
        });
        updateView();
    }

    public void addElement(final T newElement) {
        while (history.size() > currentElement+1)
            history.remove(history.size()-1);
        history.add(newElement);
        currentElement++;
        undo.setEnabled(true);
        redo.setEnabled(false);
        updateView();
    }

    private void updateView() {
        updatableView.update(history.get(currentElement));
    }

    public T getCurrent() {
        return history.get(currentElement);
    }
}
