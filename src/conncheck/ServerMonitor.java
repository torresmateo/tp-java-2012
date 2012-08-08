package conncheck;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import database.Bitacora;
import database.Conector;
import database.DBInterface;
import email.MonitorEMail;
import gui.MainApp;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class ServerMonitor extends Thread{
	
	private Properties serverInfo;
	static Logger logger = Logger.getLogger(ServerMonitor.class);
	
	public ServerMonitor(Properties serverInfo){
		this.serverInfo = serverInfo;
	}
	
	public void run(){
		Connection conPostgres = null;
		try{
			conPostgres = Conector.connectByFile(MainApp.POSTGRES_PROPERTIES_PATH);
		} catch (ClassNotFoundException e) {
			logger.error("No se encontro el driver");
		} catch (SQLException e) {
			logger.error("Error SQL: " + e.getMessage() + e.getStackTrace());
		}
		DBInterface db = null;
		if(conPostgres != null)
			db = new DBInterface(conPostgres);
		
		
		Iterator<ConnectionChecker>  itr;
		int checkInterval = Integer.parseInt(serverInfo.get("check_interval").toString());
		PropertyConfigurator.configure("src/log4j.properties");
		
		String targetMail = serverInfo.get("email_notification").toString();
		String alias = serverInfo.get("alias").toString();
		String address = serverInfo.get("address").toString();
		ArrayList<ConnectionChecker> connList = stringToPortArray(serverInfo.get("ports_list").toString(),address);
		
		MonitorEMail mail = new MonitorEMail();
		mail.addRecipient(targetMail);
	//	mail.addRecipient("minardifer@gmail.com");//te va a llegar el spam de la vida :)
		
		String mailBody = "";
		ConnectionChecker currentConnection;
		ArrayList<Bitacora> auxBitacoraList = new ArrayList<Bitacora>();
		java.util.Date date;
		java.sql.Date sqlDate;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S"); 
		while(true){//vamos a monitorizar este servidor mientras no nos maten la aplicacion
			try {
				itr = connList.iterator();
				while(itr.hasNext()){
					currentConnection = itr.next();
					if(!currentConnection.Check()){
						date = new java.util.Date();
						mail.setSubject("Alerta del Sistema de Monitoreo de Conecciones");
						mailBody = "Fallo la conexión con la siguiente configuración:\n";
						mailBody += "alias = " + alias + "\n";
						mailBody += "address = " + address + "\n";
						mailBody += "port = " + currentConnection.getPort() + "\n";
						mail.setBody(mailBody);
						auxBitacoraList = db.selectBitacoraObj(" email = '"+ targetMail +"' and alias = '" + alias + "' order by marca_tiempo desc limit 1 offset 0");
						System.out.println("watahells" + auxBitacoraList);
						sqlDate = new java.sql.Date( df.parse( auxBitacoraList.get(0).getMarcaTiempo()).getTime());
						System.out.println("watahells" + df.format(sqlDate));
						
						db.insertBitacoraObj(new Bitacora(alias, 
															currentConnection.getHost(), 
															currentConnection.getPort(), 
															targetMail, 
															"DOWN",""));
						logger.error("Enviado Email de Error a " + targetMail);
						auxBitacoraList.clear();
					//	mail.sendEMail();
					}else{
						logger.info("CheckCorrecto: " + currentConnection.toString());
					}
					
				}
				sleep(checkInterval*60000);//esperamos la cantidad configurada de tiempo para volver a hacer el check
			} catch (InterruptedException e) {
				System.err.println("Error en el ServerMonitor");
				e.printStackTrace();
			} catch (SQLException e) {
				logger.error("Error SQL: " + e.getMessage() + e.getStackTrace());
			} catch (ParseException e) {
				logger.error("Parse Error: " + e.getMessage() + e.getStackTrace());
			}
		}
	}
	
	
	private ArrayList<ConnectionChecker> stringToPortArray(String portList, String address){
		ArrayList<ConnectionChecker> returnArray = new ArrayList<ConnectionChecker>();
		String[] strList = portList.split(",");
		for(int i=0; i < strList.length; i++){
			returnArray.add(new ConnectionChecker(address, Integer.parseInt(strList[i])));
		}
		return returnArray;
	}
	

}
