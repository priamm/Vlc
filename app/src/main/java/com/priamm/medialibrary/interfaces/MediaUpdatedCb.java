package com.priamm.medialibrary.interfaces;

import com.priamm.medialibrary.media.MediaWrapper;

public interface MediaUpdatedCb {
    void onMediaUpdated(MediaWrapper[] mediaList);
}
