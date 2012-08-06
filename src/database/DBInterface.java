package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * @author torresmateo
 * 
 * Interfaz entre la base de datos y el sistema.
 */

public class DBInterface {
	Connection conn;
	
	public DBInterface(Connection conn){
		this.conn = conn;
	}
	
	public static void main(String[] args){
		DBInterface db = null;
		try{
			Drivers.cargarDrivers();
			Connection conPostgres = Conexiones.obtenerConexion(Conexiones.DBMS_TYPE_POSTGRES);
			db = new DBInterface(conPostgres);
		
			ResultSet rs = db.selectAllBitacora();
			while (rs.next()) {
				System.out.println("Alias " + rs.getString("alias"));
				System.out.println("IP " + rs.getString("direccion_ip"));
				System.out.println("Port " + rs.getString("puerto"));
				System.out.println("E-mail " + rs.getString("email"));
				System.out.println("Status " + rs.getString("estado"));
				
				System.out.println("--- --- ---");
			}
			
			Iterator<Bitacora> itr = db.selectAllBitacoraObj().iterator();
			while(itr.hasNext()){
				System.out.println(itr.next());
			}
			
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("No se encontro el driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("No se pudo conectar" + e.getMessage());
			e.printStackTrace();
		}
		
		
	}
	//========================================================================
	//						Bitacora
	//========================================================================
	//retorna un array de objetos
	public ArrayList<Bitacora> selectBitacoraObj(String where) throws SQLException {
		ArrayList<Bitacora> returnArray = new ArrayList<Bitacora>();
		String sql = "select * from bitacora_servicios where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			returnArray.add(new Bitacora(
						rs.getInt("id_bitacora_servicios"),
						rs.getString("alias"),
						rs.getString("direccion_ip"),
						rs.getString("puerto"),
						rs.getString("email"),
						rs.getString("estado")
					));
		}
		return returnArray;
	}
	
	public ResultSet selectBitacora(String where) throws SQLException {
		String sql = "select * from bitacora_servicios where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	public ArrayList<Bitacora> selectAllBitacoraObj() throws SQLException {
		ArrayList<Bitacora> returnArray = new ArrayList<Bitacora>();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from bitacora_servicios");
		while(rs.next()){
			returnArray.add(new Bitacora(
						rs.getInt("id_bitacora_servicios"),
						rs.getString("alias"),
						rs.getString("direccion_ip"),
						rs.getString("puerto"),
						rs.getString("email"),
						rs.getString("estado")
					));
		}
		return returnArray;
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
