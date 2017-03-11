package com.franklyn.mobileswitch.activities;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toast;

import com.franklyn.mobileswitch.R;
import com.franklyn.mobileswitch.adapter.SwitchAdapter;
import com.franklyn.mobileswitch.app.AppController;
import com.franklyn.mobileswitch.helper.pojo.SwitchContent;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AGBOMA franklyn on 1/31/17.
 */

public class HistoryActivity extends AppCompatActivity {

    private ListView switchView;
    private SwitchAdapter switchAdapter;
    private DatabaseReference databaseListReference;
    private ChildEventListener childEventListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        if(getResources().getConfiguration().screenWidthDp <= 600){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        init();
        loadSwitchHistory();
    }

    private void init() {

        databaseListReference = AppController.mFirebaseDatabase.getReference()
                .child(AppController.SWITCH_LIST_DATABASE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading switch history");
        progressDialog.setCancelable(false);

        switchView = (ListView) findViewById(R.id.list_switch_content);

        List<SwitchContent> switchContents = new ArrayList<>();
        switchAdapter = new SwitchAdapter(this, switchContents);
        switchView.setAdapter(switchAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        dismissDialog();

        if(childEventListener != null) {
            databaseListReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

    private void dismissDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    private void loadSwitchHistory() {

        if(childEventListener == null) {

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    SwitchContent switchContent = dataSnapshot.getValue(SwitchContent.class);
                    if(switchContent != null)
                        switchAdapter.add(dataSnapshot.getValue(SwitchContent.class));
                    else
                        Toast.makeText(HistoryActivity.this, "No history available",
                                Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //end progress
                            dismissDialog();
                        }
                    }, 1000);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            databaseListReference.addChildEventListener(childEventListener);

        }
    }

}
