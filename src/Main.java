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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import virtuoso.jena.driver.*;

public class Main {
	
	
	static void getObjects(ArrayList<Object> objects, ArrayList<Field> fields, HashSet<String> interfaces,  VirtGraph graph){
		

		Query sparql = QueryFactory.create("SELECT ?sujeto (group_concat(?subClass ; separator= \" \") as ?subClasses) FROM <http://localhost:8890/ExampleTFG>  WHERE { " 
				+ "?sujeto <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.essi.upc.edu/~jvarga/gql/Object> ."
				+ "OPTIONAL {?sujeto <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?subClass .}"
				+ "}"
				+ "group by ?sujeto");


	    VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
	    ResultSet res = vqe.execSelect();

	    while(res.hasNext()){
	    	 ArrayList<Field> fieldsOfObject = new ArrayList<>();
	    	 QuerySolution qs = res.next();
	    	 for(Field f : fields){
	    		 if(f.getDomain().equals(qs.get("?sujeto").toString())) fieldsOfObject.add(f);
	    	 }
	    	 
	    	 ArrayList<String> subClasses = null;
	    	 
	    	 if(qs.get("?subClasses").toString().length() == 0)subClasses = new ArrayList<>();
	    	 else{
	    		 subClasses = new ArrayList<String>(Arrays.asList(qs.get("?subClasses").toString().split(" ")));
	    		 interfaces.addAll(subClasses);
	    	 }
	    	 objects.add(new Object(qs.get("?sujeto").toString() , subClasses, fieldsOfObject ));
	    }
	}
	
	
	static ArrayList<Modifier> sortModifiers(String startNode, ArrayList<String> otherNodes, VirtGraph graph){
		ArrayList<Modifier> orderedModifiers = new ArrayList<>();
		
		for(int i = 0; i < otherNodes.size(); ++i){
			Query sparql = QueryFactory.create("SELECT ?rightNode ?rightNodeType  FROM <http://localhost:8890/ExampleTFG> WHERE { "
					+ "<" + startNode + "> <http://www.essi.upc.edu/~jvarga/gql/combinedWith> ?rightNode."
					+ "?rightNode <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?rightNodeType."
					+ "}");
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
		    ResultSet res = vqe.execSelect();


		    while(res.hasNext()){
		    	 QuerySolution qs = res.next();
		    	 if(qs.get("?rightNode") != null){
		    		 if(qs.get("?rightNodeType").toString().equals("http://www.essi.upc.edu/~jvarga/gql/List")) orderedModifiers.add(new List(qs.get("?rightNode").toString(), new ArrayList<Modifier>()));
		    		 else if(qs.get("?rightNodeType").toString().equals("http://www.essi.upc.edu/~jvarga/gql/NonNull")) orderedModifiers.add(new NonNull(qs.get("?rightNode").toString(), new ArrayList<Modifier>()));
		    		 startNode = qs.get("?rightNode").toString();
		    	 }
		    }
		}
		
		return orderedModifiers;
	}
	
	static ArrayList<String> getCombinedModifiers(String subject , VirtGraph graph){
		ArrayList<String> combinedModifiers = new ArrayList<>();
		Query sparql = QueryFactory.create("SELECT  (group_concat(?combinedWith; separator= \" \") as ?combinedModifiers) FROM <http://localhost:8890/ExampleTFG> WHERE { "
				+ "<" + subject + "> <http://www.essi.upc.edu/~jvarga/gql/combinedWith>+ ?combinedWith."
				+ "}"
				);

	    VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
	    ResultSet res = vqe.execSelect();
	    while(res.hasNext()){
	    	 QuerySolution qs = res.next();
	    	 if(qs.get("?combinedModifiers").toString().length() == 0)combinedModifiers = new ArrayList<>();
	    	 else combinedModifiers = new ArrayList<String>(Arrays.asList(qs.get("?combinedModifiers").toString().split(" ")));
	    }

		return combinedModifiers;
	}
	
