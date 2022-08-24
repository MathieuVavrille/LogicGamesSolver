package com.mvavrill.logicGamesSolver.controller.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.mvavrill.logicGamesSolver.R;

/**
 * Mainly deals with the information menu to open urls.
 */
public class InformationActivity extends AppCompatActivity {

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.donate_github, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.information_donate:
                Intent donationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.buymeacoffee.com/tiouz"));
                startActivity(donationIntent);
                return true;
            case R.id.information_github:
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MathieuVavrille/LogicGamesSolver"));
                startActivity(githubIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        // Toolbar
        MaterialToolbar topAppBar = findViewById(R.id.information_top_app_bar);
        setSupportActionBar(topAppBar);
        topAppBar.setNavigationOnClickListener(view -> finish());
    }
}