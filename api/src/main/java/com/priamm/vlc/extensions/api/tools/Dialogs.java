package com.priamm.vlc.extensions.api.tools;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.priamm.vlc.api.R;

public class Dialogs {

    /**
     * Constant value to disable Dialog message.
     */
    public static final int NO_MESSAGE = 0;


    /**
     * Facility function to show an AlertDialog
     * @param context The context to use for Dialog display
     * @param title Title for the AlertDialog
     * @param message Message to display (set to NO_MESSAGE if you don't want to show any message)
     * @param cancelListener The listener wich will be triggered after dialog cancelation.
     *                       If null, the dialog is not cancelable
     * @return the AlertDialog instance
     */
    public static AlertDialog showAlertDialog(@NonNull Context context, @NonNull int title,
                                              @Nullable int message,
                                              @Nullable DialogInterface.OnCancelListener cancelListener) {
        return showAlertDialog(context, title, message, cancelListener, null, null);
    }

    /**
     * Facility function to show an AlertDialog
     * @param context The context to use for Dialog display
     * @param title Title for the AlertDialog
     * @param message Message to display (set to NO_MESSAGE if you don't want to show any message)
     * @param cancelListener The listener wich will be triggered after dialog cancelation.
     *                       If null, the dialog is not cancelable
     * @param positiveAction DialogInterface.OnClickListener wich will be triggered on 'OK' button click.
     *                       If null, the AlertDialog doesn't have positive button.
     * @param negativeAction ialogInterface.OnClickListener wich will be triggered on 'Cancel' button click.
     *                       If null, the AlertDialog doesn't have negative button.
     * @return the AlertDialog instance
     */
    public static AlertDialog showAlertDialog(@NonNull Context context, @NonNull int title,
                                              @NonNull int message,
                                              @Nullable DialogInterface.OnCancelListener cancelListener,
                                              @Nullable DialogInterface.OnClickListener positiveAction,
                                              @Nullable DialogInterface.OnClickListener negativeAction) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title);
        if (message > 0)
            builder.setMessage(message);
        if (cancelListener != null) {
            builder.setCancelable(true)
                    .setOnCancelListener(cancelListener);
        }
        if (positiveAction != null) {
            builder.setPositiveButton(android.R.string.ok, positiveAction);
        }
        if (negativeAction != null) {
            builder.setPositiveButton(android.R.string.cancel, negativeAction);
        }
        AlertDialog alert = builder.create();
        alert.show();
        return alert;
    }

    /**
     * Show an AlertDialog warning user the extension needs internet access,
     * and offers him to activate wifi connection
     * @param context The context to use for Dialog display
     * @param cancelListener The listener wich will be triggered after dialog cancelation or positive action
     * @return the AlertDialog instance
     */
    public static AlertDialog showNetworkNeeded(@NonNull final Context context,
                                                @NonNull final DialogInterface.OnCancelListener cancelListener) {
        return showAlertDialog(context, R.string.network_error_title, R.string.network_error_message,
                cancelListener, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        dialog.cancel();
                    }
                }, null);
    }

    public static AlertDialog showInstallVlc(@NonNull final Activity activity) {
        return showAlertDialog(activity, R.string.vlc_error_title, R.string.vlc_error_message,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                        activity.finish();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=org.videolan.vlc")));
                        dialog.dismiss();
                        activity.finish();
                    }
                }, null);
    }
}
