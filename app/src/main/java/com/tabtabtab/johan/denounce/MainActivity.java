package com.tabtabtab.johan.denounce;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

import monitor.ClientMonitor;

public class MainActivity extends FragmentActivity {
    private ClientMonitor monitor;
    private SubscriberFragment subFragment;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            subFragment = new SubscriberFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, subFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {

       final android.support.v4.app.FragmentManager fragman = subFragment.getFragMan();
        int count = fragman.getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("Disconnect?")
                    .setMessage("Are you sure you want to disconnect to host?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            ClientMonitor monitor = subFragment.getClientMonitor();
                            if (monitor != null) {
                                monitor.closeConnection();
                                fragman.popBackStack();
                            }
                        }
                    }).create().show();
        }

    }

}
