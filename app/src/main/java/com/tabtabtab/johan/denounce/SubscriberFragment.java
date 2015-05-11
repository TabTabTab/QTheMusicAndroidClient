package com.tabtabtab.johan.denounce;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private Vibrator vibration;

    public SubscriberFragment() {
        // Required empty public constructor
        //
    }

    public ClientMonitor getClientMonitor(){
        return clientMonitor;
    }

    public android.support.v4.app.FragmentManager getFragMan(){
        return getFragmentManager();
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
        ImageView i = (ImageView) rootView.findViewById(R.id.note_imageview);
        i.setOnClickListener(this);
        final EditText editText = (EditText) rootView.findViewById(R.id.hostNbrText);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_ENTER:
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            onClick(v);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
        vibration = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.connect_to_host_btn:
                vibration.vibrate(50);
                EditText hostText = (EditText) rootView.findViewById(R.id.hostNbrText);
                String stringHostNbr = hostText.getText().toString();
                if (!stringHostNbr.isEmpty()) {
                    try{
                        int hostNbr = Integer.parseInt(stringHostNbr);
                        startQueue(hostNbr);
                    }catch(NumberFormatException e){
                        Toast.makeText(activity, "Host number must be a number",
                                Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(activity, "Host number must be a number",
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.note_imageview:
                new AlertDialog.Builder(activity)
                        .setTitle("Easter egg")
                        .setMessage("This app is created by the legends: \n Denhi Huynh \n Fredrik Folkesson \n Johan Nyholm \n Shan Senanayake \n ")
                        .setPositiveButton(android.R.string.yes, null).show();
                break;
        }

    }

    private void startQueue(int hostId){


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
