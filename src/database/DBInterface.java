package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * @author torresmateo
 * 
 * Interfaz entre la base de datos y el sistema.
 */

public class DBInterface {
	Connection conn;
	//========================================================================
	//						Bitacora
	//========================================================================
	public ResultSet selectBitacora(String where) throws SQLException {
		String sql = "select * from bitacora_servicios where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ResultSet selectAllBitacora() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from bitacora_servicios");
		return rs;
	}
	
	public void deleteBitacora(String where) throws SQLException{
		//TODO esta funcion deberia de recibir un objeto que eliminar de la base de datos y aca 
		//se debe preparar el sql con las propiedades de ese objeto entero.
		String sql = "delete from bitacora_servicios where " + where;
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
	}
	
	
	//========================================================================
	//						Variables del Sistema
	//========================================================================
		
	//TODO la misma onda, hacer que funcione con objetos
	
	public ResultSet selectSysVar(String where) throws SQLException{
		String sql = "select * from sys_vars where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public void deleteSysVar(String where) throws SQLException{
		String sql = "delete from sys_vars where " + where;
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
	}
	
}
