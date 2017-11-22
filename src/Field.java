import java.util.ArrayList;

public class Field {

	
	private Modifier hasModifier;
	private String name;
	private String domain;
	private FieldProperty property;
	
	public Field(String name){
		this.setName(name);
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

	public FieldProperty getProperty() {
		return property;
	}

	public void setProperty(FieldProperty property) {
		this.property = property;
	}
}
