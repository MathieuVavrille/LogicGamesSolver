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

    private Pair<Integer,Integer> previousClicked;
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
                DrawCell.draw(canvas, gridCells[i][j], cellWidth/4+j*3*cellWidth/2, cellWidth/4+i*3*cellWidth/2, cellWidth, true, 0);
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
                drawInequality(canvas, (j+1)*3*cellWidth/2, i*3*cellWidth/2+3*cellWidth/4, true, lineIneq[i][j]);
            }
        }
        int[][] columnIneq = grid.getColumnIneq();
        for (int i = 0; i < gridCells.length-1; i++) {
            for (int j = 0; j < gridCells.length; j++) {
                drawInequality(canvas, j*3*cellWidth/2+3*cellWidth/4, (i+1)*3*cellWidth/2, false, columnIneq[i][j]);
            }
        }
    }

    private void drawInequality(final Canvas canvas, final float x, final float y, final boolean isLine, final int ineq) {
        if (ineq != 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(gridSeparatorSize);
            Path ineqPath = new Path();
            if (isLine) {
                ineqPath.moveTo(x+ineq*cellWidth/8, y-cellWidth/4);
                ineqPath.lineTo(x-ineq*cellWidth/8, y);
                ineqPath.lineTo(x+ineq*cellWidth/8, y+cellWidth/4);
            }
            else {
                ineqPath.moveTo(x-cellWidth/4, y+ineq*cellWidth/8);
                ineqPath.lineTo(x, y-ineq*cellWidth/8);
                ineqPath.lineTo(x+cellWidth/4, y+ineq*cellWidth/8);
            }
            canvas.drawPath(ineqPath, paint);
        }
    }

    public void setGridActivity(FutoshikiActivity futoshikiInputActivity) {
        this.futoshikiActivity = futoshikiInputActivity;
    }

    public void update(final FutoshikiGrid<DigitCell> grid) {
        this.grid = grid;
        onSizeChanged(gridWidth, gridWidth, gridWidth, gridWidth);
        invalidate();
    }

    private Pair<Integer,Integer> getPosClicked(final float x, final float y) {
        return new Pair<>((int) (y/(3*cellWidth/2)), (int) (x/(3*cellWidth/2)));
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action==MotionEvent.ACTION_DOWN)
        {
            previousClicked = getPosClicked(event.getX(), event.getY());
        }
        if (action==MotionEvent.ACTION_UP)
        {
            Pair<Integer,Integer> newlyClicked = getPosClicked(event.getX(), event.getY());
            if (newlyClicked.equals(previousClicked)) {
                futoshikiActivity.isClicked(previousClicked.getValue0(), previousClicked.getValue1());
            }
            else {
                futoshikiActivity.isClickedIneq(previousClicked.getValue0(), previousClicked.getValue1(), newlyClicked.getValue0(), newlyClicked.getValue1());
            }
        }
        return true;
    }
}
