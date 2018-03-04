// IExtensionHost.aidl
package com.priamm.vlc.extensions.api;

import com.priamm.vlc.extensions.api.VLCExtensionItem;
import android.net.Uri;

interface IExtensionHost {
    // Protocol version 1
    oneway void updateList(in String title, in List<VLCExtensionItem> items, boolean showParams, boolean isRefresh);
    oneway void playUri(in Uri uri, String title);
    oneway void unBind(int index);
}
