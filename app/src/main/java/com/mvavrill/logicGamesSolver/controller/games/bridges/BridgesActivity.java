package com.mvavrill.logicGamesSolver.controller.games.bridges;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigitFragment;
import com.mvavrill.logicGamesSolver.model.cells.Cell;
import com.mvavrill.logicGamesSolver.model.cells.EmptyCell;
import com.mvavrill.logicGamesSolver.view.games.bridges.BridgesView;

import java.util.Arrays;

public class BridgesActivity extends AppCompatActivity implements CallbackWithInteger, UndoRedoWatcher {

    private GridHistory<int[][]> gridHistory;
    private Button decreaseButton;

    private static final int minimumSize = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridges);
        decreaseButton = findViewById(R.id.bridges_button_decrease);
        decreaseButton.setOnClickListener(view -> {
            if (gridHistory.getCurrent().length <= minimumSize) {
                decreaseButton.setEnabled(false);
                return;
            }
            gridHistory.addElement(gridCopy(gridHistory.getCurrent(), -1));
            if (gridHistory.getCurrent().length <= minimumSize)
                decreaseButton.setEnabled(false);
        });
        Button increaseButton = findViewById(R.id.bridges_button_increase);
        increaseButton.setOnClickListener(view -> {
            gridHistory.addElement(gridCopy(gridHistory.getCurrent(), 1));
            if (gridHistory.getCurrent().length > 3)
                decreaseButton.setEnabled(true);
        });
        int[][] initialGrid = new int[7][7];
        Button undoButton = findViewById(R.id.bridges_button_undo);
        Button redoButton = findViewById(R.id.bridges_button_redo);
        BridgesView bridgesView = findViewById(R.id.bridges_grid_view);
        bridgesView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, initialGrid, bridgesView, this);
        Button exitButton = findViewById(R.id.bridges_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void isClicked(int i, int j) {
        Bundle b = new Bundle();
        b.putSerializable("i",i);
        b.putSerializable("j",j);
        if (gridHistory.getCurrent()[i][j] != 0) {
            int[][] newGrid = gridCopy(gridHistory.getCurrent(),0);
            newGrid[i][j] = 0;
            solveAndAdd(newGrid);
            return;
        }
        else {

        }
        boolean[] hints = new boolean[10];
        Arrays.fill(hints,true);
        hints[9] = false;
        b.putSerializable("hints", hints);
        new PopupDigitFragment(b,this, 9).show(getSupportFragmentManager(), "");
    }

    private void solveAndAdd(final int[][] grid) {
        gridHistory.addElement(grid);// TODO actually solve
    }

    private int[][] gridCopy(final int[][] grid, final int increase) {
        int[][] newGrid = new int[grid.length + increase][grid.length + increase];
        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[i].length; j++) {
                newGrid[i][j] = (i >= grid.length || j >= grid.length) ? 0 : grid[i][j];
            }
        }
        return newGrid;
    }

    private void newFixed(final int i, final int j, final int v) {
        int[][] currentGrid = gridCopy(gridHistory.getCurrent(),0);
        currentGrid[i][j] = v;
        solveAndAdd(currentGrid);
    }

    @Override
    public void onUndoOrRedo(boolean isUndo) {
        decreaseButton.setEnabled(gridHistory.getCurrent().length > minimumSize);
    }

    @Override
    public void callbackWithInteger(final Bundle callbackBundle, final int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        newFixed(i,j,v);
    }
}