package de.fub.csw.protein.prediction;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import de.fub.csw.constant.StringConstants;

public class QuickGOClient {

	public static String dir = StringConstants.appDir + File.separator
			+ "rules" + File.separator + "protein_prediction_analysis"
			+ File.separator;

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
					if (!existInList(fields[termIndex], terms))
						terms.add(fields[termIndex]);
				}
				// break;
			}
			// close input when finished
			rd.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return terms;
	}

	private static boolean existInList(String term, List<String> terms) {
		for (int i = 0; i < terms.size(); i++) {
			if (term.equalsIgnoreCase(terms.get(i)))
				return true;
		}
		return false;
	}

	public static String getOnto(String targetGOTerm) throws Exception {
		String goFile = targetGOTerm.substring(3);
		if (new File(dir + "temp" + File.separator + goFile)
				.exists())
			return dir +  "temp" + File.separator + goFile
					+ ".xml";
		else {
			SAXReader reader = new SAXReader();
			Document document = null;
			try {
				document = reader.read(new ByteArrayInputStream(getText(
						"http://amigo1.geneontology.org/cgi-bin/amigo/browse.cgi?&format=rdfxml&term="
								+ targetGOTerm).getBytes()));
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			transform(document.getRootElement(), goFile);
			return dir + goFile + ".xml";
		}

	}

	private static void transform(Element object, String fileName) {
		try {
			// Create transformer factory
			TransformerFactory factory = TransformerFactory.newInstance();
			// Use the factory to create a template containing the xsl file
			Templates template = factory.newTemplates(new StreamSource(
					new FileInputStream(dir + "goRDF2OWL.xsl")));
			Transformer xformer = template.newTransformer();
			Source source = new StreamSource(
					inputStreamFromString(StringConstants.ENTITIES_DECLARATION
							+ object.asXML()));
			XMLWriter writer = new XMLWriter(new FileWriter(dir +  "temp" + File.separator + fileName
					+ ".xml"));
			DocumentResult result = new DocumentResult();
			xformer.transform(source, result);
			writer.write(result.getDocument());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static InputStream inputStreamFromString(String str) {
		byte[] bytes = null;
		try {
			bytes = str.getBytes("GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ByteArrayInputStream(bytes);
	}

	public static String getText(String url) throws Exception {
		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		StringBuilder response = new StringBuilder();
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);
		in.close();
		String temp = response
				.toString()
				.replace(
						"<!DOCTYPE go:go PUBLIC \"-//Gene Ontology//Custom XML/RDF Version 2.0//EN\" \"http://www.geneontology.org/dtd/go.dtd\">",
						"");
		return temp;
	}

}
