
public class ScalarField extends FieldProperty {

	
	public ScalarField(String name,  Scalar scalar){
		super(name);
		if(scalar == null) setRange("null");
		else setRange(scalar.toString());
	}

	
}
