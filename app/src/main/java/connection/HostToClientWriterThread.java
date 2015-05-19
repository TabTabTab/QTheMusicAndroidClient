package connection;

import java.io.IOException;

import monitor.HostMonitor;


/**
 * This thread gets all the message and the destinations for said message from
 * the monitor, in a wrapper class. Then it writes the message to the given
 * destinations.
 * 
 * @author dat11sse
 * 
 */
public class HostToClientWriterThread extends Thread {
	private HostMonitor hostMonitor;

	public HostToClientWriterThread(HostMonitor hostMonitor) {
		this.hostMonitor = hostMonitor;
	}

	public void run() {

		while (!isInterrupted()) {
			try {
				
				hostMonitor.sendData();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
