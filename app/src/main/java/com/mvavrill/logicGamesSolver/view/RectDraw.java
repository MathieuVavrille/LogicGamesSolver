package com.mvavrill.logicGamesSolver.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class RectDraw extends View {

    private final Paint paint = new Paint();

    private float fullWidth;
    private float gridWidth;
    private float gridSeparatorSize;
    private float cellWidth;

    public RectDraw(Context context) {
        super(context);
        // create the Paint and set its color
    }
    public RectDraw(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        fullWidth = w;
        gridWidth = fullWidth*0.8f;
        cellWidth = gridWidth/3f;
        gridSeparatorSize = gridWidth / 50f;                             // Margin between two buttons

    }
    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth( gridSeparatorSize );
        float left = gridWidth*0.1f;
        float top = gridWidth*0.1f;
        float right = gridWidth*0.9f;
        float bottom = gridWidth*0.9f;
        paint.setColor( Color.BLACK );
        paint.setStrokeWidth( gridSeparatorSize );
        for( int i=0; i<=3; i++ ) {
            canvas.drawLine( i*cellWidth+fullWidth*0.1f, fullWidth*0.1f, i*cellWidth+fullWidth*0.1f, gridWidth+fullWidth*0.1f, paint);
            canvas.drawLine( fullWidth*0.1f,i*cellWidth+fullWidth*0.1f, gridWidth+fullWidth*0.1f, i*cellWidth+fullWidth*0.1f, paint);
        }
    }

}
