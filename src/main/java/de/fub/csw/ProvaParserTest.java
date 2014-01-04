package de.fub.csw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;

import ws.prova.kernel2.ProvaKnowledgeBase;
import ws.prova.kernel2.ProvaResolutionInferenceEngine;
import ws.prova.kernel2.ProvaResultSet;
import ws.prova.kernel2.ProvaRule;
import ws.prova.parser2.ProvaParsingException;
import ws.prova.parser2.ProvaParserImpl;
import ws.prova.reference2.ProvaKnowledgeBaseImpl;
import ws.prova.reference2.ProvaResolutionInferenceEngineImpl;
import ws.prova.reference2.ProvaResultSetImpl;

public class ProvaParserTest {

	@Test
	public void simpleParse() {
		String t = "sponsor(contact('+','+'),'+',results('-','-','-'), performative('-'))";
		ProvaKnowledgeBase kb = new ProvaKnowledgeBaseImpl();
		ProvaResultSet resultSet = new ProvaResultSetImpl();
		StringReader sr = new StringReader(
				"interface("+t+").\n");
		BufferedReader in = new BufferedReader(sr);
		
		ProvaParserImpl parser = new ProvaParserImpl("inline1", new Object[] {});
		try {
			List<ProvaRule> rules = parser.parse(kb, resultSet, in);
			// Run each goal
			for( ProvaRule rule : rules ) {
				if( rule.getHead()!=null ) 
					System.out.println("AAAAAAAAAAAAAAAA111:"+rule.getHead().getTerms().getFixed()[0].getClass());
			}
		} catch (ProvaParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
