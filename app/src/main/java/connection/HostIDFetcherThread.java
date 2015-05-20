package connection;

import android.util.Log;

import protocol.DebugConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import monitor.HostMonitor;


/**
 * This thread fetches an available ID from the central server and maintains
 * connection with said server until it is time to close the
 * application/hosting. It lets the monitor handle no available ID package.
 *
 * @author dat11sse
 */
public class HostIDFetcherThread extends Thread {

    private HostMonitor hostMonitor;
    private Socket socket;
private String serverIP;
    private int serverPort;

    public HostIDFetcherThread(String serverIP, int serverPort, HostMonitor hostMonitor) {
        this.hostMonitor = hostMonitor;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void run() {
        socket=new Socket();
        try {
            socket.connect(new InetSocketAddress(serverIP,serverPort),2000);
        } catch (IOException e1) {
            hostMonitor.setSuccessfulConnection(false);
            return;

        }
        boolean setUp = false;
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            int hostPort = DebugConstants.HOST_PORT;
            output.write(hostPort + System.lineSeparator());
            output.flush();
            String idResponse = input.readLine();
            // TODO: Handle no available id in monitor!
            int id = Integer.parseInt(idResponse);
            setUp = hostMonitor.setID(id);
            if (setUp) {
                hostMonitor.setSuccessfulConnection(true);
                hostMonitor.waitForServerConnectionToClose(socket);
            } else {
                hostMonitor.setSuccessfulConnection(false);
            }
        } catch (IOException e) {

            hostMonitor.setSuccessfulConnection(false);
        } catch (InterruptedException e) {
            hostMonitor.setSuccessfulConnection(false);
        }
        hostMonitor.setCentralServerAliveStatus(false);
    }
}
