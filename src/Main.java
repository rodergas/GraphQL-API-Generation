/*
 *  $Id: VirtuosoSPARQLExample2.java,v 1.3 2008/04/10 07:26:30 source Exp $
 *
 *  This file is part of the OpenLink Software Virtuoso Open-Source (VOS)
 *  project.
 *
 *  Copyright (C) 1998-2008 OpenLink Software
 *
 *  This project is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU General Public License as published by the
 *  Free Software Foundation; only version 2 of the License, dated June 1991.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 */

//package virtuoso.jena.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import virtuoso.jena.driver.*;

public class Main {
	
	static String Ex = "http://www.example.com/";
	static String Gql = "http://www.essi.upc.edu/~jvarga/gql/";
	static String Rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	static String Rdfs = "http://www.w3.org/2000/01/rdf-schema#";
	
	
	static void addScalar(ArrayList<ScalarField> createdScalarField, Scalar scalar, String s,  StmtIterator it){
		for(int i = 0; i < createdScalarField.size(); ++i){
			 if(createdScalarField.get(i).getName().equals(s.toString())){
				 createdScalarField.get(i).setRange(scalar.toString());
				 tripleTratado( it);
				 }
		}
	}
	
	static void addObjectFieldRange(ArrayList<ObjectField> createdObjectField, String range, String s,  StmtIterator it){
		for(int i = 0; i < createdObjectField.size(); ++i){
			 if(createdObjectField.get(i).getName().equals(s.toString())){
				 createdObjectField.get(i).setRange(range);
				 tripleTratado( it);
				 }
		}
	}
	
	static void ScalarOrObjectField(String s, ArrayList<ObjectField> createdObjectField, ArrayList<ScalarField> createdScalarField, String o,  StmtIterator it){
		boolean objectField = false;
		//boolean scalarField = false;
		
		for (int i = 0; i < createdObjectField.size(); ++i){
			if(createdObjectField.get(i).getName().equals(s)){
				objectField = true;
				createdObjectField.get(i).setDomain(o.toString());
				tripleTratado( it);
				
			}
		}
		
		if (objectField == false){
			for (int i = 0; i < createdScalarField.size(); ++i){
				if(createdScalarField.get(i).getName().equals(s)){
					//scalarField = true;
					createdScalarField.get(i).setDomain(o.toString());
					tripleTratado( it);
				}
			}
		}
		
	}
	
	static void addModifierToField(String s, ArrayList<Field> createdField, String o, StmtIterator it){
		for (int i = 0; i < createdField.size(); ++i){
			if(createdField.get(i).getName().equals(s)){
				if(o.contains("NonNull")){
					createdField.get(i).addModifier(Modifier.NonNull);
				}
				else if(o.contains("List")){
					createdField.get(i).addModifier(Modifier.List);
				}
				tripleTratado( it);
			}
		}
	}
	
	static void addObjectToField(String s, ArrayList<Field> createdField, String o, StmtIterator it){
		for (int i = 0; i < createdField.size(); ++i){
			if(createdField.get(i).getName().equals(o)){
				createdField.get(i).setDomain(s);
				tripleTratado( it);
			}
		}
	}
	
	static void addFieldPropertyToField(String s, ArrayList<Field> createdField,  ArrayList<ObjectField> createdObjectField , ArrayList<ScalarField> createdScalarField , String p, StmtIterator it){
		for(int i = 0; i < createdField.size(); ++i){
			//get Field
			if(createdField.get(i).getName().equals(s)){
				boolean find = false;
				//Search if predicate is related to an object Field
				for(int j = 0; j < createdObjectField.size(); ++j){
					if(createdObjectField.get(j).getName().equals(p)){
						find = true;
						createdField.get(i).setProperty(createdObjectField.get(j));
					}
				}
				//Search if is predicate related to an scalar Field
				if(find == false){
					for(int j = 0; j < createdScalarField.size(); ++j){
						if(createdScalarField.get(j).getName().equals(p)){
							find = true;
							createdField.get(i).setProperty(createdScalarField.get(j));
						}
					}
				}
				if(find) tripleTratado( it);
			}
		}
		
	}
	
