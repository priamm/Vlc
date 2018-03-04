/*
 * *************************************************************************
 *  PreferencesAudio.java
 * **************************************************************************
 *  Copyright © 2016 VLC authors and VideoLAN
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

package com.priamm.vlc.gui.preferences;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.TwoStatePreference;

import com.priamm.libvlc.util.AndroidUtil;
import com.priamm.libvlc.util.HWDecoderUtil;
import com.priamm.vlc.PlaybackService;
import com.priamm.vlc.R;
import com.priamm.vlc.util.Constants;
import com.priamm.vlc.util.VLCInstance;

public class PreferencesAudio extends BasePreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected int getXml() {
        return R.xml.preferences_audio;
    }

    @Override
    protected int getTitleId() {
        return R.string.audio_prefs_category;
    }

    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findPreference("audio_ducking").setVisible(!AndroidUtil.isOOrLater);
        final HWDecoderUtil.AudioOutput aout = HWDecoderUtil.getAudioOutputFromDevice();
        if (aout != HWDecoderUtil.AudioOutput.ALL) {
            /* no AudioOutput choice */
            findPreference("aout").setVisible(false);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey() == null) return false;
        switch (preference.getKey()){
            case "enable_headset_detection":
                ((PreferencesActivity)getActivity()).detectHeadset(((TwoStatePreference) preference).isChecked());
                return true;
            case "enable_steal_remote_control":
                PlaybackService.Client.restartService(getActivity());
                return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final Activity activity = getActivity();
        if (activity == null) return;
        switch (key){
            case "aout":
                VLCInstance.restart();
                ((PreferencesActivity)activity).restartMediaPlayer();
                break;
            case Constants.KEY_ARTISTS_SHOW_ALL:
                ((PreferencesActivity)activity).updateArtists();
                break;
        }
    }
}