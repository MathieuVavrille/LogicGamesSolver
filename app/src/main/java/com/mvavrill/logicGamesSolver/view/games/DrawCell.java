package com.mvavrill.logicGamesSolver.view.games;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.mvavrill.logicGamesSolver.model.cells.*;

public class DrawCell {

    private final static Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    static {
        paint.setTextAlign(Paint.Align.CENTER);
    }

    public static void drawWhiteBackground(final Canvas canvas, final float x, final float y, final float cellWidth) {
        drawBackground(canvas, x, y, cellWidth, Color.WHITE);
    }
    public static void drawBlackBackground(final Canvas canvas, final float x, final float y, final float cellWidth) {
        drawBackground(canvas, x, y, cellWidth, Color.BLACK);
    }
    public static void drawBackground(final Canvas canvas, final float x, final float y, final float cellWidth, final int color) {
        paint.setColor(color);
        canvas.drawRect(x, y, x + cellWidth, y + cellWidth, paint);
    }

    public static void draw(final Canvas canvas, final DigitCell cell, final float x, final float y, final float cellWidth) {
        draw(canvas, cell, x, y, cellWidth, true, true);
    }

    public static void draw(final Canvas canvas, final DigitCell cell, final float x, final float y, final float cellWidth, final boolean satisfiable, final boolean drawHints) {
        if (cell == null)
            return;
        if (cell.getHints() != null) {
            drawWhiteBackground(canvas, x, y, cellWidth);
            if (drawHints) {
                boolean[] hints = cell.getHints();
                paint.setColor(0xFFC0C0C0);
                paint.setTextSize(cellWidth / 4f);
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (hints[3 * i + j + 1]) {
                            canvas.drawText("" + (3 * i + j + 1), x + (2 * j + 1) * cellWidth / 6f, y + (2 * i + 1) * cellWidth / 6f + cellWidth / 12, paint);
                        }
                    }
                }
            }
        } else {
            if (cell.isFixed())
                drawBackground(canvas,x,y,cellWidth,satisfiable? 0xFFF0F0F0 : 0xFFFFE0E0);
            else
                drawWhiteBackground(canvas,x,y,cellWidth);
            paint.setColor(Color.BLACK);
            paint.setTextSize(cellWidth * 0.7f);
            if (cell.isFixed() || drawHints)
                canvas.drawText("" + cell.getValue(), x + cellWidth / 2f - cellWidth/30f, y + cellWidth * 0.75f, paint);
        }
    }

    public static void draw(final Canvas canvas, final EmptyCell cell, final float x, final float y, final float cellWidth) {
        if (cell == null)
            return;
        drawBlackBackground(canvas, x, y, cellWidth);
    }

    public static void draw(final Canvas canvas, final DoubleIntCell cell, final float x, final float y, final float cellWidth) {
        if (cell == null)
            return;
        drawBlackBackground(canvas, x, y, cellWidth);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(cellWidth/40f);
        canvas.drawLine(x,y,x+cellWidth, y+cellWidth, paint);
        paint.setTextSize(cellWidth / 4f);
        if (cell.getHint1() != null)
            canvas.drawText(cell.getHint1() == -1 ? "X" : ""+cell.getHint1(), x+cellWidth*0.75f, y+cellWidth*0.375f, paint);
        if (cell.getHint2() != null)
            canvas.drawText(cell.getHint2() == -1 ? "X" : ""+cell.getHint2(), x+cellWidth/4f, y+cellWidth*0.875f, paint);
    }

    public static void draw(final Canvas canvas, final Cell cell, final float x, final float y, final float cellWidth) {
        if (cell == null)
            return;
        if (cell instanceof DigitCell)
            draw(canvas, (DigitCell) cell, x, y, cellWidth);
        else if (cell instanceof DoubleIntCell)
            draw(canvas, (DoubleIntCell) cell, x, y, cellWidth);
        else if (cell instanceof EmptyCell)
            draw(canvas, (EmptyCell) cell, x, y, cellWidth);
    }
}
