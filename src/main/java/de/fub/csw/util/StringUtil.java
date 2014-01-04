/**
 * 
 */
package de.fub.csw.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaKnowledgeBase;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaKnowledgeBaseImpl;
import de.fub.csw.AbstractTask;
import de.fub.csw.TaskManagementCenter;
import de.fub.csw.constant.StringConstants;

/**
 * @author Administrator
 * 
 */
public class StringUtil {
	
	public static ProvaKnowledgeBase kb = new ProvaKnowledgeBaseImpl();
	

	public static String getWebServiceParameters(Object serviceName) {
		return "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"+
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"SELECT ?wsdl ?operation\n"+
		"FROM <"+StringConstants.SWF_ONTOLOGY+">\n"+
	    "WHERE {\n"+
			"?service a :WebService .?service a :WebService . FILTER regex(str(?service), '"+serviceName+"')\n"+
			"?service :wsdl ?wsdl .\n"+
			"?service :operation ?operation\n"+
		"}";
	}
		
	public static String getServiceTypeSPARQLQuery(Object serviceName) {
		return "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"+
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"SELECT ?serviceType\n"+
		"FROM <"+StringConstants.SWF_ONTOLOGY+">\n"+
	    "WHERE {\n"+
			"?service a ?serviceType . FILTER regex(str(?service), '"+serviceName+"')\n"+
			"?serviceType rdfs:subClassOf :Service\n"+
		"}";

	}
	
	public static String getProvaAgentParameters(Object serviceName) {
		return "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"+
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"SELECT ?service ?address ?interface\n"+
		"FROM <"+StringConstants.SWF_ONTOLOGY+">\n"+
	    "WHERE {\n"+
			"?service a :ProvaAgent . FILTER regex(str(?service), '"+serviceName+"')\n"+
			"?service :address ?address .\n"+
			"?service :interface ?interface\n"+
		"}";
	}
	
	public static void test(Object taskID){
		Map taskMap = TaskManagementCenter.taskMap;
		AbstractTask task = (AbstractTask) taskMap.get(taskID);
		ProvaObject[] inputs = task.getInList().getFixed();
		for (int i = 0; i < inputs.length; i++) {
			ProvaConstant obj = (ProvaConstant)inputs[i];
		}
	}
	
	public static String getServiceSPARQLQuery(Object taskName) {
		
		return "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"+
        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
		"SELECT DISTINCT ?task ?service ?serviceType\n"+
		"FROM <"+StringConstants.SWF_ONTOLOGY+">\n"+
	    "WHERE {\n"+
			"?task a ?Task . FILTER regex(str(?task), '"+taskName+"')\n"+
			"?task :uses ?service .\n"+
			"?service a ?serviceType .\n"+
			"?serviceType rdfs:subClassOf :Service\n"+
		"}";

	}
		
	
	public static String getAgentSPARQLQuery(Object taskName) {
		return   "PREFIX : <http://www.corporate-semantic-web.de/sswf2013#>\n"+
	        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
	        "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"+
	        "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"+
	        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
			"SELECT DISTINCT ?task ?role ?agent\n"+
			"FROM <"+StringConstants.SWF_ONTOLOGY+">\n"+
		    "WHERE {\n"+
				"?task a ?Task . FILTER regex(str(?task), '"+taskName+"')\n"+
				"?agent :hasRole ?role .\n"+
				"?task :performedBy ?role .\n"+
			"}";

	}
	
	
	
	public static String constructSPARQLQuery5(Object taskName) {
		return   "PREFIX service: <http://de.dbpedia.org/redirects/ruleml/service.owl#>\n"+
	        "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"+
	        "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"+
			"SELECT ?eTask\n"+
			"FROM <"+StringConstants.SWF_TASK_SERVICE_ONTOLOGY+">\n"+
			"FROM <"+StringConstants.SWF_SERVICE_ONTOLOGY+">\n"+
		    "WHERE {\n"+
		         "{"+
				"?task a service:Task . FILTER regex(str(?task), '"+taskName+"')\n"+
				"?task service:equivalentWith ?eTask .\n"+
				"}"+
				"UNION"+
				"{"+
				"?eTask a service:Task .\n"+
				"?eTask service:equivalentWith ?task . FILTER regex(str(?task), '"+taskName+"')\n"+
				"}"+
			"}";
	}
	
	public static void main(String[] args){
		List lit = new ArrayList();
		lit.add(2);
		System.out.println(lit.toString());
	}
	
}
