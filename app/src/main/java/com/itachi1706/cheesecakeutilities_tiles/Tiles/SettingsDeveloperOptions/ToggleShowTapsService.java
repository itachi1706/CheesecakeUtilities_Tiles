package com.itachi1706.cheesecakeutilities_tiles.Tiles.SettingsDeveloperOptions;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.itachi1706.cheesecakeutilities_tiles.R;
import com.itachi1706.cheesecakeutilities_tiles.Tiles.BaseTileService;

/**
 * Toggle Show taps option
 */
public class ToggleShowTapsService extends BaseTileService {
    private final String TAG = this.getClass().getSimpleName();
    private final String SHOW_TOUCHES = "show_touches";

    @Override
    public void onClick() {
        int newValue = isFeatureEnabled() ? 0 : 1;

        try {
            Settings.System.putInt(contentResolver, SHOW_TOUCHES, newValue);
        } catch (SecurityException se) {
            String message = getString(R.string.permission_required_toast);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            Log.e(TAG, message);
        }

        updateTile();
    }

    @Override
    protected boolean isFeatureEnabled() {
        try {
            return Settings.System.getInt(contentResolver, SHOW_TOUCHES) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
