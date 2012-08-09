package database;

import java.sql.Timestamp;

public class ConnectionStatus {
	
	private int id;
	private String address;
	private int port;
	private String status;
	private Timestamp date;
	
	public ConnectionStatus(String address, int port, String status,
			Timestamp date) {
		super();
		this.address = address;
		this.port = port;
		this.status = status;
		this.date = date;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Timestamp getDate() {
		return date;
	}
	public void setDate(Timestamp date) {
		this.date = date;
	}
	
	
	
	
}
