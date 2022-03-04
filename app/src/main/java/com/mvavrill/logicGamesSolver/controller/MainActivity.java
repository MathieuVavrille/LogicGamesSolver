package com.mvavrill.logicGamesSolver.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.games.kakuro.KakuroActivity;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;

public class MainActivity extends AppCompatActivity {
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    private Button sudoku;
    private Button kakuro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sudoku = findViewById(R.id.main_button_sudoku);
        sudoku.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(MainActivity.this, SudokuActivity.class);
            startActivity(gridActivityIntent);
        });
        kakuro = findViewById(R.id.main_button_kakuro);
        kakuro.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(MainActivity.this, KakuroActivity.class);
            startActivity(gridActivityIntent);
        });
    }

    /*@Override
            public void onClick(View v) {
                if (hasCameraPermission()) {
                    enableCamera();
                } else {
                    requestPermission();
                }
            }*/

    /*private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }*/
}