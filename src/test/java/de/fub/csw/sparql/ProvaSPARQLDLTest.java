package de.fub.csw.sparql;


import org.junit.After;
import org.junit.Test;

import ws.prova.api2.ProvaCommunicator;
import ws.prova.api2.ProvaCommunicatorImpl;

public class ProvaSPARQLDLTest {

	static final String kAgent = "prova";

	static final String kPort = null;

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
	public void sparqldl() {
		final String rulebase = "src/main/app/rules/prova/sparqldl.prova";
		prova = new ProvaCommunicatorImpl(kAgent,kPort,rulebase,ProvaCommunicatorImpl.SYNC);
	}
}
