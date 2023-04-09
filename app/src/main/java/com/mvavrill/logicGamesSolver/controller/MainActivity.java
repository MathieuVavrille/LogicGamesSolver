package com.mvavrill.logicGamesSolver.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.games.bridges.BridgesActivity;
import com.mvavrill.logicGamesSolver.controller.games.kakuro.KakuroActivity;
import com.mvavrill.logicGamesSolver.controller.games.rikudo.RikudoActivity;
import com.mvavrill.logicGamesSolver.controller.games.slitherlink.SlitherlinkActivity;
import com.mvavrill.logicGamesSolver.controller.games.sudoku.SudokuActivity;
import com.mvavrill.logicGamesSolver.controller.menu.InformationActivity;
import com.mvavrill.logicGamesSolver.controller.menu.SettingsActivity;
import com.mvavrill.logicGamesSolver.controller.popups.CallbackWithInteger;
import com.mvavrill.logicGamesSolver.controller.popups.PopupDigits;
import com.mvavrill.logicGamesSolver.controller.popups.PopupSpinner;

public class MainActivity extends AppCompatActivity implements CallbackWithInteger {

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Toolbar
        Toolbar topAppBar = (Toolbar) findViewById(R.id.main_top_app_bar);
        setSupportActionBar(topAppBar);

        // Buttons
        Button sudoku = findViewById(R.id.main_button_sudoku);
        sudoku.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, SudokuActivity.class);
            startActivity(gridActivityIntent);
        });
        ImageButton sudokuCamera = (ImageButton) findViewById(R.id.main_button_sudoku_camera);
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
        Button rikudo = findViewById(R.id.main_button_rikudo);
        rikudo.setOnClickListener(view -> {
            Intent gridActivityIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, RikudoActivity.class);
            startActivity(gridActivityIntent);
        });
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

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.donate_about_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.top_bar_donate:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/tiouz"));
                startActivity(browserIntent);
                return true;
            case R.id.top_bar_about:
                Intent aboutIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, InformationActivity.class);
                startActivity(aboutIntent);
                return true;
            case R.id.top_bar_settings:
                Intent settingsIntent = new Intent(com.mvavrill.logicGamesSolver.controller.MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void callbackWithInteger(Bundle callbackBundle, int v) {

    }
}