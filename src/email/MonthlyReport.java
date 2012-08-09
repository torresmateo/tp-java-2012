package email;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import conncheck.ServerMonitor;

public class MonthlyReport extends Thread{
	
	
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
		ServerMonitor currentServer;
		while(!die){
			Iterator<ServerMonitor> itr = serverList.iterator();
			while(itr.hasNext()){
				currentServer = itr.next();
				if(true)//si ya paso la fecha en que hay que mandar el mail
					this.sendMonthlyEmail(currentServer);
			}
			try {
				sleep(60*60000);//dormir una hora
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
