package com.tabtabtab.johan.denounce;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import MusicQueue.HostMusicQueue;
import MusicQueue.MusicPlayerThread;
import MusicQueue.PlayerCommand;
import connection.IncomingClientListenerThread;
import monitor.HostMonitor;
import protocol.DebugConstants;

public class HostFragment extends Fragment implements View.OnClickListener {
    private HostMonitor hostMonitor;
    private String musicFolderPath;
    private HostMusicQueue queue;
    private View rootView;
    private ArrayList<String> queueList;
    private ArrayAdapter<String> arrayAdapter;
    private Activity activity;
    private Spinner spinner;

    public static HostFragment newInstance() {
        HostFragment fragment = new HostFragment();
        return fragment;
    }

    public HostFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_host, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.host_id);
        textView.setText("Your party id: " + hostMonitor.getHostId());

        //TODO Remove hard coded path
        musicFolderPath = "/storage/extSdCard/Music/";
        System.out.println(musicFolderPath);
        startTheMusicPlayer(musicFolderPath);
        try {
            IncomingClientListenerThread clientListener = new IncomingClientListenerThread(hostMonitor, DebugConstants.HOST_PORT);
            clientListener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ImageView stop = (ImageView) rootView.findViewById(R.id.music_stop_btn);
        ImageView next = (ImageView) rootView.findViewById(R.id.music_next_btn);
        ImageView play = (ImageView) rootView.findViewById(R.id.music_play_btn);
        ImageView pause = (ImageView) rootView.findViewById(R.id.music_pause_btn);
        next.setOnClickListener(this);
        pause.setOnClickListener(this);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);

        //The spinner ------------

        Button q = (Button) rootView.findViewById(R.id.host_add_track_btn);
        q.setOnClickListener(this);

        spinner = (Spinner) rootView.findViewById(R.id.host_track_list);

        List<String> list = null;

        list = hostMonitor.getMusicQueue().getAvailableTracks();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        //The listView ------------

        ListView visualQueue = (ListView) rootView.findViewById(R.id.hostqueue);
        queueList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(
                visualQueue.getContext(),
                android.R.layout.simple_list_item_1, queueList
        );
        visualQueue.setAdapter(arrayAdapter);
        Thread updateQueueThread = new Thread(new Runnable() {
            @Override
            public void run() {
                HostMusicQueue queue = hostMonitor.getMusicQueue();
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
            }
        });
        updateQueueThread.start();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


    public void setMonitor(HostMonitor hostMonitor) {
        this.hostMonitor = hostMonitor;
    }

    /**
     * Start the music player using music from the specified folderPath
     *
     * @param folderPath
     */
    private void startTheMusicPlayer(String folderPath) {
        ArrayList<String> songNames = getMusicFileNames(folderPath);
        ArrayList<String> temp = getMusicFileNames(folderPath);
        queue = new HostMusicQueue(temp);
        hostMonitor.setMusicQueue(queue);
        MusicPlayerThread player = new MusicPlayerThread(queue, songNames, folderPath, hostMonitor);
        player.start();
    }

    /**
     * Creates a list of the music files in the folder and returns it.
     *
     * @param folderPath
     * @return
     */
    private ArrayList<String> getMusicFileNames(String folderPath) {
        ArrayList<String> musicFileNames = new ArrayList<>();
        File folder = new File(folderPath);
        if (!folder.exists()) {
            return musicFileNames;
        }
        String musicFilename;
        for (File file : folder.listFiles()) {
            musicFilename = file.getName();
            if (musicFilename.endsWith(".mp3")) {
                musicFileNames.add(musicFilename);
            }
        }
        return musicFileNames;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.music_stop_btn:
                queue.setCommand(PlayerCommand.STOP);
                break;
            case R.id.music_play_btn:
                queue.setCommand(PlayerCommand.PLAY);
                break;
            case R.id.music_pause_btn:
                queue.setCommand(PlayerCommand.PAUSE);
                break;
            case R.id.music_next_btn:
                queue.setCommand(PlayerCommand.NEXT);
                break;
            case R.id.host_add_track_btn:
                int trackId = spinner.getSelectedItemPosition();
                hostMonitor.processRequest("Q " + trackId,-1);

                break;
        }
    }
}
