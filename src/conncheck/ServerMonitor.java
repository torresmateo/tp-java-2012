package conncheck;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class ServerMonitor extends Thread{
	
	private Properties serverInfo;
	
	public ServerMonitor(Properties serverInfo){
		this.serverInfo = serverInfo;
	}
	
	public void run(){
		ArrayList<Integer> portsList = stringToPortArray(serverInfo.get("ports_list").toString());
		System.out.println(serverInfo.get("ports_list"));
		Iterator<Integer> itr = portsList.iterator();
		while(itr.hasNext()){
			System.out.println(itr.next());
		}
	}
	
	
	private ArrayList<Integer> stringToPortArray(String portList){
		ArrayList<Integer> returnArray = new ArrayList<Integer>();
		String[] strList = portList.split(",");
		for(int i=0; i < strList.length; i++){
			returnArray.add(Integer.parseInt(strList[i]));
		}
		return returnArray;
	}
	

}
