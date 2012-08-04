package parser;

import utils.Properties;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class FileManager {

    HashMap<Properties, String> data;
    
	//SOLO para probar, se debe traer de una tabla
	public static final String DIR_PATH = "/home/samu/java_workspace/tp-java-2012/servers/";
	
	public FileManager(HashMap<Properties, String> data) {
		this.data = data;
	}
	
	/*
	 * El archivo a ser creado lleva el nombre de alias.properties
	 */
	
	public boolean save(){
		String server_file = DIR_PATH+data.get(Properties.ALIAS)+".properties";	
		File fichero = new File(server_file);

		if (fichero.exists())
			return false;
		else{
			Parser p = new Parser(server_file);
			p.writeProperties(data);
			return true;
		}
		
	}
}
