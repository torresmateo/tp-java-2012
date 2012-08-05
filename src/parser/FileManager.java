package parser;

import utils.Properties;

import java.io.File;
import java.util.HashMap;

public class FileManager {

	//Hashmap de la información del formulario
    HashMap<Properties, String> data;
	
	public FileManager(HashMap<Properties, String> data) {
		this.data = data;
	}
	
	/*
	 * Crea el archivo de configuración
	 */
	
	public boolean save(String DIR_PATH){
		//El archivo a ser creado lleva el nombre de alias.properties
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
