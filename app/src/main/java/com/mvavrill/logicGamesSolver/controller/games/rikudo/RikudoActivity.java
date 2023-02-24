package com.mvavrill.logicGamesSolver.controller.games.rikudo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigits;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.rikudo.RikudoSolver;
import com.mvavrill.logicGamesSolver.view.games.rikudo.RikudoView;
import com.mvavrill.logicGamesSolver.view.games.rikudo.RikudoView;

import org.javatuples.Triplet;

/**
 * Controller for the rikudo game.
 * The data passed contains the cells, a boolean telling if the hints should be printed, and an integer equal to 1 if the grid is solved, 2 if failed, and 0 otherwise
 */
public class RikudoActivity extends AppCompatActivity implements CallbackWithInteger, UndoRedoWatcher {

    private GridHistory<Triplet<DigitCell[][],Boolean,Integer>> gridHistory;
    private Switch hintSwitch;
    private boolean doNotToggle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rikudo);
        int[][] initialGrid = (int[][]) getIntent().getSerializableExtra("grid");
        if (initialGrid == null)
            initialGrid = new int[9][9];
        // History
        Button undoButton = findViewById(R.id.rikudo_input_button_undo);
        Button redoButton = findViewById(R.id.rikudo_input_button_redo);
        RikudoView rikudoView = findViewById(R.id.rikudo_input_grid_view);
        hintSwitch = findViewById(R.id.rikudo_hint_switch);
        rikudoView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, solve(initialGrid), rikudoView, this);
        hintSwitch.setOnCheckedChangeListener((button, isChecked) -> {
            if (doNotToggle)
                doNotToggle = false;
            else
                gridHistory.addElement(new Triplet<>(gridHistory.getCurrent().getValue0(), isChecked, gridHistory.getCurrent().getValue2()));
        });
        // Exit
        Button exitButton = findViewById(R.id.rikudo_input_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void isClicked(int i, int j) {
        if (gridHistory.getCurrent().getValue0()[i][j].isFixed()) {
            newFixed(i, j, 0);
            return;
        }
        if (gridHistory.getCurrent().getValue2() == 2) {
            return;
        }
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
        Bundle b = new Bundle();
        b.putSerializable("i",i);
        b.putSerializable("j",j);
        b.putSerializable("hints", hints);
        new PopupDigits(b,this, 9).show(getSupportFragmentManager(), "");
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

    private Triplet<DigitCell[][],Boolean,Integer> solve(final int[][] grid) {
        DigitCell[][] newrikudoGrid = new RikudoSolver(grid).extractInformation();
        if (newrikudoGrid != null) {
            boolean isFilled = true;
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (newrikudoGrid[i][j].getHints() != null) {
                        isFilled = false;
                    }
                }
            }
            return new Triplet<>(newrikudoGrid, hintSwitch.isChecked(), isFilled? 1 : 0);
        }
        else {
            newrikudoGrid = new DigitCell[9][9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (grid[i][j] == 0)
                        newrikudoGrid[i][j] = new DigitCell();
                    else
                        newrikudoGrid[i][j] = new DigitCell(true, grid[i][j]);
                }
            }
            return new Triplet<>(newrikudoGrid, hintSwitch.isChecked(), 2);
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

    @Override
    public void onUndoOrRedo(boolean isUndo) {
        if (hintSwitch.isChecked() != gridHistory.getCurrent().getValue1()) {
            doNotToggle = true;
            hintSwitch.toggle();
        }
    }
}