package com.itachi1706.cheesecakeutilities_tiles.Tiles.SettingsDeveloperOptions;

import android.os.BatteryManager;
import android.provider.Settings;
import android.util.Log;

import com.itachi1706.cheesecakeutilities_tiles.Tiles.BaseTileService;

/**
 * Keeps the screen on when its connected to USB but not to AC
 */
public class ToggleKeepScreenOnService extends BaseTileService {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onClick() {
        int newValue = isFeatureEnabled() ? 0 : BatteryManager.BATTERY_PLUGGED_USB;

        try {
            Settings.Global.putInt(contentResolver, Settings.Global.STAY_ON_WHILE_PLUGGED_IN, newValue);
        } catch (SecurityException se) {
            showPermissionError();
        }
        updateTile();
    }

    @Override
    protected boolean isFeatureEnabled() {
        try {
            return Settings.Global.getInt(contentResolver, Settings.Global.STAY_ON_WHILE_PLUGGED_IN) == BatteryManager.BATTERY_PLUGGED_USB;
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }
}
