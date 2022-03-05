package com.mvavrill.logicGamesSolver.controller.games.kakuro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.Button;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.PopupCallback;
import com.mvavrill.logicGamesSolver.controller.PopupDigit;
import com.mvavrill.logicGamesSolver.model.cells.*;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.cells.DoubleIntCell;
import com.mvavrill.logicGamesSolver.model.cells.EmptyCell;
import com.mvavrill.logicGamesSolver.view.games.kakuro.KakuroView;

public class KakuroActivity extends AppCompatActivity implements PopupCallback {

    private KakuroView kakuroView;
    private ConstraintLayout gridConstraintLayout;

    private GridHistory<Cell[][]> gridHistory;
    private Button increaseButton;
    private Button decreaseButton;
    private Button outlineButton;
    private boolean isOutline = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakuro);
        gridConstraintLayout = findViewById(R.id.kakuro_constraint_layout);
        increaseButton = findViewById(R.id.kakuro_button_increase);
        decreaseButton = findViewById(R.id.kakuro_button_decrease);
        outlineButton = findViewById(R.id.kakuro_button_outline);
        outlineButton.setOnClickListener(view -> {
            if (isOutline) {
                outlineButton.setText("Set values");
                isOutline = false;
            }
            else {
                outlineButton.setText("Set outline");
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
        kakuroView = findViewById(R.id.kakuro_grid_view);
        kakuroView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, initialGrid, kakuroView);
        Button exitButton = findViewById(R.id.kakuro_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void isClicked(int i, int j) {
        if (isOutline) {
            if (i == 0 || j == 0)
                return;
            Cell[][] currentGrid = gridCopy(gridHistory.getCurrent());
            if (currentGrid[i][j] instanceof DigitCell)
                currentGrid[i][j] = new EmptyCell();
            else
                currentGrid[i][j] = new DigitCell();
            recomputeNeighbours(currentGrid, i, j);
            gridHistory.addElement(currentGrid);
        }
        else {

        }
    }

    public void recomputeNeighbours(Cell[][] modifiedGrid, int i, int j) {
        if (modifiedGrid[i][j] instanceof EmptyCell) {
            if (i+1 < modifiedGrid.length && modifiedGrid[i+1][j] instanceof DigitCell
                    || j+1 < modifiedGrid.length && modifiedGrid[i][j+1] instanceof DigitCell)
                modifiedGrid[i][j] = new DoubleIntCell();
            if (modifiedGrid[i-1][j] instanceof DoubleIntCell && !(j+1 < modifiedGrid.length && modifiedGrid[i-1][j+1] instanceof DigitCell))
                modifiedGrid[i-1][j] = new EmptyCell();
            if (modifiedGrid[i][j-1] instanceof DoubleIntCell && !(i+1 < modifiedGrid.length && modifiedGrid[i+1][j-1] instanceof DigitCell))
                modifiedGrid[i][j-1] = new EmptyCell();
        }
        else { // instance of DigitCell
            if (modifiedGrid[i-1][j] instanceof EmptyCell)
                modifiedGrid[i-1][j] = new DoubleIntCell();
            if (modifiedGrid[i][j-1] instanceof EmptyCell)
                modifiedGrid[i][j-1] = new DoubleIntCell();
        }
    }

    public void popup(int i, int j) {
        new PopupDigit(i,j,this).run();
    }

    @Override
    public void callback(int i, int j, int v) {
        setGridValue(i,j,v);
    }

    public void setGridValue(int I, int J, int v) {
        /*Cell[][] currentGrid = gridCopy(gridHistory.getCurrent());
        currentGrid[I][J] = new DigitCell(true, v - 1);
        DigitCell[][] newSudokuGrid = new SudokuSolver(currentGrid).extractInformation();
        if (newSudokuGrid != null) {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    newSudokuGrid[i][j].fix(currentGrid[i][j].isFixed());
                }
            }
            gridHistory.addElement(newSudokuGrid);
        }*/
    }
    private Cell[][] gridCopy(final Cell[][] grid) {
        Cell[][] newGrid = new Cell[9][9];
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