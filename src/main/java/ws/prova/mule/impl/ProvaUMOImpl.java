package ws.prova.mule.impl;

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URLDecoder;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.MuleEventContext;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.mule.api.construct.FlowConstruct;
import org.mule.api.construct.FlowConstructAware;
import org.mule.api.endpoint.MalformedEndpointException;
import org.mule.api.lifecycle.Callable;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.lifecycle.RecoverableException;
import org.mule.api.source.MessageSource;
import org.mule.client.DefaultLocalMuleClient;
import org.mule.component.simple.LogComponent;
import org.mule.construct.Flow;
import org.mule.endpoint.DefaultInboundEndpoint;
import org.mule.source.StartableCompositeMessageSource;

import ws.prova.api2.ProvaCommunicator;
import ws.prova.api2.ProvaCommunicatorImpl;
import ws.prova.esb2.ProvaAgent;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;
import de.fub.csw.TaskManagementCenter;

public class ProvaUMOImpl extends LogComponent implements Initialisable,
		Callable, FlowConstructAware, ProvaAgent {

	protected static transient Log logger = LogFactory
			.getLog(ProvaUMOImpl.class);

	private String agentName;
	private FlowConstruct fc;
	private ProvaCommunicator comm;

	protected static ConcurrentMap<String, ProvaCommunicator> communicators = new ConcurrentHashMap<String, ProvaCommunicator>();

	/**
	 * Called once to set up this Prova message object
	 */
	public void initialise() throws InitialisationException,
			RecoverableException {
		String rulebase = "";
		MessageSource msgSource = ((Flow) fc).getMessageSource();
		if (msgSource instanceof DefaultInboundEndpoint) {
			DefaultInboundEndpoint inboundEndpoint = (DefaultInboundEndpoint) ((Flow) fc)
					.getMessageSource();
			if (inboundEndpoint.getProperties() != null) {
				if (inboundEndpoint.getProperties().containsKey("rulebase"))
					rulebase = (String) inboundEndpoint.getProperties().get(
							"rulebase");
			}
			agentName = inboundEndpoint.getName();
		} else if (msgSource instanceof StartableCompositeMessageSource) {
			StartableCompositeMessageSource compositeSource = (StartableCompositeMessageSource) ((Flow) fc)
					.getMessageSource();
			List list = compositeSource.getSources();
			for (int i = 0; i < list.size(); i++) {
				DefaultInboundEndpoint inboundEndpoint = (DefaultInboundEndpoint) list
						.get(i);
				if (inboundEndpoint.getProperties() != null) {
					if (inboundEndpoint.getProperties().containsKey("rulebase"))
						rulebase = (String) inboundEndpoint.getProperties()
								.get("rulebase");
					agentName = inboundEndpoint.getName();
					break;
				}
			}
		}

		if (communicators.containsKey(agentName)) {
			comm = communicators.get(agentName);
			return;
		}

		try {
			comm = new ProvaCommunicatorImpl(agentName, null, rulebase,
					ProvaCommunicatorImpl.ASYNC, this, null);
			communicators.put(agentName, comm);
		} catch (Exception ex) {
			logger.error("Can not initialize Prova communicator");
			ex.printStackTrace();
		}

	}

	/*
	 * Process an inbound message that arrives on this endpoint
	 * 
	 * @see
	 * org.mule.component.simple.LogComponent#onCall(org.mule.api.MuleEventContext
	 * )
	 */
	public Object onCall(MuleEventContext context) throws Exception {
		ProvaList incomingProvaMsg = null;
		MuleMessage inbound = context.getMessage();
		if (inbound.getPayload() instanceof String) {
			// the message is from Human agent
			RuleML2ProvaTranslator ruleml2prova = new RuleML2ProvaTranslator();
			String http_message = URLDecoder.decode(
					inbound.getPayloadAsString(), inbound.getEncoding());
			incomingProvaMsg = (ProvaList) ruleml2prova.transform(http_message
					.substring(http_message.indexOf("payload") + 8));
		} else if (inbound.getPayload() instanceof ProvaList) {
			// The message is from other Prova agents
			incomingProvaMsg = (ProvaList) inbound.getPayload();
		}

		logger.info("AGENT: " + getAgentName() + " received the message:"
				+ incomingProvaMsg);
		comm.addMsg(incomingProvaMsg);
		context.setStopFurtherProcessing(true);

		return null;
	}

	public String getAgentName() {
		return agentName;
	}

	/*
	 * this method is invoked when the 'sendMsg' primitive is executed
	 * 
	 * @see ws.prova.esb2.ProvaAgent#send(java.lang.String,
	 * ws.prova.kernel2.ProvaList)
	 */
	public void send(String receiver, ProvaList provaList) throws Exception {
		MuleClient client = new DefaultLocalMuleClient(fc.getMuleContext());
		if (!InfiniteLoopDetector.registerMsg(
				provaList.getFixed()[0].toString(), provaList)) {
			provaList.getFixed()[3] = ProvaConstantImpl.create("inform");
			ProvaObject[] fixed = new ProvaObject[2];
			fixed[0] = ProvaConstantImpl.create(TaskManagementCenter
					.getWfName(provaList.getFixed()[0].toString()));
			ProvaObject[] fixed1 = new ProvaObject[2];
			fixed1[0] = ProvaConstantImpl.create("infiniteLoop");
			fixed1[1] = ProvaConstantImpl.create("msg");
			ProvaList list1 = ProvaListImpl.create(fixed1);
			fixed[1] = list1;
			ProvaList list = ProvaListImpl.create(fixed);
			provaList.getFixed()[4] = list;
			Object translatedSend = new Prova2RuleMLTranslator()
					.transform(provaList);
			client.dispatch("humanAgent", translatedSend, null);
			System.out
					.println("---->An infinite loop is detected. The suspicous message is :"
							+ provaList);
			return;
		}
		try {
			// send the message to httpEndPoint as string
			if (receiver.equalsIgnoreCase("httpEndpoint"))
				client.dispatch(receiver, provaList.toString(), null);
			else if (receiver.equalsIgnoreCase("humanAgent")) {
				Object translatedSend = new Prova2RuleMLTranslator()
						.transform(provaList);
				client.dispatch(receiver, translatedSend, null);
			} else
				client.dispatch(receiver, provaList, null);
			logger.info("AGENT:" + getAgentName() + " forwards " + provaList
					+ " To:" + receiver);

			// add feedback to the knowledge base
			if (provaList.getFixed()[3].toString().equalsIgnoreCase("start")) {
				String taskID = ((ProvaList) provaList.getFixed()[4])
						.getFixed()[1].toString();
				BufferedReader inRules = new BufferedReader(new StringReader(
						"feedback(" + taskID + ", success)."));
				comm.consultSync(inRules, "success©\rules", new Object[] {});
			}

		} catch (MalformedEndpointException e) {
			// add feedback to the knowledge base
			if (provaList.getFixed()[3].toString().equalsIgnoreCase("start")) {
				String taskID = ((ProvaList) provaList.getFixed()[4])
						.getFixed()[1].toString();
				BufferedReader inRules = new BufferedReader(new StringReader(
						"feedback(" + taskID + ", failed)."));
				comm.consultSync(inRules, "failed©\rules", new Object[] {});
			}
		}

	}

	@Override
	public void receive(ProvaList arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFlowConstruct(FlowConstruct flowConstruct) {
		// TODO Auto-generated method stub
		this.fc = flowConstruct;
	}
}