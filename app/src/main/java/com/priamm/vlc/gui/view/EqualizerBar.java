/*****************************************************************************
 * EqualizerBar.java
 *****************************************************************************
 * Copyright © 2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package com.priamm.vlc.gui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.priamm.libvlc.util.AndroidUtil;
import com.priamm.vlc.R;
import com.priamm.vlc.interfaces.OnEqualizerBarChangeListener;

public class EqualizerBar extends LinearLayout {

    private static final int PRECISION = 10;
    private static final int RANGE = 20 * PRECISION;

    private VerticalSeekBar mSeek;
    private TextView mBand;
    private TextView mValue;
    private OnEqualizerBarChangeListener listener;

    public EqualizerBar(Context context, float band) {
        super(context);
        init(context, band);
    }

    public EqualizerBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, 0);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void init(Context context, float band) {
        LayoutInflater.from(context).inflate(R.layout.equalizer_bar, this, true);

        mSeek = (VerticalSeekBar) findViewById(R.id.equalizer_seek);
        //Force LTR to fix VerticalSeekBar background problem with RTL layout
        if (AndroidUtil.isJellyBeanMR1OrLater){
            mSeek.setLayoutDirection(LAYOUT_DIRECTION_LTR);
        }
        mSeek.setMax(2 * RANGE);
        mSeek.setProgress(RANGE);
        mSeek.setOnSeekBarChangeListener(mSeekListener);
        mBand = (TextView) findViewById(R.id.equalizer_band);
        mBand.setText(band < 999.5f
                ? (int) (band + 0.5f) + " Hz"
                : (int) (band / 1000.0f + 0.5f) + " kHz");
        mValue = (TextView) findViewById(R.id.equalizer_value);
    }

    private final OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float value = (progress - RANGE) / (float) PRECISION;
            mValue.setText(value + " dB");
            if (listener != null) {
                // HACK:    VerticalSeekBar programmatically calls onProgress
                //          fromUser will always be false
                //          So use custom getFromUser() instead of fromUser
                listener.onProgressChanged(value, getFromUser());
            }
        }
    };

    public void setValue(float value) {
        mSeek.setProgress((int) (value * PRECISION + RANGE));
    }

    public void setListener(OnEqualizerBarChangeListener listener) {
        this.listener = listener;
    }

    private boolean getFromUser() {
        return mSeek.getFromUser();
    }
}
