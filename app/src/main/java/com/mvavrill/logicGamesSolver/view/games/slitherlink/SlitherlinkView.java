package com.mvavrill.logicGamesSolver.view.games.slitherlink;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.mvavrill.logicGamesSolver.controller.games.slitherlink.SlitherlinkActivity;
import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

import org.javatuples.Triplet;

public class SlitherlinkView extends View implements GestureDetector.OnGestureListener, UpdatableView<Triplet<int[][],int[][],int[][]>> {

    private SlitherlinkActivity slitherlinkActivity;

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;
    private float gridOffset;

    private int[][] numbers; // -1 for don't know, 0-3 for value
    private int[][] verticalEdges; // -1 for no edge, 1 for edge, 0 for don't know
    private int[][] horizontalEdges;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GestureDetector gestureDetector;

    public SlitherlinkView(Context context) {
        super(context);
        init();
    }

    public SlitherlinkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gridWidth = w*0.94f; // TODO improve the grid offset, using the size of the dots
        gridSeparatorSize = gridWidth / 180f;
        gridOffset = w*0.03f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (numbers == null)
            return;
        cellWidth = gridWidth / numbers.length;
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, gridWidth + gridOffset*2, numbers.length * cellWidth + 2*gridOffset, paint);
        drawNumbers(canvas);
        drawGridLines(canvas);
        drawVerticalEdges(canvas);
        drawHorizontalEdges(canvas);
    }

    private void drawNumbers(final Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(cellWidth*0.75f);
        for (int i = 0; i < numbers.length; i++) {
            for (int j = 0; j < numbers[i].length; j++) {
                if (numbers[i][j] != -1)
                    canvas.drawText("" + numbers[i][j], (2*j+1) * cellWidth / 2 + gridOffset, i * cellWidth + cellWidth*0.75f + gridOffset, paint);
            }
        }
    }

    private void drawGridLines(final Canvas canvas) {
        paint.setColor(Color.BLACK);
        for (int i = 0; i <= numbers.length; i++) {
            for (int j = 0; j <= numbers[0].length; j++) {
                canvas.drawCircle(j*cellWidth + gridOffset, i*cellWidth + gridOffset, cellWidth/10, paint);
            }
        }
    }

    private void drawVerticalEdges(final Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(cellWidth/20f);
        for (int i = 0; i < verticalEdges.length; i++) {
            for (int j = 0; j < verticalEdges[i].length; j++) {
                if (verticalEdges[i][j] == 1) {
                    canvas.drawLine(i * cellWidth + gridOffset, j * cellWidth + gridOffset, i * cellWidth + gridOffset, (j + 1) * cellWidth + gridOffset, paint);
                }
                else if (verticalEdges[i][j] == -1) {
                    float x = i*cellWidth+gridOffset;
                    float y = (j+0.5f)*cellWidth+gridOffset;
                    canvas.drawLine(x-cellWidth/10, y-cellWidth/10, x+cellWidth/10, y+cellWidth/10, paint);
                    canvas.drawLine(x+cellWidth/10, y-cellWidth/10, x-cellWidth/10, y+cellWidth/10, paint);
                }
            }
        }
    }
    private void drawHorizontalEdges(final Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(cellWidth/20f);
        for (int i = 0; i < horizontalEdges.length; i++) {
            for (int j = 0; j < horizontalEdges[i].length; j++) {
                if (horizontalEdges[i][j] == 1) {
                    canvas.drawLine(j * cellWidth + gridOffset, i * cellWidth + gridOffset, (j+1) * cellWidth + gridOffset, i * cellWidth + gridOffset, paint);
                }
                else if (horizontalEdges[i][j] == -1) {
                    float x = (j+0.5f)*cellWidth+gridOffset;
                    float y = i*cellWidth+gridOffset;
                    canvas.drawLine(x-cellWidth/10, y-cellWidth/10, x+cellWidth/10, y+cellWidth/10, paint);
                    canvas.drawLine(x+cellWidth/10, y-cellWidth/10, x-cellWidth/10, y+cellWidth/10, paint);
                }
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
        if (e.getY() < gridWidth) {
            int j = (int) ((e.getX()-gridOffset) / cellWidth);
            int i = (int) ((e.getY()-gridOffset) / cellWidth);
            slitherlinkActivity.isClicked(i, j);
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

    public void setGridActivity(SlitherlinkActivity slitherlinkActivity) {
        this.slitherlinkActivity = slitherlinkActivity;
    }

    @Override
    public void update(final Triplet<int[][],int[][],int[][]> newData) {
        this.numbers = newData.getValue0();
        this.verticalEdges = newData.getValue1();
        this.horizontalEdges = newData.getValue2();
        invalidate();
    }
}
