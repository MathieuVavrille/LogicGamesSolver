package com.mvavrill.logicGamesSolver.model.cells;

public class EmptyCell implements Cell{
    public EmptyCell copy() {
        return new EmptyCell();
    }

    @Override
    public String toString() {
        return "()";
    }
}
