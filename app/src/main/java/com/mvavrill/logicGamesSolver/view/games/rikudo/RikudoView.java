package com.mvavrill.logicGamesSolver.view.games.rikudo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.controller.games.rikudo.RikudoActivity;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.games.rikudo.RikudoGrid;
import com.mvavrill.logicGamesSolver.view.games.DrawCell;
import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

import org.javatuples.Triplet;

import java.util.List;

/**
 * Draws the Digit cells. The boolean tells if it should draw hint or not. The integer is 1 if it is solved, 2 if failed, and 0 otherwise.
 */
public class RikudoView extends View implements GestureDetector.OnGestureListener, UpdatableView<RikudoGrid<DigitCell>> {

    private RikudoActivity rikudoActivity;

    private int gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    private RikudoGrid<DigitCell> grid = RikudoGrid.generateInitialGrid();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GestureDetector gestureDetector;

    public RikudoView(Context context) {
        super(context);
        init();
    }

    public RikudoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gridWidth = w;
        cellWidth = gridWidth / (float) grid.getGrid().size();
        gridSeparatorSize = cellWidth / 20f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cellRadius = (float) Math.sqrt(cellWidth*cellWidth/3);
        paint.setTextAlign(Paint.Align.CENTER);
        List<List<DigitCell>> gridCells = grid.getGrid();
        for (int i = 0; i < gridCells.size(); i++) {
            for (int j = 0; j < gridCells.get(i).size(); j++) {
                DrawCell.drawHexagon(canvas, gridCells.get(i).get(j), j * cellWidth + cellWidth*(Math.abs(gridCells.size()/2-i)+1)/2f, 3 * i * cellRadius / 2 + cellRadius, cellWidth, gridSeparatorSize, i==gridCells.size()/2 && j == gridCells.size()/2);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        this.onSingleTapUp(motionEvent);
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        float cellRadius = (float) Math.sqrt(cellWidth*cellWidth/3);
        double minDistance = Double.POSITIVE_INFINITY;
        int bestI = 0;
        int bestJ = 0;
        List<List<DigitCell>> gridCells = grid.getGrid();
        for (int i = 0; i < gridCells.size(); i++) {
            for (int j = 0; j < gridCells.get(i).size(); j++) {
                double distance = Math.pow(j * cellWidth + cellWidth * (Math.abs(gridCells.size() / 2 - i) + 1) / 2f - e.getX(), 2) + Math.pow(3 * i * cellRadius / 2 + cellRadius - e.getY(), 2);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestI = i;
                    bestJ = j;
                }
            }
        }
        if (bestI != gridCells.size()/2 || bestJ != gridCells.size()/2) {
            rikudoActivity.isClicked(bestI, bestJ);
        }
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    /*public void setGrid(final RikudoGrid<DigitCell> grid) {
        this.grid = grid;
        invalidate();
    }*/

    public void setGridActivity(RikudoActivity rikudoInputActivity) {
        this.rikudoActivity = rikudoInputActivity;
    }

    public void update(final RikudoGrid<DigitCell> grid) {
        this.grid = grid;
        onSizeChanged(gridWidth, gridWidth, gridWidth, gridWidth);
        invalidate();
    }
}
