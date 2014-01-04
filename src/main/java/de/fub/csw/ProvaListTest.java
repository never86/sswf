/**
 * @className ProvaListTest.java
 * 
 * Copyright (c) 2010-2014 Corporate Semantic Web.
 * Königin-Luise-Straße 24 - 26 14195 Berlin, Free University Berlin, Germany
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fub.csw;

import org.junit.Test;

import ws.prova.api2.ProvaCommunicator;
import ws.prova.api2.ProvaCommunicatorImpl;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;

/**
 * class description 
 * 
 * @version 0.1 
 *  
 * @date 2012-4-10
 * 
 * @author Zhili Zhao
 */

public class ProvaListTest {
	
	public static int calculateSize(ProvaList l){
		System.out.println(l.getFixed().length);
		return l.getFixed().length;
	}
	
	public static ProvaList createProvaList(){
		return ProvaListImpl.create(new ProvaObject[]{ProvaConstantImpl.create("a"),ProvaConstantImpl.create("b")});
	}
	
	public static String getQuery(){
		return query;
	}
	
	static final String kAgent = "prova";

	static final String kPort = null;

	@Test
	public void list_ops() {
		
		final String rulebase = "rules/service_ontology_test.prova";
		final int[] numSolutions = new int[] {1,0,1,0,1,1,1,1,0,1,1,1};
		
		ProvaCommunicator prova = new ProvaCommunicatorImpl(kAgent,kPort,rulebase,ProvaCommunicatorImpl.ASYNC);
	}
	
	public static String query = "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"+
"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
"SELECT ?agent WHERE \n"+
	"{ ?task a :Task . FILTER regex(str(?task), 'add')\n"+ 
                                      "?task :performedBy ?role .\n"+
                                       "?agent :hasRole ?role}";
}
