package com.priamm.medialibrary.interfaces;

import com.priamm.medialibrary.media.MediaWrapper;

public interface MediaAddedCb {
    void onMediaAdded(MediaWrapper[] mediaList);
}
