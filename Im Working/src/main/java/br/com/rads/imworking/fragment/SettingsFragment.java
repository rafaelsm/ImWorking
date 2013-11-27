package br.com.rads.imworking.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

    }
}
