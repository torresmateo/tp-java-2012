package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class PruebasBD {

	public PruebasBD() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Connection conPostgres = null;
		try {

			Drivers.cargarDrivers();
			// Connection conOracle =
			// Conexiones.obtenerConexion(Conexiones.DBMS_TYPE_ORACLE);
			conPostgres = Conexiones
					.obtenerConexion(Conexiones.DBMS_TYPE_POSTGRES);
			pruebaSelect(conPostgres);
//			pruebaUpdate(conPostgres);
			//pruebaDelete(conPostgres);

			// pruebaInsert(conPostgres);
			//pruebaSelect(conPostgres);

		} catch (ClassNotFoundException e) {
			System.out.println("No se encontro el driver");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("No se pudo conectar" + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void pruebaUpdate(Connection conPostgres)
			throws SQLException {
		Statement stmt = conPostgres.createStatement();

		// Step 3 & 4: execute a SQL UPDATE statement via executeUpdate()
		// which returns an int indicating the number of row affected.
		String sqlStr = "update alumnos_curso_java set direccion = 'LAB2', telefono = '334650' where cedula = 234";
		System.out.println("El SQL es: " + sqlStr); // For debugging
		int count = stmt.executeUpdate(sqlStr);
		System.out.println(count + " registros modificados");
	}

	private static void pruebaSelect(Connection conPostgres)
			throws SQLException {
		System.out.println("select");
		Statement sentencia = conPostgres.createStatement();
		ResultSet rs = sentencia
				.executeQuery("select * from bitacora_servicios");

		while (rs.next()) {
			System.out.println("Cedula " + rs.getInt("cedula"));
			System.out.println("nombre " + rs.getString("nombre"));

			System.out.println("direccion " + rs.getString("direccion"));
			System.out.println("telefono " + rs.getString("telefono"));
			System.out.println("--- --- ---");
		}
	}

	private static void pruebaInsert(Connection conPostgres)
			throws SQLException {
		String sqlInsert = "insert into alumnos_curso_java (cedula,nombre,direccion,email) values(?,?,?,?)";
		PreparedStatement pstmt = conPostgres.prepareStatement(sqlInsert);
		pstmt.setInt(1, 99999);
		pstmt.setString(2, "ALE F");
		pstmt.setNull(3, Types.VARCHAR);
		pstmt.setString(4, "algo@gmail.com");
		pstmt.executeUpdate();
	}

	private static void pruebaDelete(Connection conPostgres)
			throws SQLException {
		Statement stmt = conPostgres.createStatement();

		// Step 3 & 4: execute a SQL UPDATE statement via executeUpdate()
		// which returns an int indicating the number of row affected.
		String sqlDelete = "delete from alumnos_curso_java where cedula = 964343";
		System.out.println("El SQL es: " + sqlDelete); // For debugging
		int count = stmt.executeUpdate(sqlDelete);
		System.out.println(count + " registros modificados");
	}

}
