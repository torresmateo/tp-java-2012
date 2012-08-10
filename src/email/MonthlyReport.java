package email;

import gui.MainApp;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;


import conncheck.ConnectionChecker;
import conncheck.ServerMonitor;
import database.Bitacora;
import database.Conector;
import database.ConnectionStatus;
import database.DBInterface;
import database.SysVar;

public class MonthlyReport extends Thread{
	
	
	static Logger logger = Logger.getLogger(ServerMonitor.class);
	
	private ArrayList<ServerMonitor> serverList;
	private boolean die = false;
	
	public MonthlyReport(ArrayList<ServerMonitor> serverList) {
		super();
		this.serverList = serverList;
	}
	
	public ArrayList<ServerMonitor> getServerList() {
		return serverList;
	}

	public void setServerList(ArrayList<ServerMonitor> serverList) {
		this.serverList = serverList;
	}

	public boolean isDie() {
		return die;
	}

	public void setDie(boolean die) {
		this.die = die;
	}

	String generateUptimeStatistics(ArrayList<ConnectionStatus> csList){
		ConnectionStatus currentCS;
		Timestamp initialTime = null;
		Timestamp lastTime = null;
		String address = "";
		int port = 0;
		boolean setInfo = true;
		boolean setInitial = true;
		boolean setLastTime = true;
		long upTime = 0;
		long totalTime = 0;
		
		Iterator<ConnectionStatus> itr = csList.iterator();
		while(itr.hasNext()){
			currentCS = itr.next();
			if(currentCS.getStatus() == 0){//si el estado es caido, sumamos el tiempo transcurrido 
				if(setLastTime){
					setLastTime = false;
					lastTime = currentCS.getDate();
				}
				upTime += currentCS.getDate().getTime() - lastTime.getTime();
			}else{
				if(setInitial){//anotamos la primera fecha en la que la conexion estuvo up
					setInitial = false;
					initialTime = currentCS.getDate();
				}
			}
			lastTime = currentCS.getDate();
			if(setInfo){
				setInfo = false;
				address = currentCS.getAddress();
				port = currentCS.getPort();
			}
		}
		
		if( initialTime == null ){
			return ""+0;
		}
		
		totalTime = lastTime.getTime() - initialTime.getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(initialTime.getTime());
//		java.util.Date initialDate = cal.getTime();
		cal.setTimeInMillis(lastTime.getTime());
//		java.util.Date finalDate = cal.getTime();
		if(totalTime != 0){
			return ""+((upTime/totalTime)*100);
		}else{
			return ""+0;
		}
//		return "El servicio con direccion " + address + " y puerto " + port + "estuvo activo el " + (upTime/totalTime)*100 + "% del tiempo entre " + initialDate + " y " + finalDate;	
	}
	
	
	String generateStatistics(Properties serverInfo){
		Calendar cal = Calendar.getInstance();
		String statisticsString = new String();
		ArrayList<ConnectionStatus> connectionStatusTable;
		ArrayList<Bitacora> bitacoraTable;
		String address = serverInfo.getProperty("address");
		String[] strList = serverInfo.getProperty("ports_list").split(",");
		Timestamp timeNow = new Timestamp(new java.util.Date().getTime());
		Timestamp dateAux = new Timestamp(new java.util.Date().getTime());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		int year;
		int month;
		int day;
		
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
		
		statisticsString += "\naddress: " + address+"\n";
		
		try{
			
			for(int i=0; i < strList.length; i++){
				statisticsString += "\n\tport: " + strList[i] + "\n";
				
				connectionStatusTable = db.selectConnectionStatus(" address = '" + address + "' AND port = '" + strList[i] + "' ORDER BY date ");
				statisticsString += "\t\tPorcentaje de uptime: " + generateUptimeStatistics(connectionStatusTable) + " %\n";
				
				cal.setTimeInMillis(timeNow.getTime());
				day = cal.get(Calendar.DAY_OF_MONTH);//dia actual
				month = cal.get(Calendar.MONTH);//mes actual
				year = cal.get(Calendar.YEAR);//anho actual
				
				cal.set(year, month - 1, day);
				dateAux.setTime(cal.getTimeInMillis());
				
				bitacoraTable = db.selectBitacoraObj(" direccion_ip = '" + address + "' AND puerto = '" + strList[i] + "' AND marca_tiempo >= '" + dateFormat.format(dateAux) + "'");
				statisticsString += "\t\tPromedio de errores por dia: " + (bitacoraTable.size()/30.0) + "\n";
				
				bitacoraTable = db.selectBitacoraObj(" direccion_ip = '" + address + "' AND puerto = '" + strList[i] + "' AND marca_tiempo >= '" + dateFormat.format(dateAux) + "'");
				statisticsString += "\t\tPromedio de errores por semana: " + (bitacoraTable.size()/4.0) + "\n";
				
				cal.set(year, month - 3, day);
				dateAux.setTime(cal.getTimeInMillis());
				
				bitacoraTable = db.selectBitacoraObj(" direccion_ip = '" + address + "' AND puerto = '" + strList[i] + "' AND marca_tiempo >= '" + dateFormat.format(dateAux) + "'");
				statisticsString += "\t\tPromedio de errores por mes: " + (bitacoraTable.size()/3.0) + "\n";
				
				cal.set(year - 1, month, day);
				dateAux.setTime(cal.getTimeInMillis());
				
				bitacoraTable = db.selectBitacoraObj(" direccion_ip = '" + address + "' AND puerto = '" + strList[i] + "' AND marca_tiempo >= '" + dateFormat.format(dateAux) + "'");
				statisticsString += "\t\tPromedio de errores por año: " + (bitacoraTable.size()/1.0) + "\n";
			}
		} catch (SQLException e) {
			logger.error("Error SQL: " + e.getMessage() + e.getStackTrace());;
		}
		
		
		
		return statisticsString;
	}
	
