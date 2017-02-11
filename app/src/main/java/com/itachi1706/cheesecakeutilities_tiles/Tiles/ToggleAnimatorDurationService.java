package com.itachi1706.cheesecakeutilities_tiles.Tiles;

/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by Kenneth on 11/2/2017.
 * for com.itachi1706.cheesecakeutilities_tiles.Tiles in CheesecakeUtilities_Tiles
 */

import android.content.DialogInterface;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.itachi1706.cheesecakeutilities_tiles.R;
import com.itachi1706.cheesecakeutilities_tiles.Util.AnimatorDurationScaler;

import static com.itachi1706.cheesecakeutilities_tiles.Util.AnimatorDurationScaler.getAnimatorScale;
import static com.itachi1706.cheesecakeutilities_tiles.Util.AnimatorDurationScaler.getIcon;

/**
 * A {@link TileService quick settings tile} for scaling animation durations. Toggles between 0.5x and
 * 10x animator duration scales.
 */
public class ToggleAnimatorDurationService extends TileService {

    private final CharSequence[] choices = {"Animation off", "Animation scale .5x", "Animation scale 1x",
            "Animation scale 1.5x", "Animation scale 2x", "Animation scale 5x", "Animation scale 10x"};
    private final float[] scales = {0f, 0.5f, 1f, 1.5f, 2f, 5f, 10f};

    public ToggleAnimatorDurationService() {
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        updateTile();
    }

    @Override
    public void onClick() {
        final float current = getAnimatorScale(getContentResolver());
        showDialog(getDialog(findIndexOfValue(current)));
    }

    private int findIndexOfValue(float value) {
        if (scales != null) {
            for (int i = scales.length - 1; i >= 0; i--) {
                if (scales[i] == value) {
                    return i;
                }
            }
        }
        return -1;
    }

    private AlertDialog getDialog(int selectedIndex) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme_Dialog));
        builder.setTitle(R.string.dialog_animator_duration_title)
                .setSingleChoiceItems(choices, selectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AnimatorDurationScaler.setAnimatorScale(ToggleAnimatorDurationService.this, scales[which]);
                        updateTile();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }

    private void updateTile() {
        final float scale = getAnimatorScale(getContentResolver());
        final Tile tile = getQsTile();
        tile.setIcon(Icon.createWithResource(getApplicationContext(), getIcon(scale)));
        tile.updateTile();
    }
}
