package com.mvavrill.logicGamesSolver.view.games.kakuro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;
import com.mvavrill.logicGamesSolver.model.cells.DigitCell;

public class KakuroView extends View implements GestureDetector.OnGestureListener {

    private SudokuActivity sudokuInputActivity;

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    private DigitCell[][] grid;

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
        gestureDetector = new GestureDetector( getContext(),  this );
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gridSeparatorSize = w/180f;
        gridWidth = w;                                  // Size of the grid (it's a square)
        cellWidth = gridWidth / 9f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (grid != null) {
            paint.setTextAlign(Paint.Align.CENTER);
            for (int y = 0; y < 9; y++) {
                for (int x = 0; x < 9; x++) {
                    drawCellBackground(canvas, grid[y][x], x * cellWidth, y * cellWidth);
                    if (grid != null)
                        drawCell(canvas, grid[y][x], x * cellWidth, y * cellWidth);
                }
            }
            drawGridLines(canvas);
        }
    }

    private void drawCellBackground(final Canvas canvas, final DigitCell cell, final float x, final float y) {
        paint.setColor(Color.WHITE);
        if (cell != null && cell.isFixed()) {
            paint.setColor(0xF0F0F0F0);
        }
        canvas.drawRect(x, y , x + cellWidth, y + cellWidth, paint);
    }

    private void drawCell(final Canvas canvas, final DigitCell cell, final float x, final float y) {
        if (cell != null) {
            if (cell.getHints() != null) {
                boolean[] hints = cell.getHints();
                paint.setColor(0xFFC0C0C0);
                paint.setTextSize(cellWidth * 0.7f / 3f);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (hints[3*i+j]) {
                            canvas.drawText("" + (3 * i + j + 1), x + (2 * j + 1) * cellWidth / 6f, y + 2 * i * cellWidth / 6f + cellWidth / 6f / 0.75f, paint);
                        }
                    }
                }
            } else {
                paint.setColor(0xFF000000);
                paint.setTextSize(cellWidth * 0.7f);
                canvas.drawText("" + (cell.getValue() + 1),
                        x + cellWidth / 2,
                        y + cellWidth * 0.75f, paint);
            }
        }
    }

    private void drawGridLines(final Canvas canvas) {
        paint.setColor( Color.GRAY );
        paint.setStrokeWidth( gridSeparatorSize/2 );
        for( int i=0; i<=9; i++ ) {
            canvas.drawLine( i*cellWidth, 0, i*cellWidth, cellWidth*9, paint );
            canvas.drawLine( 0,i*cellWidth, cellWidth*9, i*cellWidth, paint );
        }
        paint.setColor( Color.BLACK );
        paint.setStrokeWidth( gridSeparatorSize );
        for( int i=0; i<=3; i++ ) {
            canvas.drawLine( i*(cellWidth*3), 0, i*(cellWidth*3), cellWidth*9, paint );
            canvas.drawLine( 0,i*(cellWidth*3), cellWidth*9, i*(cellWidth*3), paint );
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
            sudokuInputActivity.popup(cellY,cellX);
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

    public void setGridActivity(SudokuActivity sudokuInputActivity) {
        this.sudokuInputActivity = sudokuInputActivity;
    }
}