	void sendMonthlyEmail(ServerMonitor currentServer){
		String mailBody;System.out.println("\n\n<<< ????? >>>\n\n");
		Properties serverInfo = currentServer.getServerInfo();
		MonitorEMail mail = new MonitorEMail();
		mail.addRecipient(serverInfo.getProperty("email_notification"));
		mail.setSubject("Alerta MENSUAL del Sistema de Monitoreo de Conecciones");
		mailBody = generateStatistics(serverInfo);
		System.out.println(mailBody);
		mail.setBody(mailBody);
		mail.sendEMail();
	}
	
	public void run(){
		Calendar cal = Calendar.getInstance();
		
		ServerMonitor currentServer;
		String nextReportDateString;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Timestamp today;
		Timestamp nextReportDate;
		ArrayList<SysVar> sysVarList = new ArrayList<SysVar>();
		boolean mailSent = false;
		
		
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
		
		// se verifica la existencia de la variable de sistema NEXT_REPORT_DATE, en caso contrario se crea una.
		try {
			sysVarList = db.selectSysVarObjByName("NEXT_REPORT_DATE");
			if(sysVarList.isEmpty()){
				db.insertSysVarObj(new SysVar( "NEXT_REPORT_DATE", dateFormat.format(new java.util.Date()) ));
			}
		} catch (SQLException e1) {
			logger.error("Error SQL: " + e1.getMessage() + e1.getStackTrace());
		}
		
		while(!die){
			// se itera por cada servidor
			Iterator<ServerMonitor> itr = serverList.iterator();
			while(itr.hasNext()){System.out.println("YYEEEESSS");
				currentServer = itr.next();
				try{
					// se extraen las fechas de hoy y la fecha NEXT_REPORT_DATE
					nextReportDateString = ((db.selectSysVarObjByName("NEXT_REPORT_DATE")).get(0)).getValue();
					nextReportDate = new Timestamp(dateFormat.parse(nextReportDateString).getTime());
					today = new Timestamp(new java.util.Date().getTime());
					
					// el reporte solo se envia cuando la fecha NEXT_REPORT_DATE ya a pasado
					if( nextReportDate.getTime() < today.getTime() ){
						this.sendMonthlyEmail(currentServer);
						mailSent = true; // se anota el envio del reporte
					}
				} catch (SQLException e) {
					logger.error("Error SQL: " + e.getMessage() + e.getStackTrace());
				} catch (ParseException e) {
					logger.error("Parser Error: " + e.getMessage() + e.getStackTrace());
				}
			}
			
			// en caso de haber mandado el reporte se debe actualizar NEXT_REPORT_DATE al primer dia del mes siguiente
			if( mailSent ){
				try{
					// se calcula la fecha del mes siguiente
					nextReportDateString = ((db.selectSysVarObjByName("NEXT_REPORT_DATE")).get(0)).getValue();
					nextReportDate = new Timestamp(dateFormat.parse(nextReportDateString).getTime());
					today = new Timestamp(new java.util.Date().getTime());
					cal.setTimeInMillis(today.getTime());
					int month = cal.get(Calendar.MONTH);//mes actual
					int year = cal.get(Calendar.YEAR);//anho actual
					cal.set(year, month + 1, 1);
					nextReportDate.setTime(cal.getTimeInMillis());
					
					// se actualiza la variable en la base de datos
					db.updateSysVar("NEXT_REPORT_DATE", dateFormat.format(nextReportDate));
				} catch (SQLException e) {
					logger.error("Error SQL: " + e.getMessage() + e.getStackTrace());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			try {
				sleep(60*60000);//dormir una hora
			} catch (InterruptedException e) {
				logger.error("Interrupted Error: " + e.getMessage() + e.getStackTrace());
			}
		}
	}
}




















