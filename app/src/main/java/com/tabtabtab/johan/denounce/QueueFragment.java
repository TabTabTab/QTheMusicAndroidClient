package com.tabtabtab.johan.denounce;

/**
 * Created by Johan on 01/05/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import MusicQueue.ClientMusicQueue;
import monitor.ClientMonitor;

/**
 * A placeholder fragment containing a simple view.
 */
public class QueueFragment extends Fragment implements View.OnClickListener {
    private ClientMonitor monitor = null;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> queueList;
    private Activity activity;
    private View rootView;
    private Vibrator vibration;

    public QueueFragment() {
    }

    public void setMonitor(ClientMonitor monitor) {
        this.monitor = monitor;
    }

    public ClientMonitor getMonitor() {
        return monitor;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_queue, container, false);

        Button b = (Button) rootView.findViewById(R.id.add_track_btn);
        b.setOnClickListener(this);

        Spinner trackList = (Spinner) rootView.findViewById(R.id.track_list);

        List<String> list = null;
        try {
            list = monitor.getAvailableTracks();
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this.getActivity(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            trackList.setAdapter(dataAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ListView visualQueue = (ListView) rootView.findViewById(R.id.clientqueue);
        queueList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(
                visualQueue.getContext(),
                android.R.layout.simple_list_item_1, queueList
        );

        visualQueue.setAdapter(arrayAdapter);
        Thread updateQueueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientMusicQueue queue = monitor.getMusicQueue();
                    while (!Thread.interrupted()) {
                        ArrayList<String> temp = queue.waitForQueueChange();
                        queueList.clear();
                        for (String song : temp) {
                            queueList.add(song);
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });

                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateQueueThread.start();
        return rootView;
    }

    @Override
    public void onClick(View v) {
        vibration.vibrate(50);
        Spinner trackList = (Spinner) rootView.findViewById(R.id.track_list);
        int trackIndex = trackList.getSelectedItemPosition();
        monitor.queueTrack(trackIndex);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        vibration = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

}
