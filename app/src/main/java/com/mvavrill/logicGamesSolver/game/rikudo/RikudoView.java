package com.mvavrill.logicGamesSolver.game.rikudo;

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
import org.javatuples.Quartet;

import java.util.List;

/**
 * Draws the Digit cells. The boolean tells if it should draw hint or not. The integer is 1 if it is solved, 2 if failed, and 0 otherwise.
 */
public class RikudoView extends View implements UpdatableView<RikudoGrid<DigitCell>> {

    private RikudoActivity rikudoActivity;

    private int gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    private Pair<Integer,Integer> previousClicked;

    private RikudoGrid<DigitCell> grid = RikudoGrid.generateInitialGrid();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RikudoView(Context context) {
        super(context);
        init();
    }

    public RikudoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gridWidth = w;
        cellWidth = gridWidth / (float) grid.getGrid().size();
        gridSeparatorSize = cellWidth / 20f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float cellRadius = (float) Math.sqrt(cellWidth*cellWidth/3);
        paint.setTextAlign(Paint.Align.CENTER);
        List<List<DigitCell>> gridCells = grid.getGrid();
        for (int i = 0; i < gridCells.size(); i++) {
            for (int j = 0; j < gridCells.get(i).size(); j++) {
                Pair<Float,Float> pos = getPosition(i, j, gridCells.size(), cellRadius);
                DrawCell.drawHexagon(canvas, gridCells.get(i).get(j), pos.getValue0(), pos.getValue1(), cellWidth, gridSeparatorSize, i==gridCells.size()/2 && j == gridCells.size()/2);
            }
        }
        for (Quartet<Integer,Integer,Integer,Integer> edge : grid.getFixedEdges()) {
            drawFixedEdge(canvas, edge, gridCells.size(), cellRadius);
        }
    }

    private void drawFixedEdge(final Canvas canvas, final Quartet<Integer,Integer,Integer,Integer> edge, final int gridSize, final float cellRadius) {
        Pair<Float,Float> start = getPosition(edge.getValue0(), edge.getValue1(), gridSize, cellRadius);
        Pair<Float,Float> end = getPosition(edge.getValue2(), edge.getValue3(), gridSize, cellRadius);
        float si = start.getValue0();
        float sj = start.getValue1();
        float ei = end.getValue0();
        float ej = end.getValue1();
        float mi = (si+ei)/2;
        float mj = (sj+ej)/2;
        float di = (mi-si)/3.5f;
        float dj = (mj-sj)/3.5f;
        Path squarePath = new Path();
        squarePath.moveTo(mi+di, mj+dj);
        squarePath.lineTo(mi+dj, mj-di);
        squarePath.lineTo(mi-di, mj-dj);
        squarePath.lineTo(mi-dj, mj+di);
        squarePath.lineTo(mi+di, mj+dj);
        squarePath.close();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xF0F0F0F0);
        canvas.drawPath(squarePath, paint);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(gridSeparatorSize);
        canvas.drawPath(squarePath, paint);
    }

    private Pair<Float,Float> getPosition(final int i, final int j, final int gridSize, final float cellRadius) {
        return new Pair<>(j * cellWidth + cellWidth*(Math.abs(gridSize/2-i)+1)/2f, 3 * i * cellRadius / 2 + cellRadius);
    }

    private Pair<Integer,Integer> getClosestGrid(final float x, final float y) {
        float cellRadius = (float) Math.sqrt(cellWidth*cellWidth/3);
        double minDistance = Double.POSITIVE_INFINITY;
        int bestI = 0;
        int bestJ = 0;
        List<List<DigitCell>> gridCells = grid.getGrid();
        for (int i = 0; i < gridCells.size(); i++) {
            for (int j = 0; j < gridCells.get(i).size(); j++) {
                double distance = Math.pow(j * cellWidth + cellWidth * (Math.abs(gridCells.size() / 2 - i) + 1) / 2f - x, 2) + Math.pow(3 * i * cellRadius / 2 + cellRadius - y, 2);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestI = i;
                    bestJ = j;
                }
            }
        }
        if (bestI != gridCells.size()/2 || bestJ != gridCells.size()/2) {
            return new Pair<>(bestI, bestJ);
        }
        return null;
    }


    public void setGridActivity(RikudoActivity rikudoInputActivity) {
        this.rikudoActivity = rikudoInputActivity;
    }

    public void update(final RikudoGrid<DigitCell> grid) {
        this.grid = grid;
        onSizeChanged(gridWidth, gridWidth, gridWidth, gridWidth);
        invalidate();
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action==MotionEvent.ACTION_DOWN)
        {
            previousClicked = getClosestGrid(event.getX(), event.getY());
        }
        if (action==MotionEvent.ACTION_UP)
        {
            Pair<Integer,Integer> newlyClicked = getClosestGrid(event.getX(), event.getY());
            if (newlyClicked == null || previousClicked == null) { // One is the center
                return true;
            }
            if (newlyClicked.equals(previousClicked)) {
                rikudoActivity.isClicked(previousClicked.getValue0(), previousClicked.getValue1());
            }
            else {
                rikudoActivity.newFixedEdge(previousClicked.getValue0(), previousClicked.getValue1(), newlyClicked.getValue0(), newlyClicked.getValue1());
            }
        }
        return true;
    }
}
