package com.mvavrill.logicGamesSolver.controller.games.sudoku;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigitFragment;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.sudoku.SudokuSolver;
import com.mvavrill.logicGamesSolver.view.games.sudoku.SudokuView;

import org.javatuples.Triplet;

public class SudokuActivity extends AppCompatActivity implements CallbackWithInteger {
    //TODO OnUndo
    private GridHistory<Triplet<DigitCell[][],Boolean,Boolean>> gridHistory;
    private Switch hintSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        int[][] initialGrid = (int[][]) getIntent().getSerializableExtra("grid");
        if (initialGrid == null)
            initialGrid = new int[9][9];
        /*initialGrid[0][0] = 1;
        initialGrid[0][2] = 2;
        initialGrid[0][4] = 4;
        initialGrid[1][1] = 4;
        initialGrid[1][3] = 5;
        initialGrid[2][0] = 5;
        initialGrid[2][3] = 8;
        initialGrid[2][5] = 1;
        initialGrid[2][6] = 6;
        initialGrid[2][8] = 3;
        initialGrid[3][1] = 2;
        initialGrid[4][4] = 5;
        initialGrid[4][6] = 9;
        initialGrid[4][8] = 6;
        initialGrid[5][0] = 4;
        initialGrid[5][2] = 5;
        initialGrid[5][3] = 7;
        initialGrid[5][6] = 8;
        initialGrid[6][2] = 4;
        initialGrid[6][5] = 3;
        initialGrid[7][1] = 3;
        initialGrid[7][3] = 9;
        initialGrid[7][4] = 8;
        initialGrid[7][7] = 6;
        initialGrid[8][0] = 8;
        initialGrid[8][2] = 6;
        initialGrid[8][5] = 5;*/
        // History
        Button undoButton = findViewById(R.id.sudoku_input_button_undo);
        Button redoButton = findViewById(R.id.sudoku_input_button_redo);
        SudokuView sudokuView = findViewById(R.id.sudoku_input_grid_view);
        hintSwitch = findViewById(R.id.sudoku_hint_switch);
        sudokuView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, solve(initialGrid), sudokuView);
        hintSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            gridHistory.addElement(new Triplet<>(gridHistory.getCurrent().getValue0(), gridHistory.getCurrent().getValue1(), isChecked));
        });
        // Exit
        Button exitButton = findViewById(R.id.sudoku_input_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void popup(int i, int j) {
        Bundle b = new Bundle();
        b.putSerializable("i",i);
        b.putSerializable("j",j);
        if (gridHistory.getCurrent().getValue0()[i][j].isFixed()) {
            newFixed(i, j, 0);
            return;
        }
        if (!gridHistory.getCurrent().getValue1())
            return;
        boolean[] hints = gridHistory.getCurrent().getValue0()[i][j].allowedValues();
        int possibleVal = 0;
        boolean moreThanTwo = false;
        for (int valId = 0; valId < 10; valId++) {
            if (hints[valId]) {
                if (possibleVal == 0)
                    possibleVal = valId;
                else
                    moreThanTwo = true;
            }
        }
        if (!moreThanTwo) {
            newFixed(i,j,possibleVal);
            return;
        }
        b.putSerializable("hints", hints);
        new PopupDigitFragment(b,this, 9).show(getSupportFragmentManager(), "");
    }

    private int[][] extractFixed(final DigitCell[][] grid) {
        int[][] newGrid = new int[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (grid[i][j].isFixed())
                    newGrid[i][j] = grid[i][j].getValue();
            }
        }
        return newGrid;
    }

    private void solveAndAdd(final int[][] grid) {
        gridHistory.addElement(solve(grid));
    }

    private Triplet<DigitCell[][],Boolean,Boolean> solve(final int[][] grid) {
        DigitCell[][] newSudokuGrid = new SudokuSolver(grid).extractInformation();
        if (newSudokuGrid != null) {
            return new Triplet<>(newSudokuGrid, true, hintSwitch.isChecked());
        }
        else {
            newSudokuGrid = new DigitCell[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (grid[i][j] == 0)
                        newSudokuGrid[i][j] = new DigitCell();
                    else
                        newSudokuGrid[i][j] = new DigitCell(true, grid[i][j]);
                }
            }
            return new Triplet<>(newSudokuGrid, false, hintSwitch.isChecked());
        }
    }

    private void newFixed(final int i, final int j, final int v) {
        int[][] currentGrid = extractFixed(gridHistory.getCurrent().getValue0());
        currentGrid[i][j] = v;
        solveAndAdd(currentGrid);
    }

    @Override
    public void callbackWithInteger(final Bundle callbackBundle, final int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        newFixed(i,j,v);
    }
}