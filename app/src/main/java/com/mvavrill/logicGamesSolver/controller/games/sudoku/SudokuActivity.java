package com.mvavrill.logicGamesSolver.controller.games.sudoku;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.PopupCallback;
import com.mvavrill.logicGamesSolver.controller.PopupDigit;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.sudoku.SudokuSolver;
import com.mvavrill.logicGamesSolver.view.games.sudoku.SudokuView;

import java.util.ArrayList;
import java.util.List;

public class SudokuActivity extends AppCompatActivity implements PopupCallback {

    private GridHistory<DigitCell[][]> gridHistory;
    private ConstraintLayout gridConstraintLayout;
    private SudokuView sudokuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        gridConstraintLayout = findViewById(R.id.sudoku_input_constraint_layout);
        // Initial grid
        DigitCell[][] initialGrid = new DigitCell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                initialGrid[i][j] = new DigitCell();
            }
        }
        // History
        Button undoButton = findViewById(R.id.sudoku_input_button_undo);
        Button redoButton = findViewById(R.id.sudoku_input_button_redo);
        sudokuView = findViewById(R.id.sudoku_input_grid_view);
        sudokuView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, initialGrid, sudokuView);
        // Exit
        Button exitButton = findViewById(R.id.sudoku_input_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    @Override
    public void callback(int i, int j, int v) {
        setGridValue(i,j,v);
    }

    public void setGridValue(int I, int J, int v) {
        DigitCell[][] currentGrid = gridCopy(gridHistory.getCurrent());
        currentGrid[I][J] = new DigitCell(true, v-1);
        DigitCell[][] newSudokuGrid = new SudokuSolver(currentGrid).extractInformation();
        if (newSudokuGrid != null) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    newSudokuGrid[i][j].fix(currentGrid[i][j].isFixed());
                }
            }
            gridHistory.addElement(newSudokuGrid);
        }
    }

    public void popup(int i, int j) {
        new PopupDigit(i,j,this).run();
    }

    private DigitCell[][] gridCopy(final DigitCell[][] grid) {
        DigitCell[][] newGrid = new DigitCell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                newGrid[i][j] = grid[i][j].copy();
            }
        }
        return newGrid;
    }

    @Override
    public ConstraintLayout getGridConstraintLayout() {
        return gridConstraintLayout;
    }
}