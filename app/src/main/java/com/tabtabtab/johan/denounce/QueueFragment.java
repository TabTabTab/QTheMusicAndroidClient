package com.tabtabtab.johan.denounce;

/**
 * Created by Johan on 01/05/2015.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import monitor.ClientMonitor;
import monitor.ConnectionMonitor;

/**
 * A placeholder fragment containing a simple view.
 */
public class QueueFragment extends Fragment {
    private ClientMonitor monitor=null;
    public QueueFragment() {
    }
    public void setMonitor(ClientMonitor monitor){
        this.monitor=monitor;
    }
    public  ClientMonitor getMonitor(){
        return monitor;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_queue, container, false);
        Spinner trackList=(Spinner)rootView.findViewById(R.id.track_list);

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
        return rootView;
    }
}
