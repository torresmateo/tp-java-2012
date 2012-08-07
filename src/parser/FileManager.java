package parser;

import utils.ServerProperties;

import gui.MainApp;

import java.io.File;
import java.util.HashMap;

public class FileManager {
	
	/*
	 * Crea el archivo de configuración
	 */
	
	public static boolean save(HashMap<ServerProperties, String> data){
		//El archivo a ser creado lleva el nombre de alias.properties
		String server_file = data.get(ServerProperties.ALIAS)+".properties";	

		if (exists(server_file))
			return false;
		else{
			Parser p = new Parser(MainApp.getDIR_PATH()+server_file);
			p.writeProperties(data);
			return true;
		}
		
	}
	
	/*
	 * @param fileName Filename to update
	 * @param data Data for update
	 */
	
	public static boolean update(String fileName, HashMap<ServerProperties, String> data){
		String server_file = data.get(ServerProperties.ALIAS)+".properties";
		if(fileName.compareTo(server_file)!=0){
			if(exists(server_file))
				return false; //otro archivo de config de servidor se llama igual
		}
		remove(fileName);
		return save(data);	
	}
	
	/*
	 * @param fileName Filename to remove
	 */
	
	public static boolean remove(String fileName){
		File fichero = new File(MainApp.getDIR_PATH()+fileName);
		return fichero.delete();
	}
	
	/*
	 * @param fileName Filename to check
	 */
	
	public static boolean exists(String fileName){
		File fichero = new File(MainApp.getDIR_PATH()+fileName);
		if (fichero.exists())
			return true;
		else
			return false;
	}
	
}
