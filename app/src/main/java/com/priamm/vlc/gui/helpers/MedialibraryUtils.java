package com.priamm.vlc.gui.helpers;


import android.content.Intent;

import com.priamm.vlc.MediaParsingService;
import com.priamm.vlc.VLCApplication;
import com.priamm.vlc.util.Constants;

public class MedialibraryUtils {

    public static void removeDir(final String path) {
        VLCApplication.runBackground(new Runnable() {
            @Override
            public void run() {
                VLCApplication.getMLInstance().removeFolder(path);
            }
        });
    }

    public static void addDir(final String path) {
        final Intent intent = new Intent(Constants.ACTION_DISCOVER, null, VLCApplication.getAppContext(), MediaParsingService.class);
        intent.putExtra(Constants.EXTRA_PATH, path);
        VLCApplication.getAppContext().startService(intent);
    }
}
