package de.fub.csw.sparql;


import org.junit.After;
import org.junit.Test;

import ws.prova.api2.ProvaCommunicator;
import ws.prova.api2.ProvaCommunicatorImpl;

public class ProteinAnalysisTest {

	static final String kAgent = "prova";

	static final String kPort = null;

	// Unique key identifying the consulted input (useful in interactive environment)
	int key = 0;

	private ProvaCommunicator prova = null;
	
	@After
	public void shutdown() {
		if( prova!=null ) {
			prova.shutdown();
			prova = null;
		}
	}
	
	@Test
	public void sparql() {
		final String rulebase = "src/main/app/rules/protein_prediction_analysis/simiarity_analysis.prova";
		prova = new ProvaCommunicatorImpl(kAgent,kPort,rulebase,ProvaCommunicatorImpl.SYNC);

		try {
			synchronized(this) {
				wait(20000);
			}
		} catch (Exception e) {
		}
	}
	


}
