package conncheck;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
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
		PropertyConfigurator.configure("src/log4j.properties");//cargamos la configuracion del logger
		
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
		int retryInterval = Integer.parseInt(serverInfo.get("retry_interval").toString());
		int maxCheckAttempts = Integer.parseInt(serverInfo.get("max_check_attempts").toString());
		int notificationInterval = Integer.parseInt(serverInfo.get("notification_interval").toString());
		int toleranceAttempts = Integer.parseInt(serverInfo.get("tolerance_attempts").toString());
		int toleranceAttemptsOriginalValue = toleranceAttempts;
		
		String targetMail = serverInfo.get("email_notification").toString();
		String alias = serverInfo.get("alias").toString();
		String address = serverInfo.get("address").toString();
		ArrayList<ConnectionChecker> connList = stringToPortArray(serverInfo.get("ports_list").toString(),address);
		
		MonitorEMail mail = new MonitorEMail();
		mail.addRecipient(targetMail);
		
		String mailBody = "";
		ConnectionChecker currentConnection;
		ArrayList<Bitacora> auxBitacoraList = new ArrayList<Bitacora>();
		Timestamp date;
		Timestamp sqlDate;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		long diff = 0;
		int availableAttempts;
		
		while(true){//vamos a monitorizar este servidor mientras no nos maten la aplicacion
			try {
				itr = connList.iterator();
				while(itr.hasNext()){
					currentConnection = itr.next();
					if(!currentConnection.Check()){
						availableAttempts = maxCheckAttempts;
						while(availableAttempts > 0){
							availableAttempts--;
							date = new Timestamp(new java.util.Date().getTime());
							mail.setSubject("Alerta del Sistema de Monitoreo de Conecciones");
							mailBody = "Fallo la coneccion con la siguiente configuracion:\n";
							mailBody += "alias = " + alias + "\n";
							mailBody += "address = " + address + "\n";
							mailBody += "port = " + currentConnection.getPort() + "\n";
							mail.setBody(mailBody);
							
							//obtenemos la diferencia de tiempo entre la ultima notificacion enviada y el notification interval
							auxBitacoraList = db.selectBitacoraObj(" email = '"+ targetMail +"' and alias = '" + alias + "' order by marca_tiempo desc limit 1 offset 0");
							if(!auxBitacoraList.isEmpty()){
								sqlDate = auxBitacoraList.get(0).getMarcaTiempoDate();
								diff = date.getTime() - sqlDate.getTime();//esta variable contiene en milisegundos la diferencia entre la ultima vez enviada una alerta y la fecha actual
							}else{//en caso de que no se haya enviado antes una notificacion
								diff = notificationInterval*60000;
							}
							
							if(diff >= notificationInterval*60000 && toleranceAttempts == 1){//si el tiempo que paso es mayor o igual al tiempo que se definio como notification interval enviamos el mail de alerta
								toleranceAttempts = toleranceAttemptsOriginalValue;//hacemos a reset de la tolerancia
								System.out.println("hay que mandar la alerta");
								System.out.println(diff);
								db.insertBitacoraObj(new Bitacora(serverInfo.get("alias").toString(), 
																	currentConnection.getHost(), 
																	currentConnection.getPort(), 
																	targetMail, 
																	"DOWN",""));
								logger.error("Enviado Email de Error a" + targetMail);
								//mail.sendEMail();
							}else{//en este caso no importa si no llegamos al notification interval igual decontamos la tolerancia al siguiente fallo
								if(toleranceAttempts > 1) toleranceAttempts--; 
							}
							auxBitacoraList.clear();
							sleep(retryInterval*60000);
						}
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