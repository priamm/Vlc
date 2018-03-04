package com.priamm.vlc.gui.tv;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.webkit.WebView;

import com.priamm.vlc.R;
import com.priamm.vlc.util.Util;

public class LicenceActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView licence = new WebView(this);
        licence.loadData(Util.readAsset("licence.htm", "").replace("!COMMITID!", ""), "text/html", "UTF8");
        setContentView(licence);
        ((View)licence.getParent()).setBackgroundColor(Color.LTGRAY);
        TvUtil.applyOverscanMargin(this);
    }
}
