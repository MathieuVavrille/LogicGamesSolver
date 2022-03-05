package com.mvavrill.logicGamesSolver.view.games.kakuro;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.controller.games.kakuro.KakuroActivity;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;
import com.mvavrill.logicGamesSolver.model.cells.Cell;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.model.cells.DoubleIntCell;
import com.mvavrill.logicGamesSolver.model.cells.EmptyCell;
import com.mvavrill.logicGamesSolver.view.games.DrawCell;
import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

public class KakuroView extends View implements GestureDetector.OnGestureListener, UpdatableView<Cell[][]> {

    private KakuroActivity kakuroActivity;

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    private Cell[][] grid;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GestureDetector gestureDetector;

    public KakuroView(Context context) {
        super(context);
        init();
    }

    public KakuroView(Context context, @Nullable AttributeSet attrs) {
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
        gridSeparatorSize = gridWidth / 180f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (grid == null)
            return;
        cellWidth = gridWidth / 9f;
        paint.setTextAlign(Paint.Align.CENTER);
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                DrawCell.draw(canvas, grid[y][x], x, y, cellWidth);
            }
        }
        drawGridLines(canvas);
    }

    private void drawGridLines(final Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(gridSeparatorSize);
        for (int i = 1; i <= grid.length-1; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, gridWidth, paint);
            canvas.drawLine(0, i * cellWidth, gridWidth, i * cellWidth, paint);
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
            kakuroActivity.popup(cellY, cellX);
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

    public void setGridActivity(KakuroActivity kakuroActivity) {
        this.kakuroActivity = kakuroActivity;
    }

    @Override
    public void update(Cell[][] grid) {
        this.grid = grid;
        invalidate();
    }
}