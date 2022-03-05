package com.mvavrill.logicGamesSolver.controller.games.kakuro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.Button;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.GridHistory;
import com.mvavrill.logicGamesSolver.controller.PopupCallback;
import com.mvavrill.logicGamesSolver.controller.PopupDigit;
import com.mvavrill.logicGamesSolver.model.cells.Cell;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.cells.EmptyCell;
import com.mvavrill.logicGamesSolver.view.games.kakuro.KakuroView;

public class KakuroActivity extends AppCompatActivity implements PopupCallback {

    private KakuroView kakuroView;
    private ConstraintLayout gridConstraintLayout;

    private GridHistory<Cell[][]> gridHistory;
    private Button addColumnButton;
    private Button addLineButton;
    private Button outlineButton;
    private boolean isOutline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakuro);
        gridConstraintLayout = findViewById(R.id.kakuro_constraint_layout);
        addColumnButton = findViewById(R.id.kakuro_button_add_column);
        addLineButton = findViewById(R.id.kakuro_button_add_line);
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
        gridHistory = new GridHistory<Cell[][]>(undoButton, redoButton, initialGrid, kakuroView);
        Button exitButton = findViewById(R.id.kakuro_button_back);
        exitButton.setOnClickListener(view -> finish());
    }

    public void popup(int i, int j) {
        new PopupDigit(i,j,this).run();
    }

    @Override
    public void callback(int i, int j, int v) {
    }

    @Override
    public ConstraintLayout getGridConstraintLayout() {
        return gridConstraintLayout;
    }
}