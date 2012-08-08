package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import database.Bitacora;
import database.Conector;
import database.DBInterface;

public class Test {
	
	private static final String POSTGRES_PROPERTIES_PATH = "src/postgres.properties";
	
	public static void main(String[] args) {
		System.out.println("mercurial test");
		System.out.println("push test");
		System.out.println("fer");
		System.out.println("Mateo");
		
		DBInterface db = null;
		try{
			Connection conPostgres = Conector.conectar(POSTGRES_PROPERTIES_PATH);
			db = new DBInterface(conPostgres);
		
			ResultSet rs = db.selectAllBitacora();
			while (rs.next()) {
				System.out.println("=== === ===");
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
}
