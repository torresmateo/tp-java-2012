package conncheck;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.sun.mail.iap.ConnectionException;
/**
 * Clase que hace el checkeo de la conecccion
 */
public class ConnectionChecker {
	private Socket socket;
	private int port;
	private String host;
	static Logger logger = Logger.getLogger(ServerMonitor.class);
	
	
	
	ConnectionChecker(String host, int port){
		PropertyConfigurator.configure("src/log4j.properties");
		this.host = host;
		this.port = port;
	}
	//verifica la coneccion
	public boolean Check(){
		boolean returnValue = true;
		try{//tratamos de conectarnos
			socket = new Socket();
			socket.connect(new InetSocketAddress(this.host, this.port),1000);
		}catch(SocketTimeoutException e){//handleamos el fallo en que no se pudo conectar por timeout
			logger.error("La coneccion " + this.host + ":" + this.port + " no se pudo establecer, Timeout Reached");
			returnValue = false;
		}catch(IOException e){
			logger.error("La coneccion " + this.host + ":" + this.port + " no se pudo establecer, Connection Refused");
			returnValue = false;
		}catch(Exception e){//otro tipo de errores
			logger.error(e.getStackTrace());
			returnValue = false;
		}finally{//cerramos el soket
			if (socket != null){
				try{
					socket.close();
				}catch(Exception e){//en caso en que el cierre de socket falle
					logger.error(e.getStackTrace());
				}
			}
		}
		return returnValue;
	}
	
	//ejemplo del uso de la clase
	public static void main(String args[]){
		ConnectionChecker connCheck = new ConnectionChecker("www.google.com",80);
		if(connCheck.Check()){
			System.out.println("hay coneccion");
		}else{
			System.out.println("NO hay coneccion");
		}
	}
	
	public int getPort() {
		return port;
	}
	public String getHost() {
		return host;
	}
	@Override
	public String toString() {
		return "ConnectionChecker [socket=" + socket + ", port=" + port
				+ ", host=" + host + "]";
	}
}
