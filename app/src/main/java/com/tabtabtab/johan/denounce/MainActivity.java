package com.tabtabtab.johan.denounce;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ClientMain.UserClient;
import monitor.ClientMonitor;


public class MainActivity extends ActionBarActivity {
    private QueueFragment queueFragment;
    private ClientMonitor clientMonitor;
    private ListView queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SubscriberFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void connectToHost(View v){
        EditText hostText=(EditText)findViewById(R.id.hostNbrText);
        String stringHostNbr=hostText.getText().toString();
        if(!stringHostNbr.isEmpty()){
            int hostNbr=Integer.valueOf(stringHostNbr);
            startQueue(hostNbr);
        }
    }
    private void startQueue(int hostId){


        queueFragment= new QueueFragment();
        queue=(ListView)findViewById(R.id.queue);
        System.out.println("CURRENT QUEUE: "+queue);
        UserClient client=new UserClient(DebugConstants.CENTRAL_SERVER_IP,DebugConstants.SERVER_CLIENT_PORT,hostId,queue);
        new Thread(client).start();
        clientMonitor=client.getMonitor();
        queueFragment.setMonitor(clientMonitor);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,queueFragment)
                .commit();
    }
    public void queueTrack(View v){
        Spinner trackList=(Spinner)findViewById(R.id.track_list);
        int trackIndex=trackList.getSelectedItemPosition();
        clientMonitor.queueTrack(trackIndex);
    }

}
