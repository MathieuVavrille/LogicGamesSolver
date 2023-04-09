package com.mvavrill.logicGamesSolver.game.bridges;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigits;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.Arrays;

/**
 * Controller for the bridges game.
 * The data transmitted to the view consist in three integer matrices.
 * - The first one contains the island values.
 * - The two other ones contain the bridges values. The first one contains the horizontal bridges, and the second one the vertical bridges.
 */
public class BridgesActivity extends AppCompatActivity implements CallbackWithInteger, UndoRedoWatcher {

    private GridHistory<Triplet<int[][], int[][], int[][]>> gridHistory;
    private Button decreaseButton;

    private static final int minimumSize = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridges);
        decreaseButton = findViewById(R.id.bridges_button_decrease);
        decreaseButton.setOnClickListener(view -> {
            if (gridHistory.getCurrent().getValue0().length <= minimumSize) {
                decreaseButton.setEnabled(false);
                return;
            }
            Triplet<int[][], int[][], int[][]> current = gridHistory.getCurrent();
            gridHistory.addElement(new Triplet<>(gridCopy(current.getValue0(), -1), current.getValue1(), current.getValue2()));
            if (gridHistory.getCurrent().getValue0().length <= minimumSize)
                decreaseButton.setEnabled(false);
        });
        Button increaseButton = findViewById(R.id.bridges_button_increase);
        increaseButton.setOnClickListener(view -> {
            Triplet<int[][], int[][], int[][]> current = gridHistory.getCurrent();
            gridHistory.addElement(new Triplet<>(gridCopy(current.getValue0(), 1), current.getValue1(), current.getValue2()));
            if (gridHistory.getCurrent().getValue0().length > 3)
                decreaseButton.setEnabled(true);
        });
        int[][] initialGrid = new int[7][7];
        Button undoButton = findViewById(R.id.bridges_button_undo);
        Button redoButton = findViewById(R.id.bridges_button_redo);
        BridgesView bridgesView = findViewById(R.id.bridges_grid_view);
        bridgesView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, new Triplet<>(initialGrid, new int[7][7], new int[7][7]), bridgesView, this);
        Button exitButton = findViewById(R.id.bridges_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public boolean isTooClose(final int i, final int j, final int[][] currentGrid) {
        return i > 0 && currentGrid[i - 1][j] != 0
                || i < currentGrid.length - 1 && currentGrid[i + 1][j] != 0
                || j > 0 && currentGrid[i][j - 1] != 0
                || j < currentGrid[i].length - 1 && currentGrid[i][j + 1] != 0;
    }

    public void isClicked(int i, int j) {
        int[][] grid = gridHistory.getCurrent().getValue0();
        if (grid[i][j] != 0) {
            int[][] newGrid = gridCopy(grid, 0);
            newGrid[i][j] = 0;
            solveAndAdd(newGrid);
        } else {
            if (!isTooClose(i,j,grid)) {
                boolean[] hints = new boolean[10];
                Arrays.fill(hints, true);
                for (int k = 5 + ((i == 0 || i == grid.length - 1) ? 0 : 2) + ((j == 0 || j == grid.length - 1) ? 0 : 2); k < 10; k++)
                    hints[k] = false;
                Bundle b = new Bundle();
                b.putSerializable("i", i);
                b.putSerializable("j", j);
                b.putSerializable("hints", hints);
                new PopupDigits(b, this, 8).show(getSupportFragmentManager(), "");
            }
        }
    }

    private void solveAndAdd(final int[][] grid) {
        Pair<int[][], int[][]> edges = new BridgesSolver(grid).extractInformation();
        gridHistory.addElement(new Triplet<>(grid, edges == null ? new int[grid.length][grid.length] : edges.getValue0(), edges == null ? new int[grid.length][grid.length] : edges.getValue1()));
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
        int[][] currentGrid = gridCopy(gridHistory.getCurrent().getValue0(), 0);
        currentGrid[i][j] = v;
        solveAndAdd(currentGrid);
    }

    @Override
    public void onUndoOrRedo(boolean isUndo) {
        decreaseButton.setEnabled(gridHistory.getCurrent().getValue0().length > minimumSize);
    }

    @Override
    public void callbackWithInteger(final Bundle callbackBundle, final int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        newFixed(i, j, v);
    }
}