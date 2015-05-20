
package MusicQueue;

import java.util.ArrayList;

public class HostMusicQueue extends MusicQueue {
    private String folderPath;
    public boolean songIsPlaying = false;
    private PlayerCommand command;
    private boolean queueChanged;

    /**
     * Creates a new Host MusicQueue
     *
     * @param availableTracks a list with the names of available tracks,
     *                        the index of the string is the tracks trackId
     */
    public HostMusicQueue(ArrayList<String> availableTracks) {
        super(availableTracks);
        command = PlayerCommand.NOTHING;
        queueChanged = false;
    }

    public synchronized ArrayList<Integer> getCopyOfQueue() {
        ArrayList<Integer> copy = new ArrayList<Integer>();
        for (int track : trackQueue) {
            copy.add(track);
        }
        return copy;
    }

    /**
     * gets the next song id to play, if there is no song the thread will sleep. When sucessfull the songID will be removed from the hostMusicQueue
     *
     * @return
     */
    public synchronized int getNextSongId() {
        while (trackQueue.isEmpty()) {
            try {
                currentlyPlaying = -1;
                queueChanged = true;
                notifyAll();
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        currentlyPlaying = trackQueue.remove(0);
        queueChanged = true;
        notifyAll();
        return currentlyPlaying;
    }


    /**
     * Adds the track with the specified trackId to the hostMusicQueue
     *
     * @param trackId - the id of the track to be added
     * @return true if the track Id existed, otherwise false
     */
    public synchronized boolean addToQueue(int trackId) {
        if (trackId >= 0 && availableTracks.size() > trackId) {
            trackQueue.add(trackId);
            queueChanged = true;
            notifyAll();
            return true;
        } else {
            return false;
        }
    }


    public synchronized void setCommand(PlayerCommand command) {
        this.command = command;
        notifyAll();
    }

    public synchronized void startingSong() {
        songIsPlaying = true;
        notifyAll();
    }

    public synchronized void finishedSong() {
        currentlyPlaying = -1;
        songIsPlaying = false;
        notifyAll();
    }

    /**
     * method for the music playing thread to wait for the song to finish
     *
     * @throws InterruptedException
     */
    public synchronized PlayerCommand waitForFinishedSongOrCommand() throws InterruptedException {
        while (songIsPlaying && (command == PlayerCommand.NOTHING || command == PlayerCommand.PLAY)) {
            wait();
        }
        System.out.println("command " + command);
        System.out.println("Song is playing " + songIsPlaying);
        PlayerCommand tempCommand = command;
        command = PlayerCommand.NOTHING;
        return tempCommand;
    }

    public synchronized PlayerCommand waitForCommand() throws InterruptedException {
        while (command == PlayerCommand.STOP || command == PlayerCommand.NOTHING || command == PlayerCommand.PAUSE) {
            wait();
        }
        PlayerCommand tempCommand = command;
        command = PlayerCommand.NOTHING;
        return tempCommand;
    }

    public synchronized ArrayList<String> waitForQueueChange() {
        while (!queueChanged) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        queueChanged = false;
        return getQueueTracks();
    }


}
