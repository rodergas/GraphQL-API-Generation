import java.util.ArrayList;

public class Modifier{
	
	private String name;
	private ArrayList<Modifier> combinedWith;
	
	public Modifier(String name){
		this.name = name;
		combinedWith = new ArrayList<>();
	}
	
	public void addCombined(Modifier mod){
		combinedWith.add(mod);
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
}