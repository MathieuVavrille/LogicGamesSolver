package com.mvavrill.logicGamesSolver.controller.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.mvavrill.logicGamesSolver.R;
import com.mvavrill.logicGamesSolver.controller.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_linear_layout, new SettingsFragment())
                .commit();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(((sharedPreferences, s) -> {
            Context context = getApplicationContext();
            CharSequence text = "You unlocked... Nothing!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();}));
        MaterialToolbar topAppBar = findViewById(R.id.settings_top_app_bar);
        topAppBar.setNavigationOnClickListener(view -> finish());
    }
}