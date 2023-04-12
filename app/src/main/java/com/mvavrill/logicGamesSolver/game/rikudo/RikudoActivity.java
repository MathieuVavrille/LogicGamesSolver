package com.mvavrill.logicGamesSolver.game.rikudo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.integer.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.integer.PopupNumberFactory;
import com.mvavrill.logicGamesSolver.controller.popups.integer.PopupSpinner;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

import org.javatuples.Quartet;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the rikudo game.
 * The data transmitted to the view consist in three integer matrices.
 * - The first one contains the island values.
 * - The two other ones contain the rikudo values. The first one contains the horizontal rikudo, and the second one the vertical rikudo.
 */
public class RikudoActivity extends AppCompatActivity implements CallbackWithInteger, UndoRedoWatcher {

    private GridHistory<RikudoGrid<DigitCell>> gridHistory;
    private Button decreaseButton;

    private static final int minimumSize = 2;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rikudo);
        decreaseButton = findViewById(R.id.rikudo_button_decrease);
        decreaseButton.setOnClickListener(view -> {
            RikudoGrid<DigitCell> current = gridHistory.getCurrent();
            gridHistory.addElement(current.decreaseSize());
            if (gridHistory.getCurrent().getGrid().get(0).size() <= minimumSize)
                decreaseButton.setEnabled(false);
        });
        Button increaseButton = findViewById(R.id.rikudo_button_increase);
        increaseButton.setOnClickListener(view -> {
            RikudoGrid<DigitCell> current = gridHistory.getCurrent();
            gridHistory.addElement(RikudoGrid.increaseSize(current));
            if (gridHistory.getCurrent().getGrid().get(0).size() > minimumSize)
                decreaseButton.setEnabled(true);
        });
        RikudoGrid<DigitCell> initialGrid = RikudoGrid.generateInitialGrid();
        Button undoButton = findViewById(R.id.rikudo_button_undo);
        Button redoButton = findViewById(R.id.rikudo_button_redo);
        RikudoView rikudoView = findViewById(R.id.rikudo_grid_view);
        rikudoView.setGridActivity(this);
        gridHistory = new GridHistory<>(undoButton, redoButton, initialGrid, rikudoView, this);
        Button exitButton = findViewById(R.id.rikudo_button_back);
        exitButton.setOnClickListener(view -> finish());
        //rikudoView.setOnTouchListener(rikudoView);
    }

    public void isClicked(int i, int j) {
        RikudoGrid<DigitCell> current = gridHistory.getCurrent();
        RikudoGrid<Integer> integerGrid = RikudoGrid.extractFixedCells(current);
        if (integerGrid.getGrid().get(i).get(j) > 0) {
            integerGrid.getGrid().get(i).set(j, 0);
            solveAndAdd(integerGrid);
        } else if (current.getGrid().get(i).get(j).getHints() == null) {
            integerGrid.getGrid().get(i).set(j, current.getGrid().get(i).get(j).getValue());
            solveAndAdd(integerGrid);
        } else {
            Bundle b = new Bundle();
            b.putSerializable("i", i);
            b.putSerializable("j", j);
            int n = current.getGrid().get(0).size();
            boolean[] hints = current.getGrid().get(i).get(j).getHints();
            List<Integer> possibleValues = new ArrayList<>();
            for (int val = 1; val <= 3*n*(n-1); val++) {
                if (val >= hints.length || hints[val]) {
                    possibleValues.add(val);
                }
            }
            new PopupNumberFactory(b, this).valueArrayAllowedRange(possibleValues.stream().mapToInt(v -> v).toArray(), 1, 3*n*(n-1)).show(getSupportFragmentManager(), "");
        }
    }

    private void solveAndAdd(final RikudoGrid<Integer> grid) {
        List<List<DigitCell>> solvedGrid = new RikudoSolver(grid).extractInformation();
        if (solvedGrid != null) {
            gridHistory.addElement(new RikudoGrid<>(solvedGrid, grid.getFixedEdges()));
        }
    }

    private void newFixed(final int i, final int j, final int v) {
        RikudoGrid<Integer> currentGrid = RikudoGrid.extractFixedCells(gridHistory.getCurrent());
        currentGrid.getGrid().get(i).set(j,v);
        solveAndAdd(currentGrid);
    }

    public void newFixedEdge(final int si, final int sj, final int ei, final int ej) {
        if (si > ei || si == ei && sj > ej) {
            newFixedEdge(ei, ej, si, sj);
        }
        RikudoGrid<Integer> currentGrid = RikudoGrid.extractFixedCells(gridHistory.getCurrent());
        int n = currentGrid.getGrid().get(0).size();
        int lowerOffset = si >= n-1 ? -1 : 0;
        if (si == ei && sj+1 == ej ||
        si+1 == ei && sj+lowerOffset+1 == ej ||
        si+1 == ei && sj+lowerOffset == ej) {
            Quartet<Integer,Integer,Integer,Integer> edge = new Quartet<>(si, sj, ei, ej);
            if (currentGrid.getFixedEdges().contains(edge)) {
                currentGrid.getFixedEdges().remove(edge);
            }
            else {
                currentGrid.getFixedEdges().add(edge);
            }
        }
        solveAndAdd(currentGrid);
    }

    @Override
    public void onUndoOrRedo(boolean isUndo) {
        decreaseButton.setEnabled(gridHistory.getCurrent().getGrid().get(0).size() > minimumSize);
    }

    @Override
    public void callbackWithInteger(final Bundle callbackBundle, final int v) {
        int i = (int) callbackBundle.get("i");
        int j = (int) callbackBundle.get("j");
        newFixed(i, j, v);
    }
}