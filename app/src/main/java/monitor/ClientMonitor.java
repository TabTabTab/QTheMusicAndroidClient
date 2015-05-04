package monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import MusicQueue.ClientMusicQueue;


public class ClientMonitor implements ConnectionMonitor{
	private String hostAddress=null;
	private ClientMusicQueue musicQueue;
    private LinkedList<String> messagesToHost;
	public ClientMonitor(){
		musicQueue=null;
        messagesToHost=new LinkedList<String>();
	}
    public synchronized ClientMusicQueue getMusicQueue() throws InterruptedException {
        while(musicQueue==null){
            wait();
        }
        return musicQueue;
    }
	public synchronized void setMusicQueue(ClientMusicQueue musicQueue){
		this.musicQueue=musicQueue;
		notifyAll();
	}
	public synchronized void write(String data) {
		// TODO Auto-generated method stub
		
	}

	public synchronized String read() throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Deposits a hostAddress into the monitor. The address may 
	 * not be valid but the validity of it will be checked in the getHostAddress() call.
	 * @param hostAddress - A String containing a host Address, 
	 */
	public synchronized void depositHostAddress(String hostAddress){
		this.hostAddress=hostAddress;
		notifyAll();
	}
	/**
	 * Returns an INetAddress belonging to the host, this address is gathered from the centralServer.
	 * 
	 * Waits until a hostAddress is available
	 * @return An InetAddress belonging to the host. In case something went wrong in the retrieval of the address, 
	 * null is returned.
	 * @throws InterruptedException
	 */
	public synchronized InetAddress getHostAddress() throws InterruptedException{
		while(hostAddress==null){
			wait();
		}
		if (hostAddress.equals("-1")){
			hostAddress = null;
			return null;
		}
		InetAddress address=null;
		try {
			address = InetAddress.getByName(hostAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		hostAddress = null;
		return address;
	}
    public synchronized ArrayList<String> getAvailableTracks() throws InterruptedException {
        while(musicQueue==null){
            System.out.println("waiting...");
            wait();
        }
        System.out.println("finished waintng");
        return musicQueue.getAvailableTracks();
    }
    public synchronized String getMessage() throws InterruptedException {
        while(messagesToHost.isEmpty()){
            wait();
        }
        String msg=messagesToHost.pop();
        return msg;
    }
    public synchronized void queueTrack(int trackIndex){
        String msg="Q "+trackIndex;
        messagesToHost.add(msg);
        notifyAll();
    }
}
