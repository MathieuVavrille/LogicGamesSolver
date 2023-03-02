package com.mvavrill.logicGamesSolver.controller.games.rikudo;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.UndoRedoWatcher;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupNumberFragment;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.rikudo.RikudoGrid;
import com.mvavrill.logicGamesSolver.model.games.rikudo.RikudoSolver;
import com.mvavrill.logicGamesSolver.view.games.rikudo.RikudoView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rikudo);
        decreaseButton = findViewById(R.id.rikudo_button_decrease);
        decreaseButton.setOnClickListener(view -> {
            /*if (gridHistory.getCurrent().getGrid().get(0).size() <= minimumSize + 1) {
                decreaseButton.setEnabled(false);
                return;
            }*/
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
    }

    public void isClicked(int i, int j) {
        Log.d("Mat", "Clicked " + i + " " + j);
        /*RikudoGrid<DigitCell> current = gridHistory.getCurrent();
        RikudoGrid<Integer> integerGrid = RikudoGrid.extractCells(current);
        if (integerGrid.getGrid().get(i).get(j) != 0) {
            integerGrid.getGrid().get(i).set(j, 0);
            solveAndAdd(integerGrid);
        } else {
            Bundle b = new Bundle();
            b.putSerializable("i", i);
            b.putSerializable("j", j);
            new PopupNumberFragment(b, this).show(getSupportFragmentManager(), "");
        }*/
    }

    private void solveAndAdd(final RikudoGrid<Integer> grid) {
        List<List<DigitCell>> solvedGrid = new RikudoSolver(grid).extractInformation();
        if (solvedGrid != null) {
            gridHistory.addElement(new RikudoGrid<>(solvedGrid, grid.getFixedEdges()));
        }
    }

    private void newFixed(final int i, final int j, final int v) {
        RikudoGrid<Integer> currentGrid = RikudoGrid.extractCells(gridHistory.getCurrent());
        currentGrid.getGrid().get(i).set(j,v);
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