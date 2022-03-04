package com.mvavrill.logicGamesSolver.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.view.DrawDoubleImage;

public class DrawImageActivity extends AppCompatActivity {

    private DrawDoubleImage drawImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate of GridActivity", "start");
        setContentView(R.layout.activity_draw_image);
        boolean[][] grid = (boolean[][]) getIntent().getSerializableExtra("grid");
        drawImage = findViewById(R.id.draw_image_activity_draw_image);
        Log.d("Mat",""+(drawImage==null));
        drawImage.grid = grid;
        drawImage.invalidate();
    }
}