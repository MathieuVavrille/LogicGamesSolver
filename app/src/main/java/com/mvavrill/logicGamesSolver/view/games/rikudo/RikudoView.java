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
import com.mvavrill.logicGamesSolver.view.games.DrawCell;
import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

import org.javatuples.Triplet;

/**
 * Draws the Digit cells. The boolean tells if it should draw hint or not. The integer is 1 if it is solved, 2 if failed, and 0 otherwise.
 */
public class RikudoView extends View implements GestureDetector.OnGestureListener, UpdatableView<Triplet<DigitCell[][],Boolean,Integer>> {

    private RikudoActivity rikudoActivity;

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    private DigitCell[][] grid = new DigitCell[9][9];
    private boolean drawHints = false;
    private int satisfiable = 0;

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
        gridSeparatorSize = w / 180f;
        gridWidth = w;                                  // Size of the grid (it's a square)
        cellWidth = gridWidth / 9f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setTextAlign(Paint.Align.CENTER);
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                DrawCell.draw(canvas, grid[y][x], x * cellWidth, y * cellWidth, cellWidth, drawHints, satisfiable);
            }
        }
        drawGridLines(canvas);
    }

    private void drawGridLines(final Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(gridSeparatorSize / 2);
        for (int i = 0; i <= 9; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, gridWidth, paint);
            canvas.drawLine(0, i * cellWidth, gridWidth, i * cellWidth, paint);
        }
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(gridSeparatorSize);
        for (int i = 0; i <= 3; i++) {
            canvas.drawLine(i * (cellWidth * 3), 0, i * (cellWidth * 3), gridWidth, paint);
            canvas.drawLine(0, i * (cellWidth * 3), gridWidth, i * (cellWidth * 3), paint);
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
        if (e.getY() < gridWidth) {
            int cellX = (int) (e.getX() / cellWidth);
            int cellY = (int) (e.getY() / cellWidth);
            rikudoActivity.isClicked(cellY, cellX);
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

    public void setGrid(final DigitCell[][] grid) {
        this.grid = grid;
        invalidate();
    }

    public void setGridActivity(RikudoActivity rikudoInputActivity) {
        this.rikudoActivity = rikudoInputActivity;
    }

    public void update(Triplet<DigitCell[][],Boolean,Integer> grid) {
        this.grid = grid.getValue0();
        this.drawHints = grid.getValue1();
        this.satisfiable = grid.getValue2();
        invalidate();
    }
}
