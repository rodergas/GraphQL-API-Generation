import java.util.ArrayList;

public class Modifier{
	
	private String name;
	private ArrayList<Modifier> combinedWith;
	
	public Modifier(String name, ArrayList<Modifier> combinedWith){
		this.name = name;
		this.combinedWith = combinedWith;
		
	}
	
	public void addCombined(Modifier mod){
		getCombinedWith().add(mod);
	}
	
	public ArrayList<Modifier> getCombinedWith(){
		return combinedWith;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCombinedWith(ArrayList<Modifier> combinedWith) {
		this.combinedWith = combinedWith;
	}
}