	static void getFields(ArrayList<Field> createdField, VirtGraph graph){
		Query sparql = QueryFactory.create("SELECT ?sujetoScalarField ?sujetoObjectField ?domain ?range ?modifierType ?modifier (group_concat(?combinedWith; separator= \" \") as ?combinedModifiers) FROM <http://localhost:8890/ExampleTFG> WHERE { "
				+ "	{"
				+ "	OPTIONAL {"
				+ "		?sujetoScalarField <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> ."
				+ "		?sujetoScalarField <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.essi.upc.edu/~jvarga/gql/ScalarField> ."
				+ "		?sujetoScalarField <http://www.w3.org/2000/01/rdf-schema#domain> ?domain ."
				+ "		?sujetoScalarField <http://www.w3.org/2000/01/rdf-schema#range> ?range ."
				+ "			OPTIONAL{"
				+ "				?sujetoScalarField <http://www.essi.upc.edu/~jvarga/gql/hasModifier> ?modifier."
				+ "				?modifier <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?modifierType."
				+ "			}"
				+ "		}"
				+ "	}"
				+ "UNION"
				+ "	{"
				+ "	OPTIONAL {"
				+ "		?sujetoObjectField <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/1999/02/22-rdf-syntax-ns#Property> ."
				+ "		?sujetoObjectField <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.essi.upc.edu/~jvarga/gql/ObjectField> ."
				+ "		?sujetoObjectField <http://www.w3.org/2000/01/rdf-schema#domain> ?domain ."
				+ "		?sujetoObjectField <http://www.w3.org/2000/01/rdf-schema#range> ?range ."
				+ "			OPTIONAL{"
				+ "				?sujetoObjectField <http://www.essi.upc.edu/~jvarga/gql/hasModifier> ?modifier."
				+ "				?modifier <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?modifierType."
				+ "			}"
				+ "		}"
				+ "	}"
				+ "}"
				+ "group by ?sujetoScalarField ?sujetoObjectField ?domain ?range ?modifierType ?modifier"
				);


	    VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (sparql, graph);
	    ResultSet res = vqe.execSelect();
	    





	    while(res.hasNext()){
	    	 QuerySolution qs = res.next();

	    	 String modifierType = null;
	    	 if(qs.get("?modifierType") != null) modifierType = qs.get("?modifierType").toString();

	    	 if(qs.get("?sujetoObjectField") != null){
	    		 if(modifierType != null){
	    			 ArrayList<Modifier> combinedModifiersOrdered = new ArrayList<>();
	    			 ArrayList<String> combinedModifiers = getCombinedModifiers(qs.get("?modifier").toString() , graph);
	    		 	 if(combinedModifiers.size() != 0) combinedModifiersOrdered = sortModifiers(qs.get("?modifier").toString(), combinedModifiers, graph);

	    		 	 //##ex:stopName  gql:hasModifier ex:nn1
	    		 	 //ex:nn1 a gql:NonNull . ---> modifier a modifierType
	    		 	 //ex:nn1 gql:combinedWith ex:l1 . --> combinedModifiersOrdered
	    			 if(modifierType.equals("http://www.essi.upc.edu/~jvarga/gql/List"))createdField.add(new ObjectField(qs.get("?sujetoObjectField").toString(),qs.get("?domain").toString(), qs.get("?range").toString(), new List(qs.get("?modifier").toString(), combinedModifiersOrdered)));
	    			 else if(modifierType.equals("http://www.essi.upc.edu/~jvarga/gql/NonNull"))createdField.add(new ObjectField(qs.get("?sujetoObjectField").toString(),qs.get("?domain").toString(), qs.get("?range").toString(), new NonNull(qs.get("?modifier").toString(), combinedModifiersOrdered)));

	    		 }else{
	    			 createdField.add(new ObjectField(qs.get("?sujetoObjectField").toString(),qs.get("?domain").toString(), qs.get("?range").toString(), null ));
	    		 }
	    	 }
	    	 else if(qs.get("?sujetoScalarField") != null){
	    		 if(modifierType != null){
	    			 ArrayList<Modifier> combinedModifiersOrdered = new ArrayList<>();
	    			 
	    			 ArrayList<String> combinedModifiers = getCombinedModifiers(qs.get("?modifier").toString() , graph);
	    			 if(combinedModifiers.size() != 0)combinedModifiersOrdered = sortModifiers(qs.get("?modifier").toString(), combinedModifiers, graph);
	    	
	    		 	 
	    			 if(modifierType.equals("http://www.essi.upc.edu/~jvarga/gql/List"))createdField.add(new ScalarField(qs.get("?sujetoScalarField").toString(),qs.get("?domain").toString(), qs.get("?range").toString(), new List(qs.get("?modifier").toString(), combinedModifiersOrdered)));
	    			 else if(modifierType.equals("http://www.essi.upc.edu/~jvarga/gql/NonNull"))createdField.add(new ScalarField(qs.get("?sujetoScalarField").toString(),qs.get("?domain").toString(), qs.get("?range").toString(), new NonNull(qs.get("?modifier").toString(), combinedModifiersOrdered)));

	    		 }else{
	    			 createdField.add(new ScalarField(qs.get("?sujetoScalarField").toString(),qs.get("?domain").toString(), qs.get("?range").toString(), null ));
	    		 }
	    	 }
	    }
	}
	
	static String constructRange(String range, Modifier mod){
		String combination = range;
		int contadorClaudators = 0;
		if(mod != null){
			if(mod.getClass().equals(List.class)){ combination = combination + "]"; ++contadorClaudators;}
			else if(mod.getClass().equals(NonNull.class)) combination = combination + "!";
			if(mod.getCombinedWith().size() > 0){		
				for(Modifier combined : mod.getCombinedWith()){		
					if(combined.getClass().equals(List.class)){ combination = combination + "]"; ++contadorClaudators;}
					else if(combined.getClass().equals(NonNull.class)) combination = combination + "!";
				}
			}
			while(contadorClaudators > 0){
				combination =  "[" + combination;
				--contadorClaudators;
			}
		}
		return combination;
	}
	
