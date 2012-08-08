package parser;

import java.io.File;

public class Directory {
	
	//Directorio de los archivos de configuración
	public String DIR_PATH;
	
	public Directory(String dir){
		this.DIR_PATH=dir;
	}
	
	
	public File[] list(){
		File file = new File(this.DIR_PATH);
		if(file.exists()){
			File files[] = file.listFiles();
			return files;
		}
		return null;
	}
}
