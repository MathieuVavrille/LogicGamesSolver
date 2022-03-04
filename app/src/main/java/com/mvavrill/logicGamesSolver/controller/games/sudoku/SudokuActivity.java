package com.mvavrill.logicGamesSolver.controller.games.sudoku;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.PopupDigit;
import com.mvavrill.logicGamesSolver.model.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.sudoku.SudokuSolver;
import com.mvavrill.logicGamesSolver.view.games.sudoku.SudokuView;

import java.util.ArrayList;
import java.util.List;

public class SudokuActivity extends AppCompatActivity {

    private final List<DigitCell[][]> gridHistory= new ArrayList<>();
    private int plottedGridId = 0;
    private ConstraintLayout gridConstraintLayout;
    private Button undoButton;
    private Button redoButton;
    private SudokuView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        gridConstraintLayout = findViewById(R.id.sudoku_input_constraint_layout);
        gridView = findViewById(R.id.sudoku_input_grid_view);
        gridView.setGridActivity(this);
        DigitCell[][] initialGrid = new DigitCell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                initialGrid[i][j] = new DigitCell();
            }
        }
        gridHistory.add(initialGrid);
        gridView.setGrid(gridHistory.get(plottedGridId));
        Button exitButton = findViewById(R.id.sudoku_input_button_back);
        exitButton.setOnClickListener(view -> finish());
        undoButton = findViewById(R.id.sudoku_input_button_undo);
        undoButton.setEnabled(false);
        redoButton = findViewById(R.id.sudoku_input_button_redo);
        redoButton.setEnabled(false);
        undoButton.setOnClickListener(view -> {
            if (plottedGridId == 0)
                throw new IllegalStateException("cannot undo");
            plottedGridId--;
            redoButton.setEnabled(true);
            if (plottedGridId == 0)
                undoButton.setEnabled(false);
            updateView();
        });
        redoButton.setOnClickListener(view -> {
            if (plottedGridId == gridHistory.size()-1)
                throw new IllegalStateException("cannot redo");
            plottedGridId++;
            undoButton.setEnabled(true);
            if (plottedGridId == gridHistory.size()-1)
                redoButton.setEnabled(false);
            updateView();
        });
    }

    public void setGridValue(int I, int J, int v) {
        DigitCell[][] copy = gridCopy(gridHistory.get(plottedGridId));
        copy[I][J] = new DigitCell(true, v-1);
        DigitCell[][] newSudokuGrid = new SudokuSolver(copy).extractInformation();
        if (newSudokuGrid != null) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    newSudokuGrid[i][j].fix(gridHistory.get(plottedGridId)[i][j].isFixed());
                }
            }
            while (gridHistory.size() > plottedGridId+1)
                gridHistory.remove(gridHistory.size()-1);
            gridHistory.add(newSudokuGrid);
            plottedGridId++;
            undoButton.setEnabled(true);
            redoButton.setEnabled(false);
            updateView();
        }
    }

    public void popup(int i, int j) {
        new PopupDigit(i,j,this).run();
    }

    private void updateView() {
        gridView.setGrid(gridHistory.get(plottedGridId));
    }

    private DigitCell[][] gridCopy(final DigitCell[][] grid) {
        DigitCell[][] newGrid = new DigitCell[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                newGrid[i][j] = new DigitCell(grid[i][j]);
            }
        }
        return newGrid;
    }

    public ConstraintLayout getGridConstraintLayout() {
        return gridConstraintLayout;
    }
}