	static void writeFields(Object o , FileWriter fw) throws IOException{
		for(Field f : o.getFields()){
    		Integer index = f.getName().lastIndexOf("/");
			String shortName = f.getName().substring(index + 1);
			
			index = f.getRange().lastIndexOf("/");
			String shortRange = f.getRange().substring(index + 1);
			//construct Range [String!]
			String range = constructRange(shortRange, f.getModifier());
			fw.write("\t" + shortName + " : " + range + "\r\n");
		}
		
	}
	
	/**
	 * Executes a SPARQL query against a virtuoso url and prints results.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		VirtGraph graph = new VirtGraph ("TFG_Example1", "jdbc:virtuoso://localhost:1111", "dba", "dba");

		graph.clear ();

		ArrayList<Object> createdObjects = new ArrayList<>();
		HashSet<String> interfaces = new HashSet<>();
		ArrayList<Field> createdField= new ArrayList<>();
		
		//get Objects
		
		
		getFields(createdField, graph);
		getObjects(createdObjects, createdField, interfaces, graph);
		/*
		System.out.println("####OBJECTS#####");
		for(Object o : createdObjects){
			System.out.println("Name:  " + o.getName());
			for(String sub : o.getSubClassOf()){
				System.out.println("SubclassOf " + sub);
			}
			for(Field f : o.getFields()){
				
				System.out.println(f.getName());
				if(f.getModifier()!=null) System.out.println("Modifier " + f.getModifier().getName());
			}
			System.out.println("----");
		}
		
		for(String inte : interfaces){
			System.out.println("inte " + inte);
		}
		*/
		graph.close();

		
		

		//File newTextFile = new File("C:/Users/rober_000/Documents/TFG/Ejemplos_Ontologias/Primer_ejemplo-17_7_2017_13_57/test.js");
		File newTextFile = new File("C:/Users/rober_000/Documents/TFG/Ejemplos_Ontologias/Primer_ejemplo_extendido-18_09_2017_17_31/test.js");

        FileWriter fw = new FileWriter(newTextFile);
        
        for(Object o : createdObjects){
        	boolean interfaz = false;
        	if(interfaces.contains(o.getName())){ interfaz = true; o.setInterface(true);}
    		Integer index = o.getName().lastIndexOf("/");
			String shortName = o.getName().substring(index + 1);
        	if(interfaz){
    			fw.write("interface " + shortName + " {" + "\r\n"); //First line
    			fw.write("	" + shortName + "Type: String!" + "\r\n"); //type 
        	}else{
        		fw.write("type " + shortName + " ");
        		if(o.getSubClassOf().size() == 0)  fw.write("{" + "\r\n");
        		else{
	        			int i = 0;
	        			for(String subClassOf : o.getSubClassOf()){
	        				index = subClassOf.lastIndexOf("/");
	        				String shortNameSubClass = subClassOf.substring(index + 1);
	        				if(i == 0) fw.write("implements " + shortNameSubClass);
	        				else fw.write(", " + shortNameSubClass);
	        				++i;
	        			}
	        			fw.write("{" + "\r\n");
        			
    					//write Field of interface in the type
        				for(String subClassOf : o.getSubClassOf()){
        					for(Object searchParent : createdObjects){
        						if(searchParent.getName().equals(subClassOf)) writeFields(searchParent, fw);
        				}
        				index = subClassOf.lastIndexOf("/");
        				String shortNameSubClass = subClassOf.substring(index + 1);
        				fw.write("	" + shortNameSubClass + "Type: String!" + "\r\n"); //type 
        			}
        		}

        	}
        	writeFields(o, fw);
        	fw.write("}" + "\r\n" + "\r\n"); //End type/ interface
        }
        


        
        //Queries
        fw.write("type Query {" + "\r\n");
        for(int i = 0; i < createdObjects.size(); ++i){
        	if(!createdObjects.get(i).isInterface()){
        		Integer index = createdObjects.get(i).getName().lastIndexOf("/");
				String shortNameSubClass = createdObjects.get(i).getName().substring(index + 1);
        		fw.write("	" + "all"+ shortNameSubClass + "s: [" + shortNameSubClass +"]" + "\r\n");
        		fw.write("	" + "get"+ shortNameSubClass + "(id: String!): " + shortNameSubClass +"" + "\r\n");
        		
        	}
        }
        fw.write("}" + "\r\n");
        
        fw.write("schema {" + "\r\n");
        fw.write("	query: Query" + "\r\n");
        fw.write("}" + "\r\n" + "\r\n");
        fw.close();

	}

	


}
