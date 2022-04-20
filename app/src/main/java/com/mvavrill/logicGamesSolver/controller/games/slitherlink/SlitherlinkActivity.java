package com.mvavrill.logicGamesSolver.controller.games.slitherlink;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigits;
import com.mvavrill.logicGamesSolver.model.games.slitherlink.SlitherlinkSolver;
import com.mvavrill.logicGamesSolver.view.games.slitherlink.SlitherlinkView;

import org.javatuples.Quartet;
import org.javatuples.Triplet;

/**
 * Controller for the slitherlink game.
 * The data passed to the view contains the digits, the vertical edges, the horizontal edges and a boolean matrix to color the inside.
 */
public class SlitherlinkActivity extends AppCompatActivity implements CallbackWithInteger, UndoRedoWatcher {

    private GridHistory<Quartet<int[][],int[][],int[][],boolean[][]>> gridHistory;
    private Button minusLine;
    private Button minusCol;

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
        gridHistory = new GridHistory<>(undoButton, redoButton, new Quartet<>(initialNumbers, new int[initialSize+1][initialSize], new int[initialSize+1][initialSize], new boolean[initialSize][initialSize]), slitherlinkView, this);
        // Change size
        Button plusLine = findViewById(R.id.slitherlink_button_addLine);
        minusLine = findViewById(R.id.slitherlink_button_remLine);
        plusLine.setOnClickListener(view -> {
            int[][] newNumbers = numbersCopy(gridHistory.getCurrent().getValue0(), 1, 0);
            minusLine.setEnabled(true);
            solve(newNumbers);
        });
        minusLine.setOnClickListener(view -> {
            int[][] currentNumbers = gridHistory.getCurrent().getValue0();
            if (currentNumbers.length < 2)
                minusLine.setEnabled(false);
            else {
                int[][] newNumbers = numbersCopy(currentNumbers, -1,0);
                if (newNumbers.length < 3)
                    minusLine.setEnabled(false);
                solve(newNumbers);
            }
        });
        Button plusCol = findViewById(R.id.slitherlink_button_addCol);
        minusCol = findViewById(R.id.slitherlink_button_remCol);
        plusCol.setOnClickListener(view -> {
            int[][] newNumbers = numbersCopy(gridHistory.getCurrent().getValue0(), 0, 1);
            minusCol.setEnabled(true);
            solve(newNumbers);
        });
        minusCol.setOnClickListener(view -> {
            int[][] currentNumbers = gridHistory.getCurrent().getValue0();
            if (currentNumbers[0].length < 2)
                minusCol.setEnabled(false);
            else {
                int[][] newNumbers = numbersCopy(currentNumbers, 0,-1);
                if (newNumbers[0].length < 3)
                    minusCol.setEnabled(false);
                solve(newNumbers);
            }
        });
        // Exit
        Button exitButton = findViewById(R.id.slitherlink_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void isClicked(int i, int j) {
        Bundle b = new Bundle();
        b.putSerializable("i", i);
        b.putSerializable("j", j);
        new PopupDigits(b, this, 4).show(getSupportFragmentManager(), "");
    }

    private int[][] numbersCopy(final int[][] numbers, final int increaseI, final int increaseJ) {
        int[][] newGrid = new int[numbers.length+increaseI][numbers[0].length+increaseJ];
        for (int i = 0; i < newGrid.length; i++) {
            for (int j = 0; j < newGrid[i].length; j++) {
                if (i < numbers.length && j < numbers[i].length)
                    newGrid[i][j] = numbers[i][j];
                else
                    newGrid[i][j] = -1;
            }
        }
        return newGrid;
    }

    @Override
    public void callbackWithInteger(Bundle callbackBundle, int v) {
        Log.d("Mat","callback");
        int i = (int) callbackBundle.get("i");// TODO si ce n'est pas possible, mettre un petit message, et griser
        int j = (int) callbackBundle.get("j");
        int[][] currentNumbers = numbersCopy(gridHistory.getCurrent().getValue0(), 0, 0);
        currentNumbers[i][j] = v;
        solve(currentNumbers);
    }

    public void solve(int[][] numbers) {
        Triplet<int[][],int[][],boolean[][]> newSlitherlinkGrid = new SlitherlinkSolver(numbers).extractInformation();
        if (newSlitherlinkGrid != null) {
            gridHistory.addElement(new Quartet<>(numbers, newSlitherlinkGrid.getValue0(), newSlitherlinkGrid.getValue1(), newSlitherlinkGrid.getValue2()));
        }
    }

    @Override
    public void onUndoOrRedo(boolean isUndo) {
        int[][] grid = gridHistory.getCurrent().getValue0();
        minusLine.setEnabled(grid.length > 2);
        minusCol.setEnabled(grid[0].length > 2);
    }
}