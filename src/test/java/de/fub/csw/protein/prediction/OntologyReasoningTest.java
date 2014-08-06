package de.fub.csw.protein.prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import de.fub.csw.constant.StringConstants;
import de.fub.csw.protein.prediction.QuickGOClient;
import de.fub.csw.util.ProjectUtil;

public class OntologyReasoningTest {

	private static QueryEngine engine;

	private static String workDir = StringConstants.userDir + File.separator
			+ "rules" + File.separator + "protein_prediction_analysis"
			+ File.separator;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File fin = new File(workDir + "function_prediction_human.txt");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fin);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		String predictedGoTerm = "";
		List list1 = new ArrayList();
		String protein = "";
		int j = 0;
		boolean tag = false;
		int count = 0;
		while ((line = br.readLine()) != null) {
			String[] items = line.split("	");
			protein = items[0].trim();
			predictedGoTerm = items[1].trim();

			QuickGOClient.dir = workDir;
			String ontoFilePath = QuickGOClient.getOnto(predictedGoTerm);
			Document doc = new SAXReader().read(new File(ontoFilePath));

			long start = System.currentTimeMillis();
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			// Load the wine ontology from the web.
			OWLOntology ont = manager.loadOntologyFromOntologyDocument(IRI
					.create(new File(ontoFilePath)));

			OWLReasoner reasoner = new Reasoner.ReasonerFactory()
					.createReasoner(ont);
			reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY,
					InferenceType.CLASS_ASSERTIONS,
					InferenceType.OBJECT_PROPERTY_ASSERTIONS);
			engine = QueryEngine.create(manager, reasoner);

			analysis("GO:0008150", predictedGoTerm);
			long reasoningTimePerOntology = System.currentTimeMillis() - start;
			int classSize = doc.getRootElement().elements().size();

			if (!ProjectUtil.inList(predictedGoTerm, list1)) {
				list1.add(predictedGoTerm);
				System.out.println(++count + "\t" + protein + "\t"
						+ predictedGoTerm + "\t" + reasoningTimePerOntology
						+ "\t" + classSize);
			}
		}
	}

	public static boolean analysis(String reliableGOTerm, String predictedGoTerm) {

		return processQuery("PREFIX : <http://www.geneontology.org/go#>\n"
				+ "ASK {SubClassOf(:" + predictedGoTerm + ", :"
				+ reliableGOTerm + ")}", engine);

	}

	public static boolean processQuery(String q, QueryEngine engine) {
		try {
			// Create a query object from it's string representation
			Query query = Query.create(q);
			// Execute the query and generate the result set
			QueryResult result = engine.execute(query);
			if (result.ask()) {
				return true;
			} else {
				return false;
			}
		} catch (QueryParserException | QueryEngineException e) {
			System.out.println("Query parser error: " + e);
		}
		return false;
	}
}
