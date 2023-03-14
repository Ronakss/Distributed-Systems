public class Item {
	
	private int reserve;
	private String name, description;
	
	public Item(String name, String description, int reserve){
		setName(name);
		setDescription(description);
		setReserve(reserve);
	}
	
	public void setName(String _name){
		name = _name;
	}
	
	public void setDescription(String _description){
		description = _description;
	}
	
	public void setReserve(int _reserve){
		reserve = _reserve;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public int getReserve(){
		return reserve;
	}
	
	public String toString(){
		return String.format("%s %s, %d", name, description, reserve);
	}
}