	static void tripleTratado(StmtIterator it){
		 it.remove();
	}
	
	static void writeFields(Object obj, FileWriter fw) throws IOException{
		for(int i = 0; i < obj.getFields().size(); ++i){
			Field f = obj.getFields().get(i);
			Integer index = f.getProperty().getName().lastIndexOf("/");
			String shortName = f.getProperty().getName().substring(index + 1);
			
			index = f.getProperty().getRange().lastIndexOf("/");
			String shortNameRange = f.getProperty().getRange().substring(index + 1);
			
			ArrayList<Modifier> mod = f.getModifier();
			
			if(mod.size() == 0){
				fw.write("	" + shortName + ": " + shortNameRange +  " \r\n");
			}else{
				boolean lista = false;
				boolean noNull = false;
				for(int j = 0; j < mod.size(); ++j){
					if(mod.get(j).equals(Modifier.List)) lista = true;
					else if(mod.get(j).equals(Modifier.NonNull)) noNull = true;
				}
				
				if(lista && noNull) fw.write("	" + shortName + ": [" + shortNameRange +  "]! \r\n");
				else if(lista) fw.write("	" + shortName + ": [" + shortNameRange +  "] \r\n");
				else if (noNull)fw.write("	" + shortName + ": " + shortNameRange +  "! \r\n");
				
			}
		}
	}
	
	
	/**
	 * Executes a SPARQL query against a virtuoso url and prints results.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

/*			STEP 1			*/
		VirtGraph graph = new VirtGraph ("TFG_Example1", "jdbc:virtuoso://localhost:1111", "dba", "dba");

/*			STEP 2			*/
/*		Load data to Virtuoso		*/
		graph.clear ();

		//graph.read("file://20170714_gql_example.ttl", "TTL");

/*			STEP 3			*/
/*		Select only from VirtGraph	*/
		//Query sparql = QueryFactory.create("SELECT * WHERE { ?s ?p ?o } ");
		
		Query sparql = QueryFactory.create("SELECT * FROM <http://localhost:8890/Example4> WHERE { ?s ?p ?o }  ");


/*			STEP 4			*/
		VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
		//ResultSet results = vqe.execSelect();
		Model model = vqe.execConstruct();
		//ResultSetFormatter.out(System.out, results, sparql);
		
		
		
