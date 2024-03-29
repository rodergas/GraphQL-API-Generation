import java.util.ArrayList;

public class Field {

	
	private Modifier hasModifier;
	private String name;
	private String domain;
	private String range;
	
	public Field(String name, String domain, String range, Modifier modifier){
		this.setName(name);
		this.setDomain(domain);
		this.setRange(range);
		this.setModifier(modifier);
	}

	public void setModifier(Modifier mod){
		hasModifier = mod;
	}
	
	public Modifier getModifier() {
		return hasModifier;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getRange() {
		return range;
	}

	public void setRange(String range) {
		this.range = range;
	}

}
