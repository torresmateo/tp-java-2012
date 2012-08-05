package conncheck;

import java.net.Socket;

public class ConnectionChecker {
	private Socket socket;
	private int port;
	private String host;
	ConnectionChecker(String host, int port){
		this.host = host;
		this.port = port;
		try{
			socket = new Socket(this.host, this.port);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public boolean Check(){
		return true;
	}
}
