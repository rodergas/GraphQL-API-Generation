import java.util.ArrayList;

public class Object {
	private String name;
	private ArrayList<String> subClassOf;
	private ArrayList<Field> fields;
	
	
	public Object(String name){
		this.setName(name);
		setSubClassOf(new ArrayList<String>());
		setFields(new ArrayList<Field>());
		
	}
	


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ArrayList<String> getSubClassOf() {
		return subClassOf;
	}


	public void setSubClassOf(ArrayList<String> subClassOf) {
		this.subClassOf = subClassOf;
	}
	
	public void addSubClassOf(String subClassOfString){
		subClassOf.add(subClassOfString);
	}

	
	public void addField(Field field){
		fields.add(field);
	}


	public ArrayList<Field> getFields() {
		return fields;
	}


	public void setFields(ArrayList<Field> fields) {
		this.fields = fields;
	}

}