		ArrayList<Object> createdObjects = new ArrayList<>();
		ArrayList<ScalarField> createdScalarField= new ArrayList<>();
		ArrayList<ObjectField> createdObjectField= new ArrayList<>();
		ArrayList<Field> createdField= new ArrayList<>();
		HashSet<String> subClassOf = new HashSet<>();
		

		
		while(!model.isEmpty()){

			StmtIterator it =  model.listStatements();
			
			while (it.hasNext()) {
			     Statement stmt = it.next();
			     
			     RDFNode s = stmt.getSubject();
			     RDFNode p = stmt.getPredicate();
			     RDFNode o = stmt.getObject();
			     
			     System.out.println(s.toString() + " " + p.toString() + " " + o.toString());
			     
			     //Creating instance of the objects
			     if(p.toString().equals(Rdf + "type")){
			    	 
			    	 
			    	 
			    	 if(o.toString().equals(Gql + "ObjectField")){
			    		 createdObjectField.add(new ObjectField(s.toString()));
			    	 }else if(o.toString().equals(Gql +"ScalarField")){
			    		 createdScalarField.add(new ScalarField(s.toString(), null));
			    	 }else if(o.toString().equals(Gql +"Object")){
			    		 createdObjects.add(new Object(s.toString()));
			    	 }else if(o.toString().equals(Gql + "Field")){
			    		 createdField.add(new Field(s.toString()));
			    	 }	 
			    	 else{
			    		 //Classes instances
			    	 }
			    	 tripleTratado( it);
			     }
			     //Subclasses of the objects
			     else if(p.toString().equals(Rdfs + "subClassOf")){
			    	 for(int i = 0; i < createdObjects.size(); ++i){
			    		 if(createdObjects.get(i).getName().equals(s.toString())){
			    			 createdObjects.get(i).addSubClassOf(o.toString());
			    			 subClassOf.add(o.toString());
			    			 tripleTratado( it);
			    		 }
			    	 }
			     }
			     
			     
			     //Type of the scalar field or what is the reange of an object field
			     else if(p.toString().equals(Rdfs + "range")){
			    	 //ScalarFields
			    	 if(o.toString().equals(Gql + Scalar.Float.toString())){
			    		 addScalar(createdScalarField,Scalar.Float, s.toString(), it); 
			    	 }else if(o.toString().equals(Gql + Scalar.ID.toString())){
			    		 addScalar(createdScalarField,Scalar.ID, s.toString(),  it); 
			    	 }else if(o.toString().equals(Gql + Scalar.Boolean.toString())){
			    		 addScalar(createdScalarField,Scalar.Boolean, s.toString(),  it); 
			    	 }else if(o.toString().equals(Gql + Scalar.Int.toString())){
			    		 addScalar(createdScalarField,Scalar.Int, s.toString(),  it); 
			    	 }else if(o.toString().equals(Gql + Scalar.String.toString())){
			    		 addScalar(createdScalarField,Scalar.String, s.toString(),  it); 
			    	 }else{
			    	 
			    		 //ObjectFields
			    		 addObjectFieldRange(createdObjectField, o.toString(), s.toString(), it);
			    		 
			    	 }
			     }
			     
			     //The domain could be an object related to another object (ObjectField) ex:nearByInfrastructure rdfs:domain ex:Infrastructure ;
			     //Or could be an scalar related to an object ex:stopName rdfs:domain ex:MetroAndBusStop ;
			     
			     else if(p.toString().equals(Rdfs + "domain")){
			    	 ScalarOrObjectField(s.toString(), createdObjectField, createdScalarField, o.toString(), it);
			     }
			     
			     //Put modifier of the field
			     else if(p.toString().equals(Gql + "hasModifier")){
			    	 addModifierToField(s.toString(), createdField, o.toString(), it);
			     }
			     
			     //Put domain of the Field
			     else if(p.toString().equals(Gql +"hasField")){
			    	 addObjectToField(s.toString(), createdField, o.toString(), it);
			     }
			     

			     
			     //Scalar Field or Object Field (FieldProperty) related with a blank node (Field)
			     else if(s.toString().contains("#b")){
			    	 System.out.println("ENTRO " + s.toString() + " " + p.toString() + " " + o.toString());
			    	 addFieldPropertyToField(s.toString(), createdField, createdObjectField, createdScalarField, p.toString(), it);
			    	 
			     }		
			     //atributos de instancias "Localitzacio ex:longitude 2 ;"
			     //else if(!o.toString().contains("http://www.example.com/") && !o.toString().contains("http://www.essi.upc.edu/~jvarga/gql/")){
			     else{
			    	 tripleTratado( it);
			     }
			}

	    }
		
		
		System.out.println("####OBJECTS#####");
		for(int i = 0; i < createdObjects.size(); ++i){
			System.out.println("Name: " + createdObjects.get(i).getName());
			
			if(createdObjects.get(i).getSubClassOf().size() > 0){
				for (int j = 0; j < createdObjects.get(i).getSubClassOf().size(); ++j){
					System.out.println("Subclass Of: " + createdObjects.get(i).getSubClassOf().get(j));
				}
				
			}
			System.out.println("--------------------");
		}
		System.out.println("#####SCALAR FIELDS#####");
		for(int i = 0; i < createdScalarField.size(); ++i){
			System.out.println("Name: " + createdScalarField.get(i).getName());
			System.out.println("Domain: " + createdScalarField.get(i).getDomain());
			System.out.println("Scalar: " + createdScalarField.get(i).getRange());
			System.out.println("--------------------");
		}
		System.out.println("#####OBJECT FIELDS#####");
		for(int i = 0; i < createdObjectField.size(); ++i){
			System.out.println("Name: " + createdObjectField.get(i).getName());
			System.out.println("Domain: " + createdObjectField.get(i).getDomain());
			System.out.println("Range: " + createdObjectField.get(i).getRange());
			System.out.println("--------------------");
		}
		
