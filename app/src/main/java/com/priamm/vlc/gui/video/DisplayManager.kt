package com.priamm.vlc.gui.video

import android.annotation.TargetApi
import android.app.Activity
import android.app.Presentation
import android.content.Context
import android.content.DialogInterface
import android.graphics.PixelFormat
import android.media.MediaRouter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.FrameLayout
import com.priamm.libvlc.RendererItem
import com.priamm.libvlc.util.AndroidUtil
import com.priamm.vlc.BuildConfig
import com.priamm.vlc.R
import com.priamm.vlc.RendererDelegate

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
class DisplayManager(private val activity: Activity, cloneMode: Boolean) : RendererDelegate.RendererPlayer {

    enum class DisplayType { PRIMARY, PRESENTATION, RENDERER }

    val displayType: DisplayType
    // Presentation
    private val mediaRouter: MediaRouter? by lazy { if (AndroidUtil.isJellyBeanMR1OrLater) activity.applicationContext.getSystemService(Context.MEDIA_ROUTER_SERVICE) as MediaRouter else null }
    private var mediaRouterCallback: MediaRouter.SimpleCallback? = null
    var presentation: SecondaryDisplay? = null
    private var presentationDisplayId = -1
    ///Renderers
    private var rendererItem: RendererItem? = null

    val isPrimary: Boolean
        get() = displayType == DisplayType.PRIMARY
    val isOnRenderer: Boolean
        get() = displayType == DisplayType.RENDERER

    /**
     * Listens for when presentations are dismissed.
     */
    private val mOnDismissListener = DialogInterface.OnDismissListener { dialog ->
        if (dialog == presentation) {
            if (BuildConfig.DEBUG) Log.i(TAG, "Presentation was dismissed.")
            presentation = null
            presentationDisplayId = -1
        }
    }

    init {
        presentation = if (AndroidUtil.isJellyBeanMR1OrLater) createPresentation(cloneMode) else null
        rendererItem = RendererDelegate.selectedRenderer
        displayType = getCurrentType()
        RendererDelegate.addPlayerListener(this)
    }

    companion object {
        private val TAG = "VLC/DisplayManager"
    }

    fun release() {
        if (displayType == DisplayType.PRESENTATION) {
            presentation?.dismiss()
            presentation = null
        }
    }

    private fun updateDisplayType() {
        if (getCurrentType() != displayType && activity is VideoPlayerActivity) activity.recreate()
    }

    private fun getCurrentType() = when {
        presentationDisplayId != -1 -> DisplayType.PRESENTATION
        rendererItem !== null -> DisplayType.RENDERER
        else -> DisplayType.PRIMARY
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun createPresentation(cloneMode: Boolean): SecondaryDisplay? {
        if (mediaRouter === null || cloneMode) return null

        // Get the current route and its presentation display.
        val route = mediaRouter?.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO)
        val presentationDisplay = route?.presentationDisplay
        if (presentationDisplay !== null) {
            // Show a new presentation if possible.
            if (BuildConfig.DEBUG) Log.i(TAG, "Showing presentation on display: " + presentationDisplay)
            val presentation = SecondaryDisplay(activity, presentationDisplay)
            presentation.setOnDismissListener(mOnDismissListener)
            try {
                presentation.show()
                presentationDisplayId = presentationDisplay.displayId
                return presentation
            } catch (ex: WindowManager.InvalidDisplayException) {
                if (BuildConfig.DEBUG) Log.w(TAG, "Couldn't show presentation!  Display was removed in " + "the meantime.", ex)
                presentationDisplayId = -1
            }
        } else if (BuildConfig.DEBUG) Log.i(TAG, "No secondary display detected")
        return null
    }

    /**
     * Add or remove MediaRouter callbacks. This is provided for version targeting.
     *
     * @param add true to add, false to remove
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun mediaRouterAddCallback(add: Boolean) {
        if (!AndroidUtil.isJellyBeanMR1OrLater || mediaRouter === null
                || add == (mediaRouterCallback !== null))
            return
        if (add) {
            mediaRouterCallback = object : MediaRouter.SimpleCallback() {
                override fun onRoutePresentationDisplayChanged(
                        router: MediaRouter, info: MediaRouter.RouteInfo) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info)
                    val newDisplayId = info.presentationDisplay?.displayId ?: -1
                    if (newDisplayId == presentationDisplayId) return
                    presentationDisplayId = newDisplayId
                    if (presentationDisplayId != -1) removePresentation() else updateDisplayType()
                }
            }
            mediaRouter?.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mediaRouterCallback)
        } else {
            mediaRouter?.removeCallback(mediaRouterCallback)
            mediaRouterCallback = null
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun removePresentation() {
        if (mediaRouter === null) return

        // Dismiss the current presentation if the display has changed.
        if (BuildConfig.DEBUG) Log.i(TAG, "Dismissing presentation because the current route no longer " + "has a presentation display.")
        presentation?.dismiss()
        presentation = null
        updateDisplayType()
    }

    override fun onRendererChanged(renderer: RendererItem?) {
        rendererItem = renderer
        updateDisplayType()
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    class SecondaryDisplay(context: Context, display: Display) : Presentation(context, display) {

        lateinit var surfaceView: SurfaceView
        lateinit var subtitlesSurfaceView: SurfaceView
        lateinit var surfaceFrame: FrameLayout

        companion object {
            val TAG = "VLC/SecondaryDisplay"
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.player_remote)
            surfaceView = findViewById(R.id.remote_player_surface)
            subtitlesSurfaceView = findViewById(R.id.remote_subtitles_surface)
            surfaceFrame = findViewById(R.id.remote_player_surface_frame)
            subtitlesSurfaceView.setZOrderMediaOverlay(true)
            subtitlesSurfaceView.holder.setFormat(PixelFormat.TRANSLUCENT)
            if (BuildConfig.DEBUG) Log.i(TAG, "Secondary display created")
        }
    }
}
