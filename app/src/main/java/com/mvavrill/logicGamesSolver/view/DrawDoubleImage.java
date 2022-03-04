package com.mvavrill.logicGamesSolver.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class DrawDoubleImage extends View {

    public boolean[][] grid;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public DrawDoubleImage(Context context) {
        super(context);
    }
    public DrawDoubleImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (grid!=null) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    paint.setColor(grid[i][j] ? Color.WHITE : Color.BLACK);
                    canvas.drawRect(2*j, 2*i, 2*j + 2, 2*i + 2, paint);
                }
            }
        }
    }
}
