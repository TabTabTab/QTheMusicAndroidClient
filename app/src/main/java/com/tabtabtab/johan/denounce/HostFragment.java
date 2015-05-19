package com.tabtabtab.johan.denounce;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        textView.setText("Your host id: " + hostMonitor.getHostId());

        //TODO Remove hard coded path
        musicFolderPath =  "/storage/extSdCard/Music/";
        System.out.println(musicFolderPath);
        startTheMusicPlayer(musicFolderPath);
        try {
            IncomingClientListenerThread clientListener = new IncomingClientListenerThread(hostMonitor, DebugConstants.HOST_PORT);
            clientListener.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Button next = (Button) rootView.findViewById(R.id.music_next_btn);
        Button pause = (Button) rootView.findViewById(R.id.music_pause_btn);
        Button play = (Button) rootView.findViewById(R.id.music_play_btn);
        Button stop = (Button) rootView.findViewById(R.id.music_stop_btn);
        next.setOnClickListener(this);
        pause.setOnClickListener(this);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

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
        if(!folder.exists()){
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
        switch(v.getId()){
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

        }
    }
}
