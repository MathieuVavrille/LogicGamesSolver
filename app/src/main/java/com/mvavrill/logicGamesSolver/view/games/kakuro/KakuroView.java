package com.mvavrill.logicGamesSolver.view.games.kakuro;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.controller.games.kakuro.KakuroActivity;
import com.mvavrill.logicGamesSolver.model.cells.Cell;
import com.mvavrill.logicGamesSolver.model.cells.EmptyCell;
import com.mvavrill.logicGamesSolver.view.games.DrawCell;
import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

public class KakuroView extends View implements UpdatableView<Cell[][]> {

    private KakuroActivity kakuroActivity;

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    private Cell[][] grid;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public KakuroView(Context context) {
        super(context);
    }

    public KakuroView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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
        cellWidth = gridWidth / grid.length;
        paint.setTextAlign(Paint.Align.CENTER);
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                DrawCell.draw(canvas, grid[y][x], x*cellWidth, y*cellWidth, cellWidth);
            }
        }
        drawGridLines(canvas);
    }

    private void drawGridLines(final Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(gridSeparatorSize);
        for (int i = 1; i <= grid.length; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, gridWidth, paint);
            canvas.drawLine(0, i * cellWidth, gridWidth, i * cellWidth, paint);
        }
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(gridSeparatorSize/2);
        for (int i = 1; i < grid.length; i++) {
            for (int j = 1; j < grid[i].length; j++) {
                if (grid[i-1][j-1] instanceof EmptyCell
                        && grid[i-1][j] instanceof EmptyCell
                        && grid[i][j-1] instanceof EmptyCell
                        && grid[i][j] instanceof EmptyCell) {
                    canvas.drawLine(j * cellWidth, i*cellWidth-cellWidth/9, j * cellWidth, i*cellWidth+cellWidth/9, paint);
                    canvas.drawLine(j*cellWidth-cellWidth/9, i * cellWidth, j*cellWidth+cellWidth/9, i * cellWidth, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && e.getY() < gridWidth) {
            int j = (int) (e.getX() / cellWidth);
            int i = (int) (e.getY() / cellWidth);
            float cellX = j*cellWidth;
            float cellY = i*cellWidth;
            kakuroActivity.isClicked(i, j, Math.abs(e.getX()-cellX-cellWidth)+Math.abs(e.getY()-cellY) < Math.abs(e.getX()-cellX)+Math.abs(e.getY()-cellY-cellWidth));
        }
        return true;
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
