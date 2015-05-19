package com.tabtabtab.johan.denounce;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import connection.HostIDFetcherThread;
import monitor.HostMonitor;
import protocol.DebugConstants;

public class HomeScreenFragment extends Fragment implements View.OnClickListener {
    private Activity activity;
    private Vibrator vibration;

    public static HomeScreenFragment newInstance() {
        HomeScreenFragment fragment = new HomeScreenFragment();
        return fragment;
    }

    public HomeScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_home_screen, container, false);
        Button client = (Button) rootview.findViewById(R.id.client_btn);
        Button host = (Button) rootview.findViewById(R.id.host_btn);
        client.setOnClickListener(this);
        host.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootview;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        vibration = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.client_btn:
                vibration.vibrate(50);
                getFragmentManager().beginTransaction()
                        .add(R.id.container, new SubscriberFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.host_btn:
                vibration.vibrate(50);
                HostMonitor hostMonitor = new HostMonitor(10);
                HostIDFetcherThread fetcher = new HostIDFetcherThread(DebugConstants.CENTRAL_SERVER_IP,DebugConstants.SERVER_HOST_PORT, hostMonitor);
                fetcher.start();
                boolean success = hostMonitor.checkConnectionEstablished();
                if (success) {
                    HostFragment hostFragment = new HostFragment();
                    hostFragment.setMonitor(hostMonitor);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, hostFragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(activity, "Connection failed, please try again.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
