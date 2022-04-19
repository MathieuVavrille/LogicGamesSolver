package com.mvavrill.logicGamesSolver.controller.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.mvavrill.logicGamesSolver.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }

}
