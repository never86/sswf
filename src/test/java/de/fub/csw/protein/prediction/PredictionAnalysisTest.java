package de.fub.csw.protein.prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import de.fub.csw.constant.StringConstants;
import de.fub.csw.protein.prediction.QuickGOClient;
import de.fub.csw.util.ProjectUtil;

public class PredictionAnalysisTest {

	private static QueryEngine engine;
	
	private static String workDir = StringConstants.userDir + File.separator
			+ "rules" + File.separator + "protein_prediction_analysis"
			+  File.separator;


	/**
	 * 
	 * @param args
	 * 
	 * @throws Exception
	 */

	public static void main(String[] args) throws Exception {
		File fin = new File(workDir+"function_prediction_human.txt");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fin);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		String predictedGoTerm = "";
		String protein = "";
		boolean tag = false;
		int count = 0;
		while ((line = br.readLine()) != null) {
			String[] items = line.split("	");
			protein = items[0].trim();
			predictedGoTerm = items[1].trim();
			QuickGOClient.dir = workDir;
			String ontoFilePath = QuickGOClient.getOnto(predictedGoTerm);
			Document doc = new SAXReader().read(new File(ontoFilePath));
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
			int classSize = doc.getRootElement().elements().size();
			long start1 = System.currentTimeMillis();
			List reliableGoterms = getGOTermsByProteinID(protein);
			int i = 0;
			for (; i < reliableGoterms.size(); i++) {
				String reliableGoterm = (String) reliableGoterms.get(i);
				if (analysis(reliableGoterm, predictedGoTerm))
					break;
			}
			long overallReasoningTime = System.currentTimeMillis() - start1;
			if (i == reliableGoterms.size())
				tag = false;
			else
				tag = true;

			System.out.println(++count + "\t" + protein + "\t"
					+ predictedGoTerm + "\t" + classSize + "\t" + tag + "\t"
					+ classSize + "\t" + i + "\t" + overallReasoningTime);
		}
	}

	public static boolean analysis(String reliableGOTerm, String predictedGoTerm) {
		return processQuery(
				"PREFIX : <http://www.geneontology.org/go#>\n"
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

		} catch (Exception e) {
			System.out.println("Query parser error: " + e);
		}
		return false;
	}

	public static List getGOTermsByProteinID(String proteinID) {
		// URL for annotations from QuickGO for one protein
		List<String> terms = new ArrayList<String>();
		URL u = null;
		try {
			u = new URL("http://www.ebi.ac.uk/QuickGO/GAnnotation?protein="
					+ proteinID + "&format=tsv");
			// Connect
			HttpURLConnection urlConnection = (HttpURLConnection) u
					.openConnection();
			// Get data
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));
			// Read data
			List<String> columns = Arrays.asList(rd.readLine().split("\t"));
			// System.out.println(columns);
			// Collect the unique terms as a sorted set
			// Find which column contains GO IDs
			int termIndex = columns.indexOf("GO ID");
			int evidenceIndex = columns.indexOf("Evidence");
			// Read the annotations line by line
			String line;
			while ((line = rd.readLine()) != null) {
				String[] fields = line.split("\t");
				if (!fields[evidenceIndex].equalsIgnoreCase("IEA")
						&& !fields[evidenceIndex].equalsIgnoreCase("ISS")) {
					if (!ProjectUtil.inList(fields[termIndex], terms))
						terms.add(fields[termIndex]);
				}
				// break;
			}
			// close input when finished
			rd.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return terms;
	}
}
