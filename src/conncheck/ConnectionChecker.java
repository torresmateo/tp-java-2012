package conncheck;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
/**
 * Clase que hace el checkeo de la conecccion
 */
public class ConnectionChecker {
	private Socket socket;
	private int port;
	private String host;
	ConnectionChecker(String host, int port){
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
			System.out.println("La coneccion no se pudo establecer, se llega al timeout");
			e.printStackTrace();
			returnValue = false;
		}catch(Exception e){//otro tipo de errores
			e.printStackTrace();
			returnValue = false;
		}finally{//cerramos el soket
			if (socket != null){
				try{
					socket.close();
				}catch(Exception e){//en caso en que el cierre de socket falle
					e.printStackTrace();
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
}
