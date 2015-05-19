package MusicQueue;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

import monitor.HostMonitor;
//import sun.audio.AudioPlayer;
//import sun.audio.AudioStrea

public class MusicPlayerThread extends Thread implements MediaPlayer.OnCompletionListener {

    HostMusicQueue hostMusicQueue;
    ArrayList<String> songList;
    String folderPath;
    HostMonitor hostMonitor;

    public MusicPlayerThread(HostMusicQueue queue, ArrayList<String> songList, String folderPath, HostMonitor monitor) {
        this.hostMusicQueue = queue;
        this.songList = songList;
        this.folderPath = folderPath;
        this.hostMonitor = monitor;
    }

    public void run() {

        while (true) {

            // TODO:
            // fixa så att man kan spela .wav filer också? Nu kan man
            // istället bara spela .mp3:or

            int songIdToPlay = hostMusicQueue.getNextSongId();
            hostMonitor.setCurrentlyPlayingSongID(songIdToPlay);
            QueueActionMessage queueActionMessage = new QueueActionMessage(Action.STARTED_TRACK, -1);
            hostMonitor.addAction(queueActionMessage);

            String musicFileName = songList.get(songIdToPlay);
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            try {
                mediaPlayer.setDataSource(folderPath + musicFileName);
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.v("<----------------->", "Got IOException  in mediaplayer");
            }

            hostMusicQueue.startingSong();
            mediaPlayer.start();
            boolean finishedWithSong = false;
            PlayerCommand nextCommand;
            while (!finishedWithSong) {
                PlayerCommand command = null;
                try {
                    command = hostMusicQueue.waitForFinishedSongOrCommand();
                    if (command == PlayerCommand.NOTHING) {
                        // we recived no command, the song just finished so we exit the loop
                        break;
                    }
                    switch (command) {
                        case STOP:
                            hostMusicQueue.finishedSong();
                            queueActionMessage = new QueueActionMessage(Action.STOPPED_TRACK, -1);
                            hostMonitor.addAction(queueActionMessage);
                            mediaPlayer.stop();
                            mediaPlayer.prepare();
                            mediaPlayer.seekTo(0);
                            nextCommand = hostMusicQueue.waitForCommand();
                            if (nextCommand == PlayerCommand.PLAY) {
                                queueActionMessage = new QueueActionMessage(Action.STARTED_TRACK, -1);
                                hostMonitor.addAction(queueActionMessage);
                                mediaPlayer.start();
                                hostMusicQueue.startingSong();
                            } else {

                                hostMusicQueue.finishedSong();
                                finishedWithSong = true;
                            }
                            break;

                        case PAUSE:
                            hostMusicQueue.finishedSong();
                            mediaPlayer.pause();
                            nextCommand = hostMusicQueue.waitForCommand();
                            if (nextCommand == PlayerCommand.PLAY) {
                                mediaPlayer.start();
                                hostMusicQueue.startingSong();

                            } else {
                                mediaPlayer.stop();
                                mediaPlayer.prepare();
                                hostMusicQueue.finishedSong();
                                finishedWithSong = true;
                            }
                            break;
                        case NEXT:
                            mediaPlayer.stop();
                            mediaPlayer.prepare();
                            hostMusicQueue.finishedSong();
                            finishedWithSong = true;
                            break;
                        default:
                            break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                hostMusicQueue.setCommand(PlayerCommand.NOTHING);
            }
            queueActionMessage = new QueueActionMessage(Action.FINISHED_CURRENT_TRACK, -1);
            hostMonitor.addAction(queueActionMessage);
            hostMonitor.setCurrentlyPlayingSongID(-1);

        }

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        hostMusicQueue.finishedSong();
    }
}

