package MusicQueue;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

public abstract class MusicQueue {
    protected ArrayList<Integer> trackQueue;
    protected ArrayList<String> availableTracks;
    protected int currentlyPlaying;
    /**
     * Creates a new Host MusiqQueue
     *
     * @param availableTracks a list with the names of available tracks,
     *                        the index of the string is the tracks trackId
     */
    public MusicQueue(ArrayList<String> availableTracks) {
        this.availableTracks = availableTracks;
        trackQueue = new ArrayList<>();
        currentlyPlaying = -1;
    }

    /**
     * Fetches a list with the track names.
     * The tracks are ordered according to the hostMusicQueue order.
     *
     * @return a list of tracks
     * @throws NotAvailableTrackIdException if the hostMusicQueue contains track ids which are not available
     */
    public ArrayList<String> getQueueTracks() {
        ArrayList<String> tracknameQueue = new ArrayList<>();
        String trackName;
        if(currentlyPlaying != -1) {
            trackName = availableTracks.get(currentlyPlaying);
            tracknameQueue.add(trackName);
        }
        for (int i : trackQueue) {
            try {
                trackName = availableTracks.get(i);
                tracknameQueue.add(trackName);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return tracknameQueue;
    }


    public ArrayList<String> getAvailableTracks() {
        //TODO: maybe do not give this away?
        return availableTracks;
    }
}
