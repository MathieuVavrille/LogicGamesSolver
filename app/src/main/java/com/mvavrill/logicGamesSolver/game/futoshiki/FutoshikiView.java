package com.mvavrill.logicGamesSolver.game.futoshiki;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.model.cells.DigitCell;
import com.mvavrill.logicGamesSolver.view.DrawCell;
import com.mvavrill.logicGamesSolver.view.UpdatableView;

import org.javatuples.Pair;

public class FutoshikiView extends View implements UpdatableView<FutoshikiGrid<DigitCell>> {

    private FutoshikiActivity futoshikiActivity;

    private int gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;


    private FutoshikiGrid<DigitCell> grid = FutoshikiGrid.generateInitialGrid();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public FutoshikiView(Context context) {
        super(context);
        init();
    }

    public FutoshikiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gridWidth = w;
        cellWidth = 2f * gridWidth / (3f * grid.getGrid().length);
        gridSeparatorSize = cellWidth / 20f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, gridWidth, gridWidth, paint);
        DigitCell[][] gridCells = grid.getGrid();
        for (int i = 0; i < gridCells.length; i++) {
            for (int j = 0; j < gridCells.length; j++) {
                DrawCell.draw(canvas, gridCells[i][j], cellWidth/4+j*3*cellWidth/2, cellWidth/4+i*3*cellWidth/2, cellWidth, false, 0);
                float xcenter = 3*cellWidth/4+j*3*cellWidth/2;
                float ycenter = 3*cellWidth/4+i*3*cellWidth/2;
                Path squarePath = new Path();
                squarePath.moveTo(xcenter - cellWidth/2, ycenter-cellWidth/2);
                squarePath.lineTo(xcenter + cellWidth/2, ycenter-cellWidth/2);
                squarePath.lineTo(xcenter + cellWidth/2, ycenter+cellWidth/2);
                squarePath.lineTo(xcenter - cellWidth/2, ycenter+cellWidth/2);
                squarePath.close();
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(gridSeparatorSize);
                canvas.drawPath(squarePath, paint);
            }
        }
        int[][] lineIneq = grid.getLineIneq();
        for (int i = 0; i < gridCells.length; i++) {
            for (int j = 0; j < gridCells.length-1; j++) {
                drawInequality(canvas, i, j, lineIneq[i][j]);
            }
        }
        int[][] columnIneq = grid.getColumnIneq();
        for (int i = 0; i < gridCells.length-1; i++) {
            for (int j = 0; j < gridCells.length; j++) {
                drawInequality(canvas, i, j, columnIneq[i][j]);
            }
        }
    }

    private void drawInequality(final Canvas canvas, final int i, final int j, final int ineq) {

    }

    public void setGridActivity(FutoshikiActivity futoshikiInputActivity) {
        this.futoshikiActivity = futoshikiInputActivity;
    }

    public void update(final FutoshikiGrid<DigitCell> grid) {
        this.grid = grid;
        onSizeChanged(gridWidth, gridWidth, gridWidth, gridWidth);
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && e.getY() < gridWidth) {
            int cellX = (int) (e.getX() / (gridWidth/grid.getGrid().length));
            int cellY = (int) (e.getY() / (gridWidth/grid.getGrid().length));
            futoshikiActivity.isClicked(cellY, cellX);
        }
        return true;
    }
}