		System.out.println("#####FIELDS#####");
		for(int i = 0; i < createdField.size(); ++i){
			System.out.println("Name: " + createdField.get(i).getName());
			ArrayList<Modifier> mod = createdField.get(i).getModifier();
			for (int j = 0; j < mod.size(); ++j){
				System.out.println("Modifier: " + mod.get(j).toString());
			} 
			System.out.println("Domain: " + createdField.get(i).getDomain());
			System.out.println("Field Property Name: " + createdField.get(i).getProperty().getName());
			System.out.println("--------------------");
		}
		
		
		//File newTextFile = new File("C:/Users/rober_000/Documents/TFG/Ejemplos_Ontologias/Primer_ejemplo-17_7_2017_13_57/test.js");
		File newTextFile = new File("C:/Users/rober_000/Documents/TFG/Ejemplos_Ontologias/Primer_ejemplo_extendido-18_09_2017_17_31/test.js");

        FileWriter fw = new FileWriter(newTextFile);
        
        //Link fields (includes scalarfields, objectfields + modifier) with the created objects
        
        for(int i = 0; i < createdObjects.size(); ++i){
        	String name = createdObjects.get(i).getName();
        	for(int j = 0; j < createdField.size(); ++j){
        		if(createdField.get(j).getDomain().equals(name)){
        			createdObjects.get(i).addField(createdField.get(j));
        		}
        	}
        }
        
        //See which Object are parents of some classes (interfaces)
        
        for(int j = 0; j < createdObjects.size(); ++j){
        	Object obj = createdObjects.get(j);
        	boolean interfaz = false;
    		//interface
        	Iterator<String> it = subClassOf.iterator();
        	while(it.hasNext()){
        		String name = it.next();
        		if(obj.getName().equals(name)){
        			interfaz = true;
        			Integer index = name.lastIndexOf("/");
        			String shortName = name.substring(index + 1);
        			fw.write("interface " + shortName + " {" + " \r\n"); //First line
        			writeFields(obj,fw);
        			fw.write("	" + shortName + "Type: String!" + " \r\n"); //type 
        			fw.write("} " + " \r\n" + " \r\n");

        		}
        	}
        	
        	//No interface
        	if(!interfaz){
        		Integer index = obj.getName().lastIndexOf("/");
        		String shortName = obj.getName().substring(index + 1);
        		
        		if(obj.getSubClassOf().size() == 0)fw.write("type "  + shortName + " {" + " \r\n");
        		else{
        			String nameSubclass = "";
        			String shortNameSubClass = "";
        			fw.write("type "  + shortName + " implements ");
        			for(int i = 0; i < obj.getSubClassOf().size(); ++i){
        				index = obj.getSubClassOf().get(i).lastIndexOf("/");
        				nameSubclass = obj.getSubClassOf().get(i);
        				shortNameSubClass = obj.getSubClassOf().get(i).substring(index + 1);
        				if(i == 0) fw.write(shortNameSubClass);
        				else fw.write(", " + shortNameSubClass);
        				
        			}
        			fw.write("{" + " \r\n");
        			
        			//Fields of subclassOf to this object
        			for(int i = 0; i < createdObjects.size(); ++i){
        				if(createdObjects.get(i).getName().equals(nameSubclass)){
        					writeFields(createdObjects.get(i),fw);
        				}
        			}
        			
        			fw.write("	" + shortNameSubClass + "Type: String!" + " \r\n");
        			
        		}
        		writeFields(obj,fw);
    			fw.write("} " + " \r\n" + " \r\n");
    			
        	}
        	
        }
        


        
        fw.close();




		
		
	}




}
