package com.franklyn.mobileswitch.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.franklyn.mobileswitch.R;
import com.franklyn.mobileswitch.helper.connection.ConnectionReceiver;
import com.franklyn.mobileswitch.helper.pojo.SwitchContent;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AGBOMA franklyn on 1/25/17.
 */

public class AppController extends Application {

    private static AppController instance;
    private List<SwitchContent> switchList;
    public static FirebaseDatabase mFirebaseDatabase;
    public final static String SWITCH_DATABASE = "Switch_DB";
    public final static String SWITCH_LIST_DATABASE = "Switch_List_DB";

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        mFirebaseDatabase = FirebaseDatabase.getInstance();

        switchList = new ArrayList<>();
    }


    public static synchronized AppController getInstance() {return instance;}


    public boolean checkConnection(){
        return ConnectionReceiver.isConnected();
    }

}
