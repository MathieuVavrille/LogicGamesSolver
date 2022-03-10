package com.mvavrill.logicGamesSolver.controller.games.slitherlink;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigitFragment;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.slitherlink.SlitherlinkSolver;
import com.mvavrill.logicGamesSolver.model.games.sudoku.SudokuSolver;
import com.mvavrill.logicGamesSolver.view.games.slitherlink.SlitherlinkView;
import com.mvavrill.logicGamesSolver.view.games.sudoku.SudokuView;

import org.javatuples.Triplet;

public class SlitherlinkActivity extends AppCompatActivity implements CallbackWithInteger {

    private GridHistory<Triplet<int[][],int[][],int[][]>> gridHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slitherlink);
        // Initial grid
        int initialSize = 5;
        int[][] initialNumbers = new int[initialSize][initialSize];
        for (int i = 0; i < initialSize; i++) {
            for (int j = 0; j < initialSize; j++) {
                initialNumbers[i][j] = -1;
            }
        }
        // History
        Button undoButton = findViewById(R.id.slitherlink_button_undo);
        Button redoButton = findViewById(R.id.slitherlink_button_redo);
        SlitherlinkView slitherlinkView = findViewById(R.id.slitherlink_grid_view);
        slitherlinkView.setGridActivity(this);
        int[][] test = new int[initialSize+1][initialSize];
        gridHistory = new GridHistory<>(undoButton, redoButton, new Triplet<>(initialNumbers, test, new int[initialSize+1][initialSize]), slitherlinkView);
        // Change size

        // Exit
        Button exitButton = findViewById(R.id.slitherlink_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void isClicked(int i, int j) {
        Bundle b = new Bundle();
        b.putSerializable("i", i);
        b.putSerializable("j", j);
        new PopupDigitFragment(b, this, 4).show(getSupportFragmentManager(), "");
    }

    private int[][] numbersCopy(final int[][] numbers) {
        int[][] newGrid = new int[numbers.length][numbers[0].length];
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                newGrid[i][j] = numbers[i][j];
            }
        }
        return newGrid;
    }

    @Override
    public void callbackWithInteger(Bundle callbackBundle, int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        Triplet<int[][],int[][],int[][]> currentTriplet = gridHistory.getCurrent();
        int[][] currentNumbers = numbersCopy(currentTriplet.getValue0());
        currentNumbers[i][j] = v;
        //gridHistory.addElement(new Triplet<>(currentNumbers, currentTriplet.getValue1(), currentTriplet.getValue2()));
        Triplet<int[][],int[][],int[][]> newSlitherlinkGrid = new SlitherlinkSolver(currentNumbers).extractInformation();
        if (newSlitherlinkGrid != null) {
            gridHistory.addElement(newSlitherlinkGrid);
        }
    }
}