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

import java.io.File;

import org.mule.api.MuleContext;

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
	public static  String appDir ="";
//	public static final String SWF_KB = "rules"+File.separator+"semantic_swf_engine"+File.separator+"kb.prova";
//	public static final String PROVA_KNOWLEDGE_BASE_AGENT_NAME = "kb-agent";
	
//	public static  String SWF_SERVICE_ONTOLOGY = "http://localhost:8080/service.owl";
//	
//	public static  String SWF_TASK_SERVICE_ONTOLOGY = "http://localhost:8080/Task-Service.owl";
//	
//	public static  String EXCEPTION_HANDLING_AGENT_NAME = "csw2012_ExceptionHandlingAgent";
	
	
	public static  String ENTITIES_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
		+ "<!DOCTYPE uridef["
		+ "<!ENTITY rdf     \"http://www.w3.org/1999/02/22-rdf-syntax-ns\">\n"
		+ "<!ENTITY rdfs    \"http://www.w3.org/2000/01/rdf-schema\">\n"
		+ "<!ENTITY xsd     \"http://www.w3.org/2001/XMLSchema\">\n"
		+ "<!ENTITY owl     \"http://www.w3.org/2002/07/owl\">\n"
		+ "<!ENTITY expr    \"http://www.daml.org/services/owl-s/1.2/generic/Expression.owl\">\n"
		+ "<!ENTITY swrl    \"http://www.w3.org/2003/11/swrl\">\n"
		+ "<!ENTITY swrl-onto \"http://www.daml.org/rules/proposal/swrl.owl\">\n"
		+ "<!ENTITY service \"http://de.dbpedia.org/redirects/ruleml/service.owl\">\n"
		+ "]>\n";
			
	public static  String SWF_ONTOLOGY = "http://localhost:8080/sswf-5.0.owl";
	
	public static String queryAgentByTask(Object taskName) {
		return   "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"+
	        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
			"SELECT ?agent\n"+
			"FROM <"+SWF_ONTOLOGY+">\n"+
		    "WHERE {\n"+
				"?role :responsible ?task .\n"+
				"?agent :hasRole ?role .\n"+
				"?agent :available \"true\"^^xsd:boolean .\n"+
				"?agent :priority ?priority .\n"+
				"FILTER regex(str(?task), '"+taskName+"')\n"+
			"}\n"+
			"ORDER BY DESC (?priority)\n";
	}
	

}
