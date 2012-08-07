package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Properties;

import parser.Parser;

public class Conector {
	/**
	 * 
	 * Conecta a la base de datos usando los parametros obtenidos del archivo de
	 * configuracion nombrado file
	 * 
	 * @parm file nombre del archivo.
	 * @return la conexion a base de datos.
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * 
	 */
	public static Connection connectByFile(String file) throws SQLException,
			ClassNotFoundException {

		Parser p = new Parser(file);
		Properties prop = p.readProperties();
		
		String driverName = prop.getProperty("Driver");
		String url = prop.getProperty("Url");
		String userName = prop.getProperty("UserName");
		String pass = prop.getProperty("Password");

		Class.forName(driverName);

		Connection con = DriverManager.getConnection(url, userName, pass);

		return con;
	}
	
	public static Connection connectByHash(Hashtable<String, String> info) throws SQLException,
	ClassNotFoundException {
		
		String driverName = info.get("Driver");
		String url = info.get("Url");
		String userName = info.get("UserName");
		String pass = info.get("Password");
		
		Class.forName(driverName);
		
		Connection con = DriverManager.getConnection(url, userName, pass);
		
		return con;
}

}
