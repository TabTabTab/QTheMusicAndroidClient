package com.tabtabtab.johan.denounce;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ClientMain.UserClient;
import monitor.ClientMonitor;


public class SubscriberFragment extends Fragment implements View.OnClickListener {

    private int hostId;
    private View rootView;
    private Activity activity;
    private QueueFragment queueFragment;
    private ListView queue;
    private ClientMonitor clientMonitor;

    public SubscriberFragment() {
        // Required empty public constructor
        //
    }

    public void setHostId(int hostId) {
        this.hostId = hostId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_subscriber, container, false);
        Button b = (Button) rootView.findViewById(R.id.connect_to_host_btn);
        b.setOnClickListener(this);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public void onClick(View v) {
        EditText hostText = (EditText) rootView.findViewById(R.id.hostNbrText);
        String stringHostNbr = hostText.getText().toString();
        if (!stringHostNbr.isEmpty()) {
            try{
                int hostNbr = Integer.parseInt(stringHostNbr);
                startQueue(hostNbr, v);
            }catch(NumberFormatException e){
                Toast.makeText(activity, "Host number must be a number",
                        Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(activity, "Host number must be a number",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void startQueue(int hostId, View v){


        queueFragment= new QueueFragment();
        queue=(ListView) rootView.findViewById(R.id.queue);
        System.out.println("CURRENT QUEUE: " + queue);
        UserClient client=new UserClient(DebugConstants.CENTRAL_SERVER_IP,DebugConstants.SERVER_CLIENT_PORT,hostId);
        new Thread(client).start();
        clientMonitor=client.getMonitor();
        queueFragment.setMonitor(clientMonitor);
        boolean success = clientMonitor.checkConnectionEstablished();
        if(success){
            getFragmentManager().beginTransaction()
                    .replace(R.id.container,queueFragment)
                    .addToBackStack(null)
                    .commit();
        }else{
            Toast.makeText(activity, "Connection failed, please try again.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
