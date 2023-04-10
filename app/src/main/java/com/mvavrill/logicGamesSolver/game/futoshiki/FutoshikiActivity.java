package com.mvavrill.logicGamesSolver.game.futoshiki;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupSpinner;
import com.mvavrill.logicGamesSolver.game.futoshiki.FutoshikiGrid;
import com.mvavrill.logicGamesSolver.game.futoshiki.FutoshikiSolver;
import com.mvavrill.logicGamesSolver.game.futoshiki.FutoshikiView;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FutoshikiActivity extends AppCompatActivity implements CallbackWithInteger, UndoRedoWatcher {

    private GridHistory<FutoshikiGrid<DigitCell>> gridHistory;
    private Button decreaseButton;

    private static final int minimumSize = 2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_futoshiki);
        decreaseButton = findViewById(R.id.futoshiki_button_decrease);
        decreaseButton.setOnClickListener(view -> {
            FutoshikiGrid<DigitCell> current = gridHistory.getCurrent();
            gridHistory.addElement(FutoshikiGrid.changeSize(current, -1));
            if (gridHistory.getCurrent().getGrid().length <= minimumSize)
                decreaseButton.setEnabled(false);
        });
        Button increaseButton = findViewById(R.id.futoshiki_button_increase);
        increaseButton.setOnClickListener(view -> {
            FutoshikiGrid<DigitCell> current = gridHistory.getCurrent();
            gridHistory.addElement(FutoshikiGrid.changeSize(current, 1));
            if (gridHistory.getCurrent().getGrid().length > minimumSize)
                decreaseButton.setEnabled(true);
        });
        FutoshikiGrid<DigitCell> initialGrid = FutoshikiGrid.generateInitialGrid();
        Button undoButton = findViewById(R.id.futoshiki_button_undo);
        Button redoButton = findViewById(R.id.futoshiki_button_redo);
        FutoshikiView futoshikiView = findViewById(R.id.futoshiki_grid_view);
        futoshikiView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, initialGrid, futoshikiView, this);
        Button exitButton = findViewById(R.id.futoshiki_button_back);
        exitButton.setOnClickListener(view -> finish());
        //futoshikiView.setOnTouchListener(futoshikiView);
    }

    public void isClicked(int i, int j) {
        System.out.println("isClicked " + i + " " + j);
        FutoshikiGrid<DigitCell> current = gridHistory.getCurrent();
        FutoshikiGrid<Integer> integerGrid = FutoshikiGrid.extractFixedCells(current);
        if (integerGrid.getGrid()[i][j] > 0) {
            integerGrid.getGrid()[i][j] = 0;
            solveAndAdd(integerGrid);
        } else {
            Bundle b = new Bundle();
            b.putSerializable("i", i);
            b.putSerializable("j", j);
            int n = current.getGrid().length;
            boolean[] hints = current.getGrid()[i][j].getHints();
            List<Integer> possibleValues = new ArrayList<>();
            for (int val = 1; val <= n; val++) {
                if (val >= hints.length || hints[val]) {
                    possibleValues.add(val);
                }
            }
            Log.d("LogMat", "test " + possibleValues);
            Log.d("LogMat", Arrays.toString(hints));
            new PopupSpinner(b, this, this.getBaseContext(), possibleValues.stream().mapToInt(v -> v).toArray()).show(getSupportFragmentManager(), "");
        }
    }

    private void solveAndAdd(final FutoshikiGrid<Integer> grid) {
        DigitCell[][] solvedGrid = new FutoshikiSolver(grid).extractInformation();
        if (solvedGrid != null) {
            gridHistory.addElement(new FutoshikiGrid<>(solvedGrid, grid.getLineIneq(), grid.getColumnIneq()));
        }
    }

    private void newFixed(final int i, final int j, final int v) {
        FutoshikiGrid<Integer> currentGrid = FutoshikiGrid.extractFixedCells(gridHistory.getCurrent());
        currentGrid.getGrid()[i][j] = v;
        solveAndAdd(currentGrid);
    }

    @Override
    public void onUndoOrRedo(boolean isUndo) {
        decreaseButton.setEnabled(gridHistory.getCurrent().getGrid().length > minimumSize);
    }

    @Override
    public void callbackWithInteger(final Bundle callbackBundle, final int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        newFixed(i, j, v);
    }
}
