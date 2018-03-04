package com.priamm.vlc.extensions.api;

import android.app.Activity;
import android.os.Bundle;

import com.priamm.vlc.extensions.api.tools.Dialogs;

public class WarningActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dialogs.showInstallVlc(this);
    }
}

