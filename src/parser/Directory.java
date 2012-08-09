package parser;

import java.io.File;
import java.io.FileFilter;

public class Directory {
	
	//Directorio de los archivos de configuración
	public String DIR_PATH;
	
	public Directory(String dir){
		this.DIR_PATH=dir;
	}
	
	
	public File[] list(){
		File file = new File(this.DIR_PATH);
		File files[] = file.listFiles(new PropertiesFileFilter());
		return files;
	}
	
	public class PropertiesFileFilter implements FileFilter
	{

	  public boolean accept(File file)
	  {
	      if (file.getName().toLowerCase().endsWith(".properties"))
	      {
	        return true;
	      }
	    return false;
	  }
	}
}
