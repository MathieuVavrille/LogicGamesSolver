package com.mvavrill.logicGamesSolver.controller.games.sudoku;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigitFragment;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.sudoku.SudokuSolver;
import com.mvavrill.logicGamesSolver.view.games.sudoku.SudokuView;

public class SudokuActivity extends AppCompatActivity implements CallbackWithInteger {

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

    public boolean[] singleValueHints(int v) {
        boolean[] hints = new boolean[10];
        hints[v] = true;
        return hints;
    }

    public void popup(int i, int j) {
        Bundle b = new Bundle();
        b.putSerializable("i",i);
        b.putSerializable("j",j);
        b.putSerializable("hints", gridHistory.getCurrent()[i][j].allowedValues());
        new PopupDigitFragment(b,this).show(getSupportFragmentManager(), "");
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
    public void callbackWithInteger(Bundle callbackBundle, int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        DigitCell[][] currentGrid = gridCopy(gridHistory.getCurrent());
        currentGrid[i][j] = new DigitCell(true, v);
        Log.d("Mat", v + "");
        DigitCell[][] newSudokuGrid = new SudokuSolver(currentGrid).extractInformation();
        if (newSudokuGrid != null) {
            for (int li = 0; li < 9; li++) {
                for (int lj = 0; lj < 9; lj++) {
                    newSudokuGrid[li][lj].fix(currentGrid[li][lj].isFixed());
                }
            }
            gridHistory.addElement(newSudokuGrid);
        }
    }
}