package com.tabtabtab.johan.denounce;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import ClientMain.UserClient;


public class SubscriberFragment extends Fragment {

    private int hostId;
    public SubscriberFragment() {
        // Required empty public constructor
        //
    }
    public void setHostId(int hostId){
        this.hostId=hostId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscriber, container, false);
    }

}
