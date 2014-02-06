package de.fub.csw.protein.prediction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

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

public class GOClient {

	public static String dir = StringConstants.appDir + File.separator
			+ "rules" + File.separator + "protein_prediction_analysis"
			+ File.separator;

	public static String getOnto(Object targetGOTerm) {
		String goFile = targetGOTerm.toString().substring(3);
		if (new File(dir + goFile).exists())
			return dir + goFile + ".xml";
		else {
			SAXReader reader = new SAXReader();
			Document document = null;
			try {
				document = reader
						.read("http://amigo.geneontology.org/cgi-bin/amigo/browse.cgi?&format=rdfxml&term="
								+ targetGOTerm);
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
			XMLWriter writer = new XMLWriter(new FileWriter(dir + fileName
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

	public static void main(String[] args) {
		System.out.println(getOnto("GO:0006564"));
		;
	}
}
