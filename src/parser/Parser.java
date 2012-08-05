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
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return prop;
	}
	
	public void writeProperties(HashMap<utils.ServerProperties, String> data){
		
		try {
			OutputStream output = new FileOutputStream(this.file);

			prop.put("address", data.get(utils.ServerProperties.ADDRESS));
			prop.put("alias", data.get(utils.ServerProperties.ALIAS));
			prop.put("check_interval", data.get(utils.ServerProperties.CHECK_INTERVAL));
			prop.put("current_state", data.get(utils.ServerProperties.CURRENT_STATE));
			prop.put("email_notification", data.get(utils.ServerProperties.EMAIL_NOTIF));
			prop.put("host_name", data.get(utils.ServerProperties.HOSTNAME));
			prop.put("last_check", data.get(utils.ServerProperties.LAST_CHECK));
			prop.put("last_notificacion", data.get(utils.ServerProperties.LAST_NOTIF));
			prop.put("max_check_attempts", data.get(utils.ServerProperties.MAX_CHECK_ATTEMPTS));
			prop.put("notification_interval", data.get(utils.ServerProperties.NOTIF_INTERVAL));
			prop.put("ports_list", data.get(utils.ServerProperties.PORTS_LIST));
			prop.put("retry_interval", data.get(utils.ServerProperties.RETRY_INTERVAL));
			prop.put("tolerance_attempts", data.get(utils.ServerProperties.TOLERANCE_ATTEMPTS));
			
			prop.store(output, "server properties");
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}