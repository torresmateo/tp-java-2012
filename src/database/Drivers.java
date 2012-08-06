/*
 * Created on May 18, 2005
 *
 * Pablo Santa Cruz
 */
package database;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * Esta clase inicializa los drivers JDBC que haga falta para conexiones a base
 * de datos
 */
public class Drivers {

	public static void cargarDrivers() throws ClassNotFoundException {

		/*
		 * Cargamos el driver de postgres. Nos aseguramos de la carga, para
		 * hacerlo compatible con los JDK < 6
		 */
		Class.forName("org.postgresql.Driver");
	}

	public static void main(String[] args) {
		try {
			cargarDrivers();

			/* Lista de controladores/drivers jdbc registrados en el sistema */
			Enumeration<Driver> eDrivers = DriverManager.getDrivers();
			while (eDrivers.hasMoreElements()) {
				Driver driver = eDrivers.nextElement();
				System.out.println("DRIVER: " + driver);
			}
		} catch (ClassNotFoundException e) {
			/* No se encontr� alguna clase. */
			e.printStackTrace();
		}

	}
}
