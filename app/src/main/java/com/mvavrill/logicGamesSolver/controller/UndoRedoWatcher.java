package com.mvavrill.logicGamesSolver.controller;

public interface UndoRedoWatcher {
    void onUndoOrRedo(final boolean isUndo);
}
