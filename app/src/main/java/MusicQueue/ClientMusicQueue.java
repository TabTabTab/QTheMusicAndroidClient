package MusicQueue;

import java.util.ArrayList;


//bör fungera lite annorlunda än hostens, den nuvarande spelande låten borde ligga kvar här i, dvs ta bort låten när den spelats klart, inte när den börjats spela.
public class ClientMusicQueue extends MusicQueue{
    private boolean queueChanged=false;
	private boolean songIsPlaying = false;
	public ClientMusicQueue(ArrayList<String> availableTracks) {
		super(availableTracks);

	}

	public synchronized boolean addToQueue(int trackId){
        if(trackId>=0 && availableTracks.size()>trackId){
            trackQueue.add(trackId);
            queueChanged=true;
            notifyAll();
            return true;
        }else{
            return false;
        }
	}
	public synchronized void startingSong(){
        queueChanged=true;
		songIsPlaying=true;
        notifyAll();
	}
	public synchronized void stoppingSong(){
        queueChanged=true;
		songIsPlaying=false;
        notifyAll();
	}
	public synchronized void finishedSong(){
        queueChanged=true;
		songIsPlaying=false;
		trackQueue.remove(0);
        notifyAll();
	}

	public synchronized void printQueue(){
		boolean firstSong = true;
		for(int trackID :trackQueue){
			if(firstSong){
				if(songIsPlaying){
					System.out.println("Currently playing: "+availableTracks.get(trackID));
				}
				else{
					System.out.println(availableTracks.get(trackID));
				}
				firstSong=false;
			}
			else{
				System.out.println(availableTracks.get(trackID));
			}
		}
	}

    public synchronized ArrayList<String> waitForQueueChange() throws InterruptedException {
        while(!queueChanged){
            wait();
        }
        queueChanged=false;
        return getQueueTracks();
    }


	/**
	 * Replaces the queue with a new queue
	 * @param newQueue
	 */
	public void replaceQueue(ArrayList<Integer> newQueue){
		trackQueue=newQueue;
	}

}
