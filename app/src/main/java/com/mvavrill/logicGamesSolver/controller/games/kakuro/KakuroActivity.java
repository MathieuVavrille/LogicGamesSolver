package com.mvavrill.logicGamesSolver.controller.games.kakuro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.popups.PopupNumberFragment;
import com.mvavrill.logicGamesSolver.model.cells.*;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.cells.DoubleIntCell;
import com.mvavrill.logicGamesSolver.model.cells.EmptyCell;
import com.mvavrill.logicGamesSolver.model.games.kakuro.KakuroSolver;
import com.mvavrill.logicGamesSolver.view.games.kakuro.KakuroView;

public class KakuroActivity extends AppCompatActivity implements CallbackWithInteger, UndoRedoWatcher {

    private GridHistory<Cell[][]> gridHistory;
    private Button outlineButton;
    private Button decreaseButton;
    private boolean isOutline = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakuro);
        decreaseButton = findViewById(R.id.kakuro_button_decrease);
        decreaseButton.setOnClickListener(view -> {
            if (gridHistory.getCurrent().length <= 3) {
                decreaseButton.setEnabled(false);
                return;
            }
            gridHistory.addElement(gridCopy(gridHistory.getCurrent(), -1));
            if (gridHistory.getCurrent().length <= 3)
                decreaseButton.setEnabled(false);
        });
        Button increaseButton = findViewById(R.id.kakuro_button_increase);
        increaseButton.setOnClickListener(view -> {
            gridHistory.addElement(gridCopy(gridHistory.getCurrent(), 1));
            if (gridHistory.getCurrent().length > 3)
                decreaseButton.setEnabled(true);
        });
        outlineButton = findViewById(R.id.kakuro_button_outline);
        outlineButton.setText("Set values");
        outlineButton.setOnClickListener(view -> {
            if (isOutline) {
                outlineButton.setText("Set outline");
                isOutline = false;
            } else {
                outlineButton.setText("Set values");
                isOutline = true;
            }
        });
        int initialGridSize = 10;
        Cell[][] initialGrid = new EmptyCell[initialGridSize][initialGridSize];
        for (int i = 0; i < initialGridSize; i++) {
            for (int j = 0; j < initialGridSize; j++) {
                initialGrid[i][j] = new EmptyCell();
            }
        }
        Button undoButton = findViewById(R.id.kakuro_button_undo);
        Button redoButton = findViewById(R.id.kakuro_button_redo);
        KakuroView kakuroView = findViewById(R.id.kakuro_grid_view);
        kakuroView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, initialGrid, kakuroView, this);
        Button exitButton = findViewById(R.id.kakuro_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void isClicked(int i, int j, boolean isFirst) {
        if (isOutline) {
            if (i == 0 || j == 0)
                return;
            Cell[][] currentGrid = gridCopy(gridHistory.getCurrent(), 0);
            if (currentGrid[i][j] instanceof DigitCell)
                currentGrid[i][j] = new EmptyCell();
            else
                currentGrid[i][j] = new DigitCell();
            recomputeNeighbours(currentGrid, i, j);
            Log.d("Mat", currentGrid[i][j].toString());
            gridHistory.addElement(currentGrid);
        } else {
            Cell[][] currentGrid = gridHistory.getCurrent();
            Bundle bundle = new Bundle();
            bundle.putSerializable("i", i);
            bundle.putSerializable("j", j);
            bundle.putSerializable("first", isFirst);
            if (currentGrid[i][j] instanceof DigitCell) {
                //bundle.putSerializable("hints", ((DigitCell) currentGrid[i][j]).allowedValues());
                //new PopupDigitFragment(bundle, this).show(getSupportFragmentManager(), "");
            } else if (currentGrid[i][j] instanceof DoubleIntCell) {
                new PopupNumberFragment(bundle, this).show(getSupportFragmentManager(), "");
            }
        }
    }

    @Override
    public void callbackWithInteger(Bundle callbackBundle, int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        boolean isFirst = (boolean) callbackBundle.get("first");
        Cell[][] currentGrid = gridCopy(gridHistory.getCurrent(), 0);
        if (currentGrid[i][j] instanceof DigitCell) {
            throw new IllegalStateException("Cannot fix digit in kakuro");
            //currentGrid[i][j] = new DigitCell(true, v);
        } else if (currentGrid[i][j] instanceof DoubleIntCell) {
            DoubleIntCell cell = (DoubleIntCell) currentGrid[i][j];
            if (isFirst) {
                if (cell.getHint1() == null)
                    currentGrid[i][j] = new DoubleIntCell(null, v);
                else
                    currentGrid[i][j] = new DoubleIntCell(v, cell.getHint2());
            } else {
                if (cell.getHint2() == null)
                    currentGrid[i][j] = new DoubleIntCell(v, null);
                else
                    currentGrid[i][j] = new DoubleIntCell(cell.getHint1(), v);
            }
        } else
            throw new IllegalStateException("Cannot fix empty cell");
        //gridHistory.addElement(currentGrid);
        Cell[][] newKakuroGrid = new KakuroSolver(currentGrid).extractInformation();
        if (newKakuroGrid != null) {
            /*for (int li = 0; li < newKakuroGrid.length; li++) {
                for (int lj = 0; lj < newKakuroGrid[li].length; lj++) {
                    if (newKakuroGrid[li][lj] instanceof DigitCell) {
                        ((DigitCell) newKakuroGrid[li][lj]).fix(((DigitCell) currentGrid[li][lj]).isFixed());
                    }
                }
            }*/
            gridHistory.addElement(newKakuroGrid);
        }
    }

    public void recomputeNeighbours(Cell[][] modifiedGrid, int i, int j) {
        if (modifiedGrid[i][j] instanceof EmptyCell) {
            // Modify current grid
            if (i + 1 < modifiedGrid.length && modifiedGrid[i + 1][j] instanceof DigitCell
                    && j + 1 < modifiedGrid.length && modifiedGrid[i][j + 1] instanceof DigitCell)
                modifiedGrid[i][j] = new DoubleIntCell(-1, -1);
            else if (i + 1 < modifiedGrid.length && modifiedGrid[i + 1][j] instanceof DigitCell)
                modifiedGrid[i][j] = new DoubleIntCell(null, -1);
            else if (j + 1 < modifiedGrid.length && modifiedGrid[i][j + 1] instanceof DigitCell)
                modifiedGrid[i][j] = new DoubleIntCell(-1, null);
            // Modify upper and left ones
            if (modifiedGrid[i - 1][j] instanceof DoubleIntCell)
                modifiedGrid[i - 1][j] = ((DoubleIntCell) modifiedGrid[i - 1][j]).removeSecond();
            if (modifiedGrid[i][j - 1] instanceof DoubleIntCell)
                modifiedGrid[i][j - 1] = ((DoubleIntCell) modifiedGrid[i][j - 1]).removeFirst();
        } else { // instance of DigitCell
            if (modifiedGrid[i - 1][j] instanceof EmptyCell)
                modifiedGrid[i - 1][j] = new DoubleIntCell(null, -1);
            else if (modifiedGrid[i - 1][j] instanceof DoubleIntCell)
                modifiedGrid[i - 1][j] = ((DoubleIntCell) modifiedGrid[i - 1][j]).addSecond(-1);
            if (modifiedGrid[i][j - 1] instanceof EmptyCell)
                modifiedGrid[i][j - 1] = new DoubleIntCell(-1, null);
            else if (modifiedGrid[i][j - 1] instanceof DoubleIntCell)
                modifiedGrid[i][j - 1] = ((DoubleIntCell) modifiedGrid[i][j - 1]).addFirst(-1);
        }
    }

    private Cell[][] gridCopy(final Cell[][] grid, final int increase) {
        Cell[][] newGrid = new Cell[grid.length + increase][grid.length + increase];
        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[i].length; j++) {
                newGrid[i][j] = (i >= grid.length || j >= grid.length) ? new EmptyCell() : grid[i][j].copy();
            }
        }
        return newGrid;
    }

    @Override
    public void onUndoOrRedo(boolean isUndo) {
        decreaseButton.setEnabled(gridHistory.getCurrent().length > 3);
    }
}