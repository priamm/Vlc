package com.priamm.vlc.extensions.api;

import com.priamm.vlc.extensions.api.IExtensionHost;
import com.priamm.vlc.extensions.api.VLCExtensionItem;

interface IExtensionService {
    // Protocol version 1
    oneway void onInitialize(int index, in IExtensionHost host);
    oneway void browse(String stringId);
    oneway void refresh();
}

