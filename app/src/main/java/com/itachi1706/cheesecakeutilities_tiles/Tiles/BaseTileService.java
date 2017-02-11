package com.itachi1706.cheesecakeutilities_tiles.Tiles;

import android.content.ContentResolver;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

import com.itachi1706.cheesecakeutilities_tiles.R;

/**
 * Created by Kenneth on 11/2/2017.
 * for com.itachi1706.cheesecakeutilities_tiles.Tiles in CheesecakeUtilities_Tiles
 */

public abstract class BaseTileService extends TileService {

    protected ContentResolver contentResolver;

    @Override
    public void onStartListening() {
        super.onStartListening();
        contentResolver = getContentResolver();
        updateTile();
    }

    protected abstract boolean isFeatureEnabled();

    protected void updateTile() {
        final Tile tile = getQsTile();

        if (isFeatureEnabled()) {
            tile.setState(Tile.STATE_ACTIVE);
        } else {
            tile.setState(Tile.STATE_INACTIVE);
        }

        tile.updateTile();
    }

    protected void showPermissionError() {
        String message = getString(R.string.permission_required_toast);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Log.e("Dev Tile Service", message);
    }
}
