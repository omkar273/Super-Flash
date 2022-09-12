package com.lnstudio.SuperFlash;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.lnstudio.SuperFlash.R;

public class SettingsFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.main_preference);

    }

}
