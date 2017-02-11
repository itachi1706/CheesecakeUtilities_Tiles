package com.itachi1706.cheesecakeutilities_tiles;


import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;


public class ConfigurationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new GeneralPreferenceFragment())
                .commit();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            String[] toggleList = getResources().getStringArray(R.array.toggleList);

            for (String pref : toggleList) {
                Preference p = findPreference(pref);
                if (!(p instanceof SwitchPreference)) continue;

                p.setOnPreferenceChangeListener(this);
                String c = p.getExtras().getString("class", "-");
                Class s;
                try {
                    s = Class.forName(c);
                } catch (ClassNotFoundException e) {
                    Log.e("ClassParsing", "Invalid Class: " + c);
                    continue;
                }

                ((SwitchPreference) p).setChecked((getActivity().getPackageManager()
                        .getComponentEnabledSetting(new ComponentName(getActivity(), s)) == 1));

            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), ConfigurationActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }


        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if (!(preference instanceof SwitchPreference)) {
                return false;
            }

            String c = preference.getExtras().getString("class", "-");
            if (c.startsWith(".")) c = "com.itachi1706.cheesecakeutilities_tiles" + c;
            Log.d("DEBUG", c);
            Class s;
            try {
                s = Class.forName(c);
            } catch (ClassNotFoundException e) {
                Log.e("ClassParsing", "Invalid Class: " + c);
                return false;
            }

            Boolean val = (Boolean) newValue;

            toggleTile(val, s);

            Log.i("TileChange", "Tile: " + preference.getTitle() + " " + ((val) ? "enabled" : "disabled"));
            return true;
        }

        private void toggleTile(boolean val, Class c) {
            PackageManager manager = getActivity().getPackageManager();
            manager.setComponentEnabledSetting(new ComponentName(getActivity(), c), (val) ?
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
