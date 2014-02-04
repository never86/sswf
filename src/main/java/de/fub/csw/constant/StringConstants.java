/**
 * @className StringContants.java
 * 
 * Copyright (c) 2010-2014 Corporate Semantic Web.
 * Königin-Luise-Straße 24 - 26 14195 Berlin, Free University Berlin, Germany
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fub.csw.constant;

import java.util.List;

/**
 * class description
 * 
 * @version 0.1
 * 
 * @date 2012-1-10
 * 
 * @author Zhili Zhao
 */

public class StringConstants {

	public static String appDir = "";

	public static String ENTITIES_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<!DOCTYPE uridef["
			+ "<!ENTITY rdf     \"http://www.w3.org/1999/02/22-rdf-syntax-ns\">\n"
			+ "<!ENTITY rdfs    \"http://www.w3.org/2000/01/rdf-schema\">\n"
			+ "<!ENTITY xsd     \"http://www.w3.org/2001/XMLSchema\">\n"
			+ "<!ENTITY owl     \"http://www.w3.org/2002/07/owl\">\n"
			+ "]>\n";

	public static String SEMANTIC_DATA_REPOSITORY_URL = "http://grid.lzu.edu.cn:6060/openrdf-sesame/repositories/sswf";

	public static String WF_ONTOLOGY_BASE_URL = "http://www.corporate-semantic-web.de/sswf2013#";

	public static String queryAgentByTask(Object taskName) {
		return "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"
				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
				+ "SELECT ?agent\n" + "WHERE {\n"
				+ "?role :responsible ?task .\n" + "?agent :hasRole ?role .\n"
				+ "?agent :available \"true\"^^xsd:boolean .\n"
				+ "?agent :priority ?priority .\n"
				+ "FILTER regex(str(?task), '" + taskName + "')\n" + "}\n"
				+ "ORDER BY DESC (?priority)\n" + "LIMIT 1";
	}

	public static String updateAgent(Object agent) {
		return "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
				+ "DELETE DATA {:" + agent
				+ " :available  \"true\"^^xsd:boolean};\n" + "INSERT DATA {:"
				+ agent + " :available  \"false\"^^xsd:boolean}";
	}

	public static String goTermAnalysisQuery(String goTerm, Object targetGOTerm) {
		String query = "";
		if(goTerm.equalsIgnoreCase(targetGOTerm.toString()))
			 query = "PREFIX : <http://www.geneontology.org/go#>\n"+
						"ASK {Class(:" +  targetGOTerm +")}";
		else
		 query = "PREFIX : <http://www.geneontology.org/go#>\n"+
			"ASK {SubClassOf(:" +  targetGOTerm + ", :"+ goTerm +")}";
		System.out.println("AAAAAAAAAAAAAA:" + query);
    	return query;
		
	}

}
