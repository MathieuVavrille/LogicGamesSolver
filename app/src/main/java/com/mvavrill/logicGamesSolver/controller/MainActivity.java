package com.mvavrill.logicGamesSolver.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.games.slitherlink.SlitherlinkActivity;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.games.kakuro.KakuroActivity;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;

public class MainActivity extends AppCompatActivity implements CallbackWithInteger {
    /*private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;*/

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
        /*Button test = findViewById(R.id.main_button_test);
        test.setOnClickListener(view -> {
            new PopupNumberFragment(null,this).show(getSupportFragmentManager(), "");
        });*/
        ImageButton sudokuCamera = findViewById(R.id.main_button_sudoku_camera);
        sudokuCamera.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, CameraTestActivity.class);
            startActivity(gridActivityIntent);
        });
    }

    @Override
    public void callbackWithInteger(Bundle callbackBundle, int v) {
        Log.d("Mat", "" + v);
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