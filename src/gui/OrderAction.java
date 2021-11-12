package gui;

public enum OrderAction {
	UPDATE("更新"), 
	DELETE("删除"),
	ADD("添加");
	
	private final String value;
	
	OrderAction(String val) {
		this.value = val;
	}
	
	public String toString() {
		return this.value;
	}
}