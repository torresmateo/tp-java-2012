package database;

public class SysVar {
	private int ID;
	private String name;
	private String value;
	
	
	
	public SysVar(int iD, String name, String value) {
		super();
		ID = iD;
		this.name = name;
		this.value = value;
	}
	
	
	
	public SysVar(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}



	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}



	@Override
	public String toString() {
		return "SysVar [ID=" + ID + ", name=" + name + ", value=" + value + "]";
	}
	
	
	
}
