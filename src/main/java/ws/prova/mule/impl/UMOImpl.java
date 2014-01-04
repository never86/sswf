package ws.prova.mule.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.client.MuleClient;
import org.mule.api.lifecycle.Callable;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.component.simple.LogComponent;

import ws.prova.api2.ProvaCommunicator;
import ws.prova.kernel2.ProvaList;

public class UMOImpl extends LogComponent implements Callable {

	protected static transient Log logger = LogFactory
			.getLog(UMOImpl.class);

	private String agentName;

	protected static ConcurrentMap<String, ProvaCommunicator> communicators = new ConcurrentHashMap<String, ProvaCommunicator>();


	/**
	 * Process an inbound message that arrives on this endpoint
	 */
	public Object onCall(MuleEventContext context) throws Exception {
		ProvaList list = (ProvaList) context.getMessage().getPayload();
		MuleClient client = new DefaultLocalMuleClient(context.getMuleContext());
		client.dispatch(list.getFixed()[2].toString(), list, null);
		return null;
	}

	public String getAgentName() {
		return agentName;
	}
	
}