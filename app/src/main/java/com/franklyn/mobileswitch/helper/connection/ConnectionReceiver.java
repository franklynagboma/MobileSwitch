package com.franklyn.mobileswitch.helper.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.franklyn.mobileswitch.R;

/**
 * Created by AGBOMA franklyn on 1/25/17.
 */
public class ConnectionReceiver extends BroadcastReceiver {

    private final String LOG_TAG = ConnectionReceiver.class.getSimpleName();
    private static boolean value;

    @Override
    public void onReceive(Context context, final Intent intent) {

        int connectionType = Connection.getConnectionState(context);

        if(connectionType == 0){
            Log.e(LOG_TAG, "not connected");
            value = false;
            //connectionDialog(context);

        }
        else {
            Log.e(LOG_TAG, "connected");
            value = true;
        }
    }


    public static boolean isConnected() {
        return value;
    }
}
