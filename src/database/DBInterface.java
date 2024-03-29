package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
	/*
	public static void main(String[] args){
		DBInterface db = null;
		try{
			Connection conPostgres = Conector.connectByFile("src/postgres.properties");
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
			
			/*Bitacora myBitacora = new Bitacora("arsis","ip de arsis",50,"torresmateo@gmail.com","a","2012-08-06 19:45:51");
			
			db.insertBitacoraObj(myBitacora);*/
			/*
			SysVar sv = new SysVar("DIR_PATH", "Hello");
			db.insertSysVarObj(sv);*/
			
			/*
			ResultSet rs = db.selectSysVar("name=\'DIR_PATH\'");
			while (rs.next()) {
				System.out.println("id " + rs.getString("id_sys_vars"));
				System.out.println("name " + rs.getString("name"));
				System.out.println("value " + rs.getString("value"));
				
				System.out.println("--- --- ---");
			}
			
			
		} catch (ClassNotFoundException e) {
			System.out.println("No se encontro el driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("No se pudo conectar" + e.getMessage());
			e.printStackTrace();
		}
		
		
	}*/
	//========================================================================
	//						Bitacora
	//========================================================================
	
	
	//********************************** selectBitacora **********************************				
	
	//retorna un array de objetos
	public ArrayList<Bitacora> selectBitacoraObj(String where) throws SQLException {
		ArrayList<Bitacora> returnArray = new ArrayList<Bitacora>();
		String sql = "select * from bitacora_servicios where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			Bitacora auxBitacora = new Bitacora(
						rs.getInt("id_bitacora_servicios"),
						rs.getString("alias"),
						rs.getString("direccion_ip"),
						rs.getInt("puerto"),
						rs.getString("email"),
						rs.getString("estado"),
						rs.getString("marca_tiempo")
					);
			auxBitacora.setMarcaTiempoDate(rs.getTimestamp("marca_tiempo"));
			returnArray.add(auxBitacora);
		}
		return returnArray;
	}
	
	public ResultSet selectBitacora(String where) throws SQLException {
		String sql = "select * from bitacora_servicios where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		return rs;
	}
	
	//********************************** selectAllBitacora **********************************
	
	//retorna un array de objetos
	public ArrayList<Bitacora> selectAllBitacoraObj() throws SQLException {
		ArrayList<Bitacora> returnArray = new ArrayList<Bitacora>();
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from bitacora_servicios");
		while(rs.next()){
			Bitacora auxBitacora = new Bitacora(
					rs.getInt("id_bitacora_servicios"),
					rs.getString("alias"),
					rs.getString("direccion_ip"),
					rs.getInt("puerto"),
					rs.getString("email"),
					rs.getString("estado"),
					rs.getString("marca_tiempo")
				);
			auxBitacora.setMarcaTiempoDate(rs.getTimestamp("marca_tiempo"));
			returnArray.add(auxBitacora);
		}
		return returnArray;
	}
	
	public ResultSet selectAllBitacora() throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from bitacora_servicios");
		return rs;
	}
	
	
	//********************************** insertBitacora **********************************
	
	public void insertBitacoraObj(Bitacora newEntry) throws SQLException {
		String sql = "insert into bitacora_servicios (alias,direccion_ip,puerto,email,estado) values(?,?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, newEntry.getAlias());
		pstmt.setString(2, newEntry.getDireccionIP());
		pstmt.setInt(3, newEntry.getPuerto());
		pstmt.setString(4,newEntry.getEmail());
		pstmt.setString(5,newEntry.getEstado());
		pstmt.executeUpdate();
	}
	
	//********************************** deleteBitacora **********************************
	public void deleteBitacora(String where) throws SQLException{
		String sql = "delete from bitacora_servicios where " + where;
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
	}
	
	
	//========================================================================
	//						Variables del Sistema
	//========================================================================
	
	//********************************** selectSysVarObjByName **********************************
	
	public ArrayList<SysVar> selectSysVarObjByName(String name) throws SQLException{
		ArrayList<SysVar> returnArray = new ArrayList<SysVar>();
		String sql = "select * from sys_vars where name = \'" + name + "\'";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			returnArray.add(new SysVar(
						rs.getInt("id_sys_vars"),
						rs.getString("name"),
						rs.getString("value")
					));
		}
		return returnArray;
	}
	
	//********************************** selectSysVarObj **********************************
	
	public ArrayList<SysVar> selectSysVarObj(String where) throws SQLException{
		ArrayList<SysVar> returnArray = new ArrayList<SysVar>();
		String sql = "select * from sys_vars where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			returnArray.add(new SysVar(
						rs.getInt("id_sys_var"),
						rs.getString("name"),
						rs.getString("value")
					));
		}
		return returnArray;
	}
	
	//********************************** inertSysVarObj **********************************
	
	public void insertSysVarObj(SysVar newEntry) throws SQLException{
		String sql = "insert into sys_vars (name,value) values(?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, newEntry.getName());
		pstmt.setString(2, newEntry.getValue());
		pstmt.executeUpdate();
	}
	
	public void updateSysVar(String name, String value) throws SQLException{
		String sql = "update sys_vars set value = '" + value + "' where name = '" + name + "' ";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
	}
	
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
	
	//========================================================================
	//						Connection Status
	//========================================================================
		
	public ArrayList<ConnectionStatus> selectAllConnectionStatus() throws SQLException{
		ArrayList<ConnectionStatus> returnArray = new ArrayList<ConnectionStatus>();
		String sql = "select * from connection_status ";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			returnArray.add(new ConnectionStatus(
					rs.getString("address"), 
					rs.getInt("port"), 
					rs.getInt("satus"), 
					rs.getTimestamp("date") ));
		}
		return returnArray;
	}
	
	public ArrayList<ConnectionStatus> selectConnectionStatus(String where) throws SQLException{
		ArrayList<ConnectionStatus> returnArray = new ArrayList<ConnectionStatus>();
		String sql = "select * from connection_status where " + where;
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()){
			returnArray.add(new ConnectionStatus(
					rs.getString("address"), 
					rs.getInt("port"), 
					rs.getInt("status"), 
					rs.getTimestamp("date") ));
		}
		return returnArray;
	}
	
	public void insertConnectionStatus(ConnectionStatus newEntry) throws SQLException{
		String sql = "insert into connection_status (address,port,status,date) values(?,?,?,?)";
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, newEntry.getAddress());
		pstmt.setInt(2, newEntry.getPort());
		pstmt.setInt(3, newEntry.getStatus());
		pstmt.setTimestamp(4, newEntry.getDate());
		pstmt.executeUpdate();
	}
	
	public void deleteConnectionStatus(String where) throws SQLException{
		String sql = "delete from connection_status where " + where;
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
	}
	
}
