package database;
/**
 * Objeto Bitacora
 * 
 * Matchea exactamente la estructura de la base de datos
 *
 */
public class Bitacora {
	private int ID;
	private String alias;
	private String direccionIP;
	private String puerto;
	private String email;
	private String estado;
	
	public Bitacora(int iD, String alias, String direccionIP, String puerto,
			String email, String estado) {
		super();
		ID = iD;
		this.alias = alias;
		this.direccionIP = direccionIP;
		this.puerto = puerto;
		this.email = email;
		this.estado = estado;
	}
	
	public Bitacora(String alias, String direccionIP, String puerto,
			String email, String estado) {
		super();
		this.alias = alias;
		this.direccionIP = direccionIP;
		this.puerto = puerto;
		this.email = email;
		this.estado = estado;
	}

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getDireccionIP() {
		return direccionIP;
	}
	public void setDireccionIP(String direccionIP) {
		this.direccionIP = direccionIP;
	}
	public String getPuerto() {
		return puerto;
	}
	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getEstado() {
		return estado;
	}
	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public String toString() {
		return "Bitacora [ID=" + ID + ", alias=" + alias + ", direccionIP="
				+ direccionIP + ", puerto=" + puerto + ", email=" + email
				+ ", estado=" + estado + "]";
	}
	
	
		
}
