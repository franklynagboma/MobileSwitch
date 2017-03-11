package com.franklyn.mobileswitch.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.franklyn.mobileswitch.R;
import com.franklyn.mobileswitch.app.AppController;
import com.franklyn.mobileswitch.helper.connection.ConnectionReceiver;
import com.franklyn.mobileswitch.helper.pojo.SwitchContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;


/**
 * Created by AGBOMA franklyn on 1/25/17.
 */

public class MobileSwitchActivity extends AppCompatActivity implements View.OnClickListener{

    private final String DEVICE_NAME = Build.MANUFACTURER + "  "+ Build.MODEL;
    private final String LOG_TAG = MobileSwitchActivity.class.getSimpleName();

    private Button buttonOn, buttonOff;
    private ImageView wifiButton, switchMenu;
    private int getCurrentState;

    private boolean connectionEnabled;
    private ProgressDialog stateLoadingDialog;
    //private int brownColor = R.color.colorPrimaryDark, whiteColor = R.color.button_off;
    private Drawable brownDrawable, whiteDrawable;

    //fire base reference
    private DatabaseReference mFirebaseReference;
    private DatabaseReference mFirebaseListReference;
    public ValueEventListener mValueEventListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_switch);

        if(getResources().getConfiguration().screenWidthDp <= 600){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        //Access internet model of device.
        new ConnectionReceiver().onReceive(this, new Intent());

        //initialize widgets
        init();
        //getSwitchStateFromCloud();


    }

    private void init() {

        //object views
        switchMenu = (ImageView) findViewById(R.id.switch_menu);
        buttonOn = (Button) findViewById(R.id.button_on);
        buttonOff = (Button) findViewById(R.id.button_off);
        wifiButton = (ImageView) findViewById(R.id.wifi_button);


        //set onclick listener
        buttonOn.setOnClickListener(this);
        buttonOff.setOnClickListener(this);
        switchMenu.setOnClickListener(this);

        stateLoadingDialog = new ProgressDialog(this);
        stateLoadingDialog.setCancelable(false);

        brownDrawable = getResources().getDrawable(R.drawable.brown);
        whiteDrawable = getResources().getDrawable(R.drawable.white);

        mFirebaseReference = AppController.mFirebaseDatabase.getReference()
                .child(AppController.SWITCH_DATABASE);
        mFirebaseListReference = AppController.mFirebaseDatabase.getReference()
                .child(AppController.SWITCH_LIST_DATABASE);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.switch_menu:

                PopupMenu showMenu = new PopupMenu(MobileSwitchActivity.this, view);
                showMenu.getMenuInflater().inflate(R.menu.list_menu, showMenu.getMenu());

                showMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.history:

                                startActivity(new Intent(MobileSwitchActivity.this,
                                        HistoryActivity.class));
                                break;
                        }
                        return true;
                    }
                });
                showMenu.show();

                break;
            case R.id.button_on:

                Log.e(LOG_TAG, "On " + AppController.getInstance().checkConnection());
                if(AppController.getInstance().checkConnection()) {
                    //testing with non real data
                    /*progressDialog("Sending switch state");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //stop loading dialog
                            progressDialog("");
                        }
                    }, 2000);*/
                    //set off enable
                    setButtonEnable(buttonOff, true);
                    //setButtonColor(buttonOff, whiteColor);
                    setButtonColor(buttonOff, whiteDrawable);
                    //set off disable as it was
                    setButtonEnable(buttonOn, false);
                    //setButtonColor(buttonOn, brownColor);
                    setButtonColor(buttonOn, brownDrawable);
                    //end testing

                    setSwitchStateToCloud(1);
                }
                else {
                    connectionDialog();
                }

                break;
            case R.id.button_off:

                Log.e(LOG_TAG, "Off " + AppController.getInstance().checkConnection());
                if(AppController.getInstance().checkConnection()) {
                    /*//testing with non real data
                    progressDialog("Sending switch state");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //stop loading dialog
                            progressDialog("");
                        }
                    }, 2000);*/
                    //set on enable
                    setButtonEnable(buttonOn, true);
                    //setButtonColor(buttonOn, whiteColor);
                    setButtonColor(buttonOn, whiteDrawable);
                    //set off disable as it was
                    setButtonEnable(buttonOff, false);
                    //setButtonColor(buttonOff, brownColor);
                    setButtonColor(buttonOff, brownDrawable);
                    //end testing

                    setSwitchStateToCloud(0);
                }
                else {
                    connectionDialog();
                }

                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        setButtonEnable(buttonOn, false);
        setButtonEnable(buttonOff, false);
        setButtonColor(buttonOn, whiteDrawable);
        setButtonColor(buttonOff, whiteDrawable);
        wifiButton.setEnabled(false);

        //get connection status
        Log.e(LOG_TAG, "connection Enabled Resume "
                + AppController.getInstance().checkConnection());

        if(AppController.getInstance().checkConnection()){
            wifiButton.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_action_network_wifi_connected));
            getSwitchStateFromCloud();
        }
        else {
            wifiButton.setImageDrawable(getResources()
                    .getDrawable(R.drawable.ic_action_network_wifi));
            connectionDialog();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        //set connection status false
        Log.e(LOG_TAG, "connection Enabled Pause "
                + AppController.getInstance().checkConnection());
    }

    private void setButtonEnable(Button bttn, boolean value){
        Log.e(LOG_TAG, "isEnabled " + value);
        bttn.setEnabled(value);
    }

    public void connectionDialog(){

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.warning_dialog, null, false);
        //ImageView image = (ImageView) dialogView.findViewById(R.id.warning_drawable);
        //TextView text = (TextView) dialogView.findViewById(R.id.warning_text);

        MaterialDialog.Builder warning = new MaterialDialog.Builder(this);
        warning.autoDismiss(false)
                .customView(dialogView, true)
                .backgroundColorRes(R.color.white_card);
        //set setting connection
        warning.positiveText("Settings")
                .positiveColor(getResources().getColor(R.color.colorPrimary))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog,
                                        @NonNull DialogAction which) {

                        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                        if(settingsIntent.resolveActivity(getPackageManager()) != null){
                            dialog.dismiss();
                            startActivity(settingsIntent);
                        }
                    }
                });

        final MaterialDialog mDialog = warning.build();
        mDialog.setCancelable(false);
        if(!mDialog.isShowing())
            mDialog.show();

    }

    private void progressDialog(String message) {

        Log.e(LOG_TAG, "In progress dialog");


        if(stateLoadingDialog.isShowing()) {
            Log.e(LOG_TAG, "progress ends");
            stateLoadingDialog.dismiss();
        }
        else {
            Log.e(LOG_TAG, "progress starts");
            stateLoadingDialog.setMessage(message);
            stateLoadingDialog.show();
        }

    }

    private void setButtonColor(Button bttn, int color) {

        Log.e(LOG_TAG, "Change button color");
        bttn.setBackgroundResource(color);
    }
    private void setButtonColor(Button bttn, Drawable color) {

        Log.e(LOG_TAG, "Change button color");
        bttn.setBackground(color);
    }

    private void setSwitchStateToCloud(int state) {

        progressDialog("Sending switch state");
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);

        String timeStamp = "" + hour + "H-" + min + "M-" + seconds + "s";
        Log.e(LOG_TAG, "Time stamp " + timeStamp);

        //check condtion for on/off then set string of respective state
        String stringState = (state == 1) ? "ON" : "OFF";
        Log.e(LOG_TAG, "string state " + stringState);

        mFirebaseReference.setValue(state);
        mFirebaseListReference.push()
                .setValue(new SwitchContent(DEVICE_NAME, "Switch "
                        + stringState, timeStamp));

        //stop loading dialog
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(stateLoadingDialog.isShowing())
                    stateLoadingDialog.dismiss();
            }
        }, 2000);

    }
    private void getSwitchStateFromCloud() {

        //call loading dialog
        progressDialog("Loading switch state");
        getCurrentState = -1;
        loadState();
    }

    private void loadState() {

        //get state
        if(mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //get class current state
                    if(dataSnapshot.getValue() != null){
                        getCurrentState = dataSnapshot.getValue(int.class);
                        Log.e(LOG_TAG, "get current state " + getCurrentState);
                    }

                    if(getCurrentState != -1) {

                        if(getCurrentState == 1) {
                            //set on disable for even
                            setButtonEnable(buttonOn, false);
                            //setButtonColor(buttonOn, brownColor);
                            setButtonColor(buttonOn, brownDrawable);
                            //set off enable as it was
                            setButtonEnable(buttonOff, true);
                        }
                        else {
                            //set off disable for odd
                            setButtonEnable(buttonOff, false);
                            //setButtonColor(buttonOff, brownColor);
                            setButtonColor(buttonOff, brownDrawable);
                            //set on enable as it was
                            setButtonEnable(buttonOn, true);
                        }
                    }
                    else {
                        Toast.makeText(MobileSwitchActivity.this,
                                "Could not load current state", Toast.LENGTH_SHORT).show();

                        //set first time app lunch
                        setButtonEnable(buttonOn, true);
                        setButtonEnable(buttonOff, true);
                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //stop loading dialog
                            Log.e(LOG_TAG, "end progress loading");
                            if(stateLoadingDialog.isShowing())
                                stateLoadingDialog.dismiss();
                        }
                    }, 1000);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mFirebaseReference.addValueEventListener(mValueEventListener);
        }

    }

    private class ConnectionDialogTask extends AsyncTask<Void, Void, Void> {

        private String LOG_TAG = ConnectionDialogTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... voids) {
            while(connectionEnabled) {
                Log.e(LOG_TAG, "Task running");

                Thread checkConnection  = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(!AppController.getInstance().checkConnection())
                                    new MobileSwitchActivity().connectionDialog();
                            }
                        });
                    }
                }); checkConnection.start();
            }
            return null;
        }
    }
}
