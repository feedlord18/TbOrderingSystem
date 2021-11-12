package gui;

public enum Status {
	SHIPPED("已发货"), 
	DELIVERED("已签收");
	
	private final String value;
	
	Status(String val) {
		this.value = val;
	}
	
	public String toString() {
		return this.value;
	}
	
	public static String[] getStatusBeans() {
		return new String[] { SHIPPED.value, DELIVERED.value };
	}
}