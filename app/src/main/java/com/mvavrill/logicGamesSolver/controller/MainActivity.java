package com.mvavrill.logicGamesSolver.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.games.bridges.BridgesActivity;
import com.mvavrill.logicGamesSolver.controller.games.slitherlink.SlitherlinkActivity;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.games.kakuro.KakuroActivity;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;

public class MainActivity extends AppCompatActivity implements CallbackWithInteger {
    /*static{
        if(OpenCVLoader.initDebug())
            Log.d("Check","OpenCv configured successfully");
        else
            Log.d("Check","OpenCv doesn't configured successfully");
    }*/
    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    /*@Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;//super.onCreateOptionsMenu(menu);
    }*/
    //Toolbar topAppBar = (Toolbar) findViewById(R.id.main_topAppBar);
    //setSupportActionBar(topAppBar);
        /*topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sudoku = findViewById(R.id.main_button_sudoku);
        sudoku.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, SudokuActivity.class);
            startActivity(gridActivityIntent);
        });
        ImageButton sudokuCamera = findViewById(R.id.main_button_sudoku_camera);
        sudokuCamera.setOnClickListener(view -> {
            if (hasCameraPermission()) {
                Intent sudokuCameraIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, SudokuCameraActivity.class);
                startActivity(sudokuCameraIntent);
            } else {
                requestPermission();
            }
        });
        Button kakuro = findViewById(R.id.main_button_kakuro);
        kakuro.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, KakuroActivity.class);
            startActivity(gridActivityIntent);
        });
        Button slitherlink = findViewById(R.id.main_button_slitherlink);
        slitherlink.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, SlitherlinkActivity.class);
            startActivity(gridActivityIntent);
        });
        Button bridges = findViewById(R.id.main_button_bridges);
        bridges.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, BridgesActivity.class);
            startActivity(gridActivityIntent);
        });
        //Button test = findViewById(R.id.main_test);
        /*test.setOnClickListener(view -> {
            Intent sudokuCameraIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, Progress_Test.class);
            startActivity(sudokuCameraIntent);
        });*/
    }

    @Override
    public void callbackWithInteger(Bundle callbackBundle, int v) {
        Log.d("Mat", "" + v);
    }

    private boolean hasCameraPermission() {
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
        //Intent intent = new Intent(this, CameraActivity.class);
        //startActivity(intent);
    }
}