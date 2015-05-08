package ClientMain;

import android.widget.ListView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;



import connection.ClientFromHostReaderThread;
import connection.HostAddressRetriever;
import connection.InvalidResponseException;
import monitor.ClientMonitor;

public class UserClient implements Runnable{
	private static ClientMonitor clientMonitor;
	private String serverIp;
	private int serverPort;
    private int hostId;

	public UserClient(String serverIp,int serverPort, int hostId){
        this.hostId=hostId;
		this.serverIp=serverIp;
		this.serverPort=serverPort;
		clientMonitor=new ClientMonitor();
	}
    public ClientMonitor getMonitor(){
        return clientMonitor;
    }

	public void run(){
		HostAddressRetriever har=new HostAddressRetriever(serverIp,serverPort);
		try {
			InetSocketAddress hostAddress=har.retreiveHostAddress(hostId);
			System.out.println("We got the host address:");
			System.out.println(hostAddress);
			Socket socket = new Socket(hostAddress.getHostString(),hostAddress.getPort());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            System.out.println("starting listener");
            ClientFromHostReaderThread hostListener=new ClientFromHostReaderThread(socket.getInputStream(),clientMonitor);
			hostListener.start();
			System.out.println("Q + track id for queing, 'list' for requesting the whole que");
			while(true){
                String line="";
                try {
                    line=clientMonitor.getMessage();
                    writer.write(line+"\n");
                    writer.flush();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //System.out.println("what command do you want to send");
				//	String line = keyboard.nextLine();
				//	String line = keyboard.nextLine();
				//	int songID = keyboard.nextInt();
				//	writer.write(line+"\n");
				//	writer.flush();
				System.out.println("request sent to host: "+line);
				
			}
		
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

