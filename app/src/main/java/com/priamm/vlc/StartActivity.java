/*
 * *************************************************************************
 *  StartActivity.java
 * **************************************************************************
 *  Copyright © 2015 VLC authors and VideoLAN
 *  Author: Geoffrey Métais
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *  ***************************************************************************
 */

package com.priamm.vlc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;

import com.priamm.libvlc.util.AndroidUtil;
import com.priamm.vlc.gui.MainActivity;
import com.priamm.vlc.gui.SearchActivity;
import com.priamm.vlc.gui.helpers.hf.StoragePermissionsDelegate;
import com.priamm.vlc.gui.tv.MainTvActivity;
import com.priamm.vlc.gui.tv.audioplayer.AudioPlayerActivity;
import com.priamm.vlc.gui.video.VideoPlayerActivity;
import com.priamm.vlc.media.MediaUtils;
import com.priamm.vlc.util.AndroidDevices;
import com.priamm.vlc.util.Constants;
import com.priamm.vlc.util.Permissions;
import com.priamm.vlc.util.Util;

public class StartActivity extends FragmentActivity implements StoragePermissionsDelegate.CustomActionController {

    public final static String TAG = "VLC/StartActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        final boolean tv =  showTvUi();
        final String action = intent != null ? intent.getAction(): null;

        if (Intent.ACTION_VIEW.equals(action) && intent.getData() != null) {
            if (Permissions.checkReadStoragePermission(this, true))
                startPlaybackFromApp(intent);
            return;
        }

        // Start application
        /* Get the current version from package */
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        final int currentVersionNumber = BuildConfig.VERSION_CODE;
        final int savedVersionNumber = settings.getInt(Constants.PREF_FIRST_RUN, -1);
        /* Check if it's the first run */
        final boolean firstRun = savedVersionNumber == -1;
        final boolean upgrade = firstRun || savedVersionNumber != currentVersionNumber;
        if (upgrade)
            settings.edit().putInt(Constants.PREF_FIRST_RUN, currentVersionNumber).apply();
        startMedialibrary(firstRun, upgrade);
        // Route search query
        if (Intent.ACTION_SEARCH.equals(action) || "com.google.android.gms.actions.SEARCH_ACTION".equals(action)) {
            startActivity(intent.setClass(this, tv ? com.priamm.vlc.gui.tv.SearchActivity.class : SearchActivity.class));
            finish();
            return;
        } else if (MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH.equals(action)) {
            final Intent serviceInent = new Intent(Constants.ACTION_PLAY_FROM_SEARCH, null, this, PlaybackService.class)
                    .putExtra(Constants.EXTRA_SEARCH_BUNDLE, intent.getExtras());
            Util.startService(this, serviceInent);
        } else if (Constants.ACTION_SHOW_PLAYER.equals(action)) {
            startActivity(new Intent(this, tv ? AudioPlayerActivity.class : MainActivity.class));
        } else {
            startActivity(new Intent(this, tv ? MainTvActivity.class : MainActivity.class)
                    .putExtra(Constants.EXTRA_FIRST_RUN, firstRun)
                    .putExtra(Constants.EXTRA_UPGRADE, upgrade));
        }
        finish();
    }

    private void startPlaybackFromApp(Intent intent) {
        if (intent.getType() != null && intent.getType().startsWith("video"))
            startActivity(intent.setClass(this, VideoPlayerActivity.class));
        else
            MediaUtils.openMediaNoUi(intent.getData());
        finish();
    }

    private void startMedialibrary(final boolean firstRun, final boolean upgrade) {
        if (!VLCApplication.getMLInstance().isInitiated() && Permissions.canReadStorage(StartActivity.this))
            startService(new Intent(Constants.ACTION_INIT, null, StartActivity.this, MediaParsingService.class)
                    .putExtra(Constants.EXTRA_FIRST_RUN, firstRun)
                    .putExtra(Constants.EXTRA_UPGRADE, upgrade));
    }

    private boolean showTvUi() {
        return AndroidUtil.isJellyBeanMR1OrLater && (AndroidDevices.isAndroidTv || (!AndroidDevices.isChromeBook && !AndroidDevices.hasTsp) ||
                PreferenceManager.getDefaultSharedPreferences(this).getBoolean("tv_ui", false));
    }

    @Override
    public void onStorageAccessGranted() {
        startPlaybackFromApp(getIntent());
    }
}