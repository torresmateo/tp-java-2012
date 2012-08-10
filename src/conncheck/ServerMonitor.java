package conncheck;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import database.Bitacora;
import database.Conector;
import database.ConnectionStatus;
import database.DBInterface;
import email.MonitorEMail;
import gui.MainApp;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import parser.FileManager;

import utils.ServerProperties;

// TODO 
// borar todos los println 
// descomentar los sleeps
// borrar el sleep(0)
// descomentar el mail.sendEMail()

public class ServerMonitor extends Thread{
	
	private Properties serverInfo;
	static Logger logger = Logger.getLogger(ServerMonitor.class);
	private boolean die = false;
	MainApp father;
	
	private static final int down = 0;
	private static final int ok = 1;
	
	public void setDie(boolean die) {
		this.die = die;
	}

	public Properties getServerInfo() {
		return serverInfo;
	}

	public ServerMonitor(Properties serverInfo, MainApp father){
		this.serverInfo = serverInfo;
		this.father = father;
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
		
		// hash para anotar los nuevos valores en el .properties
		HashMap<ServerProperties, String> data = new HashMap<ServerProperties, String>();
		
		// Se asignan los valores iniciales al data
		data.put(ServerProperties.ADDRESS, serverInfo.get("address").toString());
    	data.put(ServerProperties.ALIAS, serverInfo.get("alias").toString());
    	data.put(ServerProperties.CHECK_INTERVAL, serverInfo.get("check_interval").toString());
    	data.put(ServerProperties.EMAIL_NOTIF, serverInfo.get("email_notification").toString());
    	data.put(ServerProperties.HOSTNAME, serverInfo.get("host_name").toString());
    	data.put(ServerProperties.MAX_CHECK_ATTEMPTS, serverInfo.get("max_check_attempts").toString());
    	data.put(ServerProperties.NOTIF_INTERVAL, serverInfo.get("notification_interval").toString());
    	data.put(ServerProperties.PORTS_LIST, serverInfo.get("ports_list").toString());
    	data.put(ServerProperties.RETRY_INTERVAL, serverInfo.get("retry_interval").toString());
    	data.put(ServerProperties.TOLERANCE_ATTEMPTS, serverInfo.get("tolerance_attempts").toString());  
    	data.put(ServerProperties.LAST_CHECK, serverInfo.get("last_check").toString());  
    	data.put(ServerProperties.LAST_NOTIF, serverInfo.get("last_notification").toString());  
    	data.put(ServerProperties.CURRENT_STATE, serverInfo.get("current_state").toString());  
    	boolean connectionsOK;
		
		String targetMail = serverInfo.get("email_notification").toString();
		String alias = serverInfo.get("alias").toString();
		String address = serverInfo.get("address").toString();
		ArrayList<ConnectionChecker> connList = stringToPortArray(serverInfo.get("ports_list").toString(),address);
		
		MonitorEMail mail = new MonitorEMail();
		mail.addRecipient(targetMail);
		
		String mailBody = "";
		ConnectionChecker currentConnection;
		ArrayList<Bitacora> auxBitacoraList = new ArrayList<Bitacora>();
		ArrayList<ConnectionStatus> connectionStatusTable = new ArrayList<ConnectionStatus>();
		Timestamp date;
		Timestamp sqlDate;
		//SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
		long diff = 0;
		int availableRetryAttempts;
		
		itr = connList.iterator();
		while(itr.hasNext()){
			currentConnection = itr.next();
			currentConnection.setAttemptsRemainig(toleranceAttempts);
		}
		
		//control de last_check despues de iniciar App
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Timestamp today;
		Timestamp last_check;
		Timestamp last_check_int;
		long check_int_ms=checkInterval*60*1000;
		try {
			
			last_check = new Timestamp(dateFormat.parse(data.get(ServerProperties.LAST_CHECK)).getTime());
			//al last_check se suma el check interval para el control
			last_check_int = new Timestamp(last_check.getTime()+check_int_ms);
			today = new Timestamp(new java.util.Date().getTime());
			if(today.getTime() > last_check_int.getTime()){ //si el tiempo actual es mayor al last_check mas el check interval
				long diff1=today.getTime() - last_check.getTime();
				int hour = (int) ((diff1 / 1000) / 3600);
				int minutes = (int) (((diff1 / 1000) / 60) % 60);
				int seconds = (int) ((diff1 / 1000) % 60);
				java.sql.Date fecha = new java.sql.Date(last_check.getTime());
				java.sql.Time hora = new java.sql.Time(last_check.getTime());
				mail.setSubject("Alerta del Sistema de Monitoreo de Conecciones");
				mailBody = "El servicio de monitoreo estuvo apagado. El último chequeo para la " +
						"configuración: \n";
				mailBody += "alias = " + alias + "\n";
				mailBody += "address = " + address + "\n";
				mailBody += "check interval = " + checkInterval + " minutos\n";
				mailBody += "fue realizado en fecha "+fecha+" y hora "+hora+".\n";
				mailBody += "Existe una diferencia de " + hour+":"+minutes+":"+seconds;
				mailBody += " con relación al último chequeo.\n";
				mail.setBody(mailBody);
				mail.sendEMail();
			}
		} catch (ParseException e1) {
			logger.error("Last check date format invalid to server: "+data.get(ServerProperties.ALIAS));
		}
		
		//se utiliza la variable die para poder terminar la ejecucion del thread desde MainApp
		while(!die){
			try {
				connectionsOK = true;
				//iteramos la lista de conexiones, cada una de las cuales va a checkear un puerto en este servidor
				itr = connList.iterator();
				while(itr.hasNext()){
					currentConnection = itr.next();
					System.out.println(currentConnection.getPort());
					// se anota el tiempo del ultimo chequeo
					data.put(ServerProperties.LAST_CHECK, (new Timestamp(new java.util.Date().getTime())).toString());
					if(!currentConnection.Check()){//si la conexion con el puerto de esta iteracion falla
						System.out.println(" -> check FALLIDO");
						availableRetryAttempts = maxCheckAttempts;//reseteamos la cantidad de attempts disponibles con los que contamos
						while(availableRetryAttempts > 0){//mientras no lleguemos al max_check_attempts
							sleep(retryInterval*60000);
							System.out.println(" -> -> sleep de "+retryInterval+" mins");
							// se anota el tiempo del ultimo chequeo
							data.put(ServerProperties.LAST_CHECK, (new Timestamp(new java.util.Date().getTime())).toString());
							if(!currentConnection.Check()){//retry
								System.out.println(" -> -> retry FALLIDO");
								availableRetryAttempts--;//contamos con un retry menos
								logger.error("Retry de la coneccion" + currentConnection);
							}else{
								System.out.println(" -> -> retry EXITOSO");
								break;//salimos del while
							}
						}
						//si salimos sin utilizar el break significa que utilizamos todos los attempts
						if(availableRetryAttempts == 0){
							
							// se anota la caida del servicio
							connectionsOK = false;
							connectionStatusTable = db.selectConnectionStatus(" address = '" + address + "' AND  port = '" + currentConnection.getPort() + "' ORDER BY date DESC LIMIT 1");
							if( connectionStatusTable.isEmpty() || connectionStatusTable.get(0).getStatus() != down){
								db.insertConnectionStatus(new ConnectionStatus(serverInfo.getProperty("address"), currentConnection.getPort(), down, new Timestamp(new java.util.Date().getTime()) ));
							}
							
							//obtenemos la fecha actual
							date = new Timestamp(new java.util.Date().getTime());
							
							//obtenemos la diferencia de tiempo entre la ultima notificacion enviada y el notification interval
							auxBitacoraList = db.selectBitacoraObj(" email = '"+ targetMail +"' and alias = '" + alias + "' and puerto = " + currentConnection.getPort() + " order by marca_tiempo desc limit 1 offset 0");
							if(!auxBitacoraList.isEmpty()){
								sqlDate = auxBitacoraList.get(0).getMarcaTiempoDate();
								diff = date.getTime() - sqlDate.getTime();//esta variable contiene en milisegundos la diferencia entre la ultima vez enviada una alerta y la fecha actual
							}else{//en caso de que no se haya enviado antes una notificacion
								diff = notificationInterval*60000;
							}

							//si el tiempo que paso es mayor o igual al tiempo que se definio como notification interval 
							//y ademas estamos la ultima instancia de tolerance_attempts enviamos el mail de alerta 
							if(diff >= notificationInterval*60000 && currentConnection.getAttemptsRemainig() == 1){
								
								currentConnection.setAttemptsRemainig(toleranceAttempts);
								//se prepara el mensaje de la alerta
								mail.setSubject("Alerta del Sistema de Monitoreo de Conecciones");
								mailBody = "Fallo la conexion con la siguiente configuracion:\n";
								mailBody += "alias = " + alias + "\n";
								mailBody += "address = " + address + "\n";
								mailBody += "port = " + currentConnection.getPort() + "\n";
								mail.setBody(mailBody);
								
								//insertamos a la base de datos la alerta a enviar
								db.insertBitacoraObj(new Bitacora(serverInfo.get("alias").toString(), currentConnection.getHost(), currentConnection.getPort(), targetMail, "DOWN",""));
								logger.error("Enviado Email de Error a" + targetMail);
								System.out.println(" -------> MADAR MAIL");
								mail.sendEMail();
								// se anota el tiempo de la ultima notificacion enviada
								data.put(ServerProperties.LAST_NOTIF, (new Timestamp(new java.util.Date().getTime())).toString());
							}else{//en este caso no importa si no llegamos al notification interval igual descontamos la tolerancia al siguiente fallo (poner el status como DOWN)
								if(currentConnection.getAttemptsRemainig() > 1) currentConnection.setAttemptsRemainig(currentConnection.getAttemptsRemainig() - 1); 
							}
							auxBitacoraList.clear();
						}else{
							logger.info("RetryCorrecto: " + currentConnection.toString());
							currentConnection.setAttemptsRemainig(toleranceAttempts);
						}
					}else{//conexion con la iteracion actual es exitosa
						// se anota que el estado del servicio es OK
						connectionStatusTable = db.selectConnectionStatus(" address = '" + address + "' AND port = '" + currentConnection.getPort() + "' ORDER BY date DESC LIMIT 1");
						if( connectionStatusTable.isEmpty() || connectionStatusTable.get(0).getStatus() != ok){
							db.insertConnectionStatus(new ConnectionStatus(serverInfo.getProperty("address"), currentConnection.getPort(), ok, new Timestamp(new java.util.Date().getTime()) ));
						}
						System.out.println(" -> check EXITOSO");
						logger.info("CheckCorrecto: " + currentConnection.toString());
						currentConnection.setAttemptsRemainig(toleranceAttempts);
					}
				}
				
				if(connectionsOK){
					data.put(ServerProperties.CURRENT_STATE, "OK");
				}else{
					data.put(ServerProperties.CURRENT_STATE, "DOWN");
				}
				System.out.println("update desde el thread");
				FileManager.update(data.get(ServerProperties.ALIAS)+".properties",data);
				father.readServerFilesWithoutServerMonitorCall();
				father.refreshTreeModel();
				System.out.println("==> CURRENT_STATE="+data.get(ServerProperties.CURRENT_STATE));
				sleep(checkInterval*60000);//esperamos la cantidad configurada de tiempo para volver a hacer el check
				System.out.println("sleep de "+checkInterval+" mins");
				System.out.println();
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

	@Override
	public String toString() {
		return "ServerMonitor [serverInfo=" + serverInfo + ", die=" + die + "]";
	}
	

}