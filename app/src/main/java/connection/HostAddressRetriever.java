package connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class HostAddressRetriever {
	private String centralServerIp;
	private int centralServerPort;
	public HostAddressRetriever(String centralServerIp,int centralServerPort){
		this.centralServerIp=centralServerIp;
		this.centralServerPort=centralServerPort;
	}
	public InetSocketAddress retrieveHostAddress(int hostId) throws UnknownHostException, IOException, InvalidResponseException{

		Socket serverConnectionSocket=new Socket(centralServerIp,centralServerPort);
		OutputStream os=serverConnectionSocket.getOutputStream();
		InputStream is=serverConnectionSocket.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		br.readLine(); /*USED TO BYPASS LISTING OF HOSTS*/
		makeHostAddressRequest(os,hostId);
		InetSocketAddress hostAddress=retreiveHostAddressResponse(is);
		serverConnectionSocket.close();
		return hostAddress;
	}

	private void makeHostAddressRequest(OutputStream os,int hostId) throws IOException{
		BufferedWriter br=new BufferedWriter(new OutputStreamWriter(os));
		br.write(hostId+"\n");
		br.flush();
		System.out.println("Sent hostaddress request");
	}
	private InetSocketAddress retreiveHostAddressResponse(InputStream is) throws IOException,InvalidResponseException{
		BufferedReader br=new BufferedReader(new InputStreamReader(is));
		String hostAddressResponse;
		System.out.println("trying to read line");
		hostAddressResponse=br.readLine();
		System.out.println("Read line");
		InetSocketAddress hostAddress =createInetFromString(hostAddressResponse);
		return hostAddress;
	}
	
	private InetSocketAddress createInetFromString(String hostAddressResponse) throws InvalidResponseException{
		String[] ipAndPort=hostAddressResponse.split(":");
		if (ipAndPort.length!=2){
			String errorMsg="Response from CentralServer was not of the correct format: IP:port";
			errorMsg+="\n"+"Message received was: "+hostAddressResponse;
			throw new InvalidResponseException(errorMsg);
		}
		String ip=ipAndPort[0];
		try{
			int port=Integer.parseInt(ipAndPort[1].trim());
			InetSocketAddress hostAddress=new InetSocketAddress(ip, port);
			return hostAddress;
		}catch(NumberFormatException e){
			String errorMsg="Response from CentralServer did not contain a numeric port number";
			errorMsg+="\n"+"Message received was: "+hostAddressResponse;
			throw new InvalidResponseException(errorMsg);
		}
		
	}
}
