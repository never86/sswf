package de.fub.csw.sparql;

import java.io.FileWriter;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * This class translates the raw gene ontology to an ontology that contains only GO term hierarchy
 * Note the RDF root element should be handled manually. 
 *
 */
public class GeneOntologyTransformation {

	public static void main(String[] args) throws Exception {
		
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader
					.read("D://go.xml");
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Loading XML is finished.");
		
		List list = document.getRootElement().elements();
		
		System.out.println(list.size()+" classes is found.");
		
		for (int i = 0; i < list.size(); i++) {
			Element classEle = (Element) list.get(i);
			List elements = classEle.elements();
			for (int j = 0; j <elements.size(); j++) {
				if(!((Element)elements.get(j)).getName().equalsIgnoreCase("subClassOf"))
					classEle.remove((Element)elements.get(j));
			}
		}
		
		System.out.println("Processing XML is finished.");
		
		XMLWriter writer = new XMLWriter(new FileWriter("D://output.xml"));

		writer.write(document);
		writer.close();
		
	}
}