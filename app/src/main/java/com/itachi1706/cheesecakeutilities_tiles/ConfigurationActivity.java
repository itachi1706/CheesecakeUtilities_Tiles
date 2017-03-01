package com.itachi1706.cheesecakeutilities_tiles;


import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

        private boolean ss, du;

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

            // Check permission grant state
            ss = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
            du = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED;

            Log.i("PermissionCheck", "SECURE_SETTINGS: " + ss + " | DUMP: " + du);

            findPreference("perm_securesettings").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity()).setTitle("Grant SECURE_SETTINGS Permission")
                            .setMessage("To be able to use some of the tiles, " +
                                    "please grant the android.permission.SECURE_SETTINGS permission\n\n" +
                                    "To grant this permission, connect your device to a computer with ADB installed " +
                                    "and execute the following command:\n\n" + getResources().getString(R.string.permission_ss_command) +
                                    "\n\nClick share to share the command required")
                            .setPositiveButton(android.R.string.ok, null)
                            .setNeutralButton("Share", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.permission_ss_command));
                                    sendIntent.setType("text/plain");
                                    startActivity(sendIntent);
                                }
                            }).show();
                    return false;
                }
            });

            findPreference("perm_dump").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity()).setTitle("Grant DUMP Permission")
                            .setMessage("To be able to use some of the tiles, " +
                                    "please grant the android.permission.DUMP permission\n\n" +
                                    "To grant this permission, connect your device to a computer with ADB installed " +
                                    "and execute the following command:\n\n" + getResources().getString(R.string.permission_d_command) +
                                    "\n\nClick share to share the command required")
                            .setPositiveButton(android.R.string.ok, null)
                            .setNeutralButton("Share", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.permission_d_command));
                                    sendIntent.setType("text/plain");
                                    startActivity(sendIntent);
                                }
                            }).show();
                    return false;
                }
            });

            if (ss && du) getPreferenceScreen().removePreference(findPreference("cat_additionalperm")); // Hide category
            else {
                PreferenceCategory cat = (PreferenceCategory) findPreference("cat_additionalperm");
                if (ss) {
                    boolean res = cat.removePreference(findPreference("perm_securesettings"));
                    Log.d("DEBUG", "Removed SS: " + res);
                }
                if (du) {
                    boolean res = cat.removePreference(findPreference("perm_dump"));
                    Log.d("DEBUG", "Removed D: " + res);
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
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
