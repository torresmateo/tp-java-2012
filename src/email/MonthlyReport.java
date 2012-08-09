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


import conncheck.ServerMonitor;
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
		totalTime = lastTime.getTime() - initialTime.getTime();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(initialTime.getTime());
		java.util.Date initialDate = cal.getTime();
		cal.setTimeInMillis(lastTime.getTime());
		java.util.Date finalDate = cal.getTime();
		
		return "El servicio con direccion " + address + " y puerto " + port + "estuvo activo el " + (upTime/totalTime)*100 + "% del tiempo entre " + initialDate + " y " + finalDate;
	}
	
	String generateStatistics(Properties serverInfo){
		return "cada un anho explota la nutria!!";
	}
	
	void sendMonthlyEmail(ServerMonitor currentServer){
		String mailBody;
		Properties serverInfo = currentServer.getServerInfo();
		MonitorEMail mail = new MonitorEMail();
		mail.addRecipient(serverInfo.getProperty("email_notification"));
		mail.setSubject("Alerta del Sistema de Monitoreo de Conecciones");
		mailBody = generateStatistics(serverInfo);
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
		
		try {
			sysVarList = db.selectSysVarObjByName("NEXT_REPORT_DATE");
			if(sysVarList.isEmpty()){
				db.insertSysVarObj(new SysVar("NEXT_REPORT_DATE", dateFormat.format(new java.util.Date())));
			}
		} catch (SQLException e1) {
			logger.error("Error SQL: " + e1.getMessage() + e1.getStackTrace());
		}
		
		while(!die){
			Iterator<ServerMonitor> itr = serverList.iterator();
			while(itr.hasNext()){
				currentServer = itr.next();
				try{
					nextReportDateString = ((db.selectSysVarObjByName("NEXT_REPORT_DATE")).get(0)).getValue();
					nextReportDate = new Timestamp(dateFormat.parse(nextReportDateString).getTime());
					today = new Timestamp(new java.util.Date().getTime());
					
					if( nextReportDate.getTime() < today.getTime() ){//si ya paso la fecha en que hay que mandar el mail
						this.sendMonthlyEmail(currentServer);
						mailSent = true;
					}
				} catch (SQLException e) {
					logger.error("Error SQL: " + e.getMessage() + e.getStackTrace());
				} catch (ParseException e) {
					logger.error("Parser Error: " + e.getMessage() + e.getStackTrace());
				}
			}
			
			if( mailSent ){
				try{
					nextReportDateString = ((db.selectSysVarObjByName("NEXT_REPORT_DATE")).get(0)).getValue();
					nextReportDate = new Timestamp(dateFormat.parse(nextReportDateString).getTime());
					today = new Timestamp(new java.util.Date().getTime());
					cal.setTimeInMillis(today.getTime());
					int month = cal.get(Calendar.MONTH);//mes actual
					int year = cal.get(Calendar.YEAR);//a–o actual
					cal.set(year, month + 1, 1);
					
					nextReportDate.setTime(cal.getTimeInMillis());
					
					db.updateSysVar("NEXT_REPORT_DATE", nextReportDate.toString());
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
