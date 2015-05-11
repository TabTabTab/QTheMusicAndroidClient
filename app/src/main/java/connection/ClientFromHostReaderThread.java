package connection;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.ArrayList;

import MusicQueue.ClientMusicQueue;
import monitor.ClientMonitor;

public class ClientFromHostReaderThread extends Thread {
    private InputStream hostStream;
    private ClientMonitor clientMonitor;
    ClientMusicQueue musicQueue;

    public ClientFromHostReaderThread(InputStream hostStream, ClientMonitor clientMonitor) {
        this.clientMonitor = clientMonitor;
        this.hostStream = hostStream;
    }

    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(hostStream));
        try {
            System.out.println("we are not going to find the songs");
            ArrayList<String> availableTracks = retrieveAllSongs(br);
            System.out.println("We got the following tracks, queue as you wish.");
            musicQueue = new ClientMusicQueue(availableTracks);
            for (int i = 0; i < availableTracks.size(); i++) {
                System.out.println("Track ID: " + i + " TrackName: " + availableTracks.get(i));
            }
            clientMonitor.setMusicQueue(musicQueue);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //lägg in stopp och paus medelanden

        while (true) {
            String message;
            try {
                message = br.readLine();
                String[] splittedMessage = message.split(" ");
                String command = splittedMessage[0];
                switch (command) {
                    case "A":
                        musicQueue.addToQueue(Integer.parseInt(splittedMessage[1]));
                        break;
                    case "S":
                        musicQueue.startingSong();
                        break;
                    case "STOP":
                        musicQueue.stoppingSong();
                        break;
                    case "F":
                        musicQueue.finishedSong();
                        break;
                    default:
                        System.out.println("Unknown message");
                        break;
                }


                //skriv ut nuvarande kön
                System.out.println("nu skriver jag ut min kö");
                musicQueue.printQueue();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Retrieves all available songs from the host as well as
     *
     * @return true if succeeded, false otherwise
     */
    private boolean setUpQueue() {
        boolean success = false;
        BufferedReader br = new BufferedReader(new InputStreamReader(hostStream));
        try {
            ArrayList<String> availableTracks = retrieveAllSongs(br);
            System.out.println("We got the following tracks, queue as you wish.");
            musicQueue = new ClientMusicQueue(availableTracks);
            for (int i = 0; i < availableTracks.size(); i++) {
                System.out.println("Track ID: " + i + " TrackName: " + availableTracks.get(i));
            }
            clientMonitor.setMusicQueue(musicQueue);
            success = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return success;
    }

    private ArrayList<String> retrieveAllSongs(BufferedReader br) throws IOException {
        String nbrOfSongsResponse;
        nbrOfSongsResponse = br.readLine();
        int nbrOfSongs = Integer.parseInt(nbrOfSongsResponse);
        ArrayList<String> availableTracks = new ArrayList<String>(nbrOfSongs);
        String trackName;
        for (int i = 0; i < nbrOfSongs; i++) {
            try {
                trackName = URLDecoder.decode(br.readLine(), "UTF-8");
                availableTracks.add(trackName);
            } catch (UnsupportedEncodingException e) {
                Log.e("utf8", "conversion", e);
            }
        }
        return availableTracks;
    }

}
