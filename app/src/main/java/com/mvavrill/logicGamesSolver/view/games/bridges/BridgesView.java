package com.mvavrill.logicGamesSolver.view.games.bridges;

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

import com.mvavrill.logicGamesSolver.controller.games.bridges.BridgesActivity;
import com.mvavrill.logicGamesSolver.model.cells.Cell;
import com.mvavrill.logicGamesSolver.view.games.DrawCell;
import com.mvavrill.logicGamesSolver.view.games.UpdatableView;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.javatuples.Triplet;

public class BridgesView extends View implements GestureDetector.OnGestureListener, UpdatableView<Triplet<int[][],int[][],int[][]>> {

    private BridgesActivity bridgesActivity;
    private int[][] islands;
    private int[][] horizontalEdges;
    private int[][] verticalEdges;

    private float gridWidth;
    private float gridSeparatorSize;
    private float cellSize;

    private static final float islandSizeRatio = 0.46f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GestureDetector gestureDetector;

    public BridgesView(Context context) {
        super(context);
        init();
    }
    public BridgesView(Context context, @Nullable AttributeSet attrs) {
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
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (islands == null)
            return;
        cellSize = gridWidth / islands.length;
        gridSeparatorSize = cellSize / 20f;
        paint.setTextAlign(Paint.Align.CENTER);
        drawGridLines(canvas);
        // Draw bridges
        drawBridges(canvas);
        // Draw islands
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (islands[i][j] != 0) {
                    paint.setColor(Color.BLACK);
                    canvas.drawCircle((j+0.5f)*cellSize, (i+0.5f)*cellSize, cellSize*islandSizeRatio+gridSeparatorSize, paint);
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle((j+0.5f)*cellSize, (i+0.5f)*cellSize, cellSize*islandSizeRatio, paint);
                    paint.setColor(Color.BLACK);
                    paint.setTextSize(cellSize * 0.7f);
                    canvas.drawText("" + islands[i][j], j*cellSize + cellSize / 2f, i*cellSize + cellSize * 0.75f, paint);
                }
            }
        }
    }

    public boolean isTooClose(final int i, final int j, final int[][] currentGrid) {
        return i > 0 && currentGrid[i - 1][j] != 0
                || i < currentGrid.length - 1 && currentGrid[i + 1][j] != 0
                || j > 0 && currentGrid[i][j - 1] != 0
                || j < currentGrid[i].length - 1 && currentGrid[i][j + 1] != 0;
    }

    private void drawGridLines(final Canvas canvas) {
        paint.setColor(0xFFE0E0E0);
        paint.setStrokeWidth(gridSeparatorSize);
        for (int i = 0; i <= islands.length; i++) {
            canvas.drawLine((i+0.5f)*cellSize, 0, (i+0.5f)*cellSize, gridWidth, paint);
            canvas.drawLine(0, (i+0.5f)*cellSize, gridWidth, (i+0.5f)*cellSize, paint);
        }
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (!isTooClose(i,j,islands)) {
                    paint.setColor(0xFFE0E0E0);
                    canvas.drawCircle((j + 0.5f) * cellSize, (i + 0.5f) * cellSize, cellSize * islandSizeRatio / 5 + gridSeparatorSize, paint);
                    paint.setColor(Color.WHITE);
                    canvas.drawCircle((j + 0.5f) * cellSize, (i + 0.5f) * cellSize, cellSize * islandSizeRatio / 5, paint);
                }
            }
        }
    }

    private void drawBridges(final Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(gridSeparatorSize);
        for (int i = 0; i < islands.length; i++) {
            for (int j = 0; j < islands[i].length; j++) {
                if (islands[i][j] != 0) {
                    //Horizontal
                    if (horizontalEdges[i][j] != 0) {
                        for (int k = j + 1; k < islands[i].length; k++) {
                            if (islands[i][k] != 0) {
                                float offset = 0;
                                if (horizontalEdges[i][j] == 2)
                                    offset = gridSeparatorSize;
                                canvas.drawLine((j + 0.5f) * cellSize, (i + 0.5f) * cellSize-offset, (k + 0.5f) * cellSize, (i + 0.5f) * cellSize-offset, paint);
                                canvas.drawLine((j + 0.5f) * cellSize, (i + 0.5f) * cellSize+offset, (k + 0.5f) * cellSize, (i + 0.5f) * cellSize+offset, paint);
                                break;
                            }
                        }
                    }
                    //Vertical
                    if (verticalEdges[i][j] != 0) {
                        for (int k = i + 1; k < islands.length; k++) {
                            if (islands[k][j] != 0) {
                                float offset = 0;
                                if (verticalEdges[i][j] == 2)
                                    offset = gridSeparatorSize;
                                canvas.drawLine((j + 0.5f) * cellSize-offset, (i + 0.5f) * cellSize, (j + 0.5f) * cellSize-offset, (k + 0.5f) * cellSize, paint);
                                canvas.drawLine((j + 0.5f) * cellSize+offset, (i + 0.5f) * cellSize, (j + 0.5f) * cellSize+offset, (k + 0.5f) * cellSize, paint);
                                break;
                            }
                        }
                    }
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
            int j = (int) (e.getX() / cellSize);
            int i = (int) (e.getY() / cellSize);
            bridgesActivity.isClicked(i, j);
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

    public void setGridActivity(BridgesActivity bridgesActivity) {
        this.bridgesActivity = bridgesActivity;
    }

    @Override
    public void update(Triplet<int[][],int[][],int[][]> islands) {
        this.islands = islands.getValue0();
        this.horizontalEdges = islands.getValue1();
        this.verticalEdges = islands.getValue2();
        invalidate();
    }
}
