package com.mvavrill.logicGamesSolver.game.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.view.DrawCell;
import com.mvavrill.logicGamesSolver.view.UpdatableView;

import org.javatuples.Triplet;

/**
 * Draws the Digit cells. The boolean tells if it should draw hint or not. The integer is 1 if it is solved, 2 if failed, and 0 otherwise.
 */
public class SudokuView extends View implements UpdatableView<Triplet<DigitCell[][],Boolean,Integer>> {

    private SudokuActivity sudokuActivity;

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    private DigitCell[][] grid = new DigitCell[9][9];
    private boolean drawHints = false;
    private int satisfiable = 0;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SudokuView(Context context) {
        super(context);
    }

    public SudokuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && e.getY() < gridWidth) {
            int cellX = (int) (e.getX() / cellWidth);
            int cellY = (int) (e.getY() / cellWidth);
            sudokuActivity.isClicked(cellY, cellX);
        }
        return true;
    }

    public void setGrid(final DigitCell[][] grid) {
        this.grid = grid;
        invalidate();
    }

    public void setGridActivity(SudokuActivity sudokuInputActivity) {
        this.sudokuActivity = sudokuInputActivity;
    }

    @Override
    public void update(Triplet<DigitCell[][],Boolean,Integer> grid) {
        this.grid = grid.getValue0();
        this.drawHints = grid.getValue1();
        this.satisfiable = grid.getValue2();
        invalidate();
    }
}
