package parser;

import java.io.*;
import java.util.*;

public class Parser{
	
	public Properties prop;
	public String file;
	
	/*
	 * Constructor de la clase Parser
	 * @param File Ruta de configuración del servidor.
	 */
	public Parser(String file){
		this.prop = new Properties();
		this.file = file;
	}
	
	public Properties readProperties(){
		InputStream is = null;
		
		try {
			is = new FileInputStream(this.file);
			prop.load(is);
		/*	for (Enumeration<Object> e = prop.keys(); e.hasMoreElements();) {
				Object obj = e.nextElement();
				System.out.println(obj + "="+ prop.getProperty(obj.toString()));
			}*/
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return prop;
	}
	
	public void writeProperties(HashMap<utils.Properties, String> data){
		
		try {
			OutputStream output = new FileOutputStream(this.file);

			prop.put("address", data.get(utils.Properties.ADDRESS));
			prop.put("alias", data.get(utils.Properties.ALIAS));
			prop.put("check_interval", data.get(utils.Properties.CHECK_INTERVAL));
			prop.put("current_state", data.get(utils.Properties.CURRENT_STATE));
			prop.put("email_notification", data.get(utils.Properties.EMAIL_NOTIF));
			prop.put("host_name", data.get(utils.Properties.HOSTNAME));
			prop.put("last_check", data.get(utils.Properties.LAST_CHECK));
			prop.put("last_notificacion", data.get(utils.Properties.LAST_NOTIF));
			prop.put("max_check_attempts", data.get(utils.Properties.MAX_CHECK_ATTEMPTS));
			prop.put("notification_interval", data.get(utils.Properties.NOTIF_INTERVAL));
			prop.put("ports_list", data.get(utils.Properties.PORTS_LIST));
			prop.put("retry_interval", data.get(utils.Properties.RETRY_INTERVAL));
			prop.put("tolerance_attempts", data.get(utils.Properties.TOLERANCE_ATTEMPTS));
			
			prop.store(output, "server properties");
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}