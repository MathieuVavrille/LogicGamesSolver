package com.mvavrill.logicGamesSolver.view.games.slitherlink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.controller.games.slitherlink.SlitherlinkActivity;
import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

import org.javatuples.Quartet;

public class SlitherlinkView extends View implements UpdatableView<Quartet<int[][],int[][],int[][],boolean[][]>> {

    private SlitherlinkActivity slitherlinkActivity;

    private float maxWidth;
    private float maxHeight;
    private float cellSize;
    private float gridOffset;
    private static final float factorDotRadius = 0.1f;
    private static final float factorLineSize = 0.05f;

    private int[][] numbers; // -1 for don't know, 0-3 for value
    private int[][] verticalEdges; // -1 for no edge, 1 for edge, 0 for don't know
    private int[][] horizontalEdges;
    private boolean[][] isInside;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public SlitherlinkView(Context context) {
        super(context);
    }

    public SlitherlinkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        maxWidth = w;
        maxHeight = h;
        Log.d("Mat",maxHeight+"");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initializeSizes();
        if (numbers == null)
            return;
        paint.setColor(Color.WHITE);
        drawBackground(canvas);
        drawNumbers(canvas);
        drawGridDots(canvas);
        drawVerticalEdges(canvas);
        drawHorizontalEdges(canvas);
    }

    private void initializeSizes() {
        float cellWidth = maxWidth / (2*factorDotRadius+numbers[0].length);
        float cellHeight = maxHeight / (2*factorDotRadius+numbers.length);
        cellSize = Math.min(cellWidth,cellHeight);
        gridOffset = cellSize*factorDotRadius;
    }

    private void drawBackground(final Canvas canvas) {
        for (int i = 0; i < isInside.length; i++) {
            for (int j = 0; j < isInside[i].length; j++) {
                if (isInside[i][j])
                    paint.setColor(0xD0D0D0D0);
                else
                    paint.setColor(Color.WHITE);
                canvas.drawRect(gridOffset + j*cellSize, gridOffset + i*cellSize, gridOffset + (j+1)*cellSize, gridOffset + (i+1)*cellSize, paint);
            }
        }
    }

    private void drawNumbers(final Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellSize*0.75f);
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                if (numbers[i][j] != -1)
                    canvas.drawText("" + numbers[i][j], (2*j+1) * cellSize / 2 + gridOffset, i * cellSize + cellSize*0.75f + gridOffset, paint);
            }
        }
    }

    private void drawGridDots(final Canvas canvas) {
        paint.setColor(Color.BLACK);
        for (int i = 0; i <= numbers.length; i++) {
            for (int j = 0; j <= numbers[0].length; j++) {
                canvas.drawCircle(j*cellSize + gridOffset, i*cellSize + gridOffset, cellSize*factorDotRadius, paint);
            }
        }
    }

    private void drawVerticalEdges(final Canvas canvas) {
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(cellSize*factorLineSize);
        for (int i = 0; i < verticalEdges.length; i++) {
            for (int j = 0; j < verticalEdges[i].length; j++) {
                if (verticalEdges[i][j] == 1) {
                    canvas.drawLine(i * cellSize + gridOffset, j * cellSize + gridOffset, i * cellSize + gridOffset, (j + 1) * cellSize + gridOffset, paint);
                }
                else if (verticalEdges[i][j] == -1) {
                    float x = i*cellSize+gridOffset;
                    float y = (j+0.5f)*cellSize+gridOffset;
                    canvas.drawLine(x-cellSize/10, y-cellSize/10, x+cellSize/10, y+cellSize/10, paint);
                    canvas.drawLine(x+cellSize/10, y-cellSize/10, x-cellSize/10, y+cellSize/10, paint);
                }
            }
        }
    }
    private void drawHorizontalEdges(final Canvas canvas) {
        paint.setColor(Color.DKGRAY);
        paint.setStrokeWidth(cellSize/20f);
        for (int i = 0; i < horizontalEdges.length; i++) {
            for (int j = 0; j < horizontalEdges[i].length; j++) {
                if (horizontalEdges[i][j] == 1) {
                    canvas.drawLine(j * cellSize + gridOffset, i * cellSize + gridOffset, (j+1) * cellSize + gridOffset, i * cellSize + gridOffset, paint);
                }
                else if (horizontalEdges[i][j] == -1) {
                    float x = (j+0.5f)*cellSize+gridOffset;
                    float y = i*cellSize+gridOffset;
                    canvas.drawLine(x-cellSize/10, y-cellSize/10, x+cellSize/10, y+cellSize/10, paint);
                    canvas.drawLine(x+cellSize/10, y-cellSize/10, x-cellSize/10, y+cellSize/10, paint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN && gridOffset <= e.getY() && e.getY() < maxHeight && gridOffset < e.getX() && e.getX() < maxWidth) {
            int j = (int) ((e.getX() - gridOffset) / cellSize);
            int i = (int) ((e.getY() - gridOffset) / cellSize);
            slitherlinkActivity.isClicked(i, j);
        }
        return true;
    }

    public void setGridActivity(SlitherlinkActivity slitherlinkActivity) {
        this.slitherlinkActivity = slitherlinkActivity;
    }

    @Override
    public void update(final Quartet<int[][],int[][],int[][],boolean[][]> newData) {
        this.numbers = newData.getValue0();
        this.verticalEdges = newData.getValue1();
        this.horizontalEdges = newData.getValue2();
        this.isInside = newData.getValue3();
        invalidate();
    }
}
