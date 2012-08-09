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
