package ws.prova.mule.impl;



import java.io.File;

import java.io.IOException;

import java.net.URLDecoder;

import java.util.Date;

import java.util.Properties;

import java.util.UUID;

import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.ConcurrentMap;



import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import org.mule.api.MuleEventContext;

import org.mule.api.MuleMessage;

import org.mule.api.client.MuleClient;

import org.mule.api.construct.FlowConstruct;

import org.mule.api.construct.FlowConstructAware;

import org.mule.api.endpoint.EndpointBuilder;

import org.mule.api.endpoint.MalformedEndpointException;

import org.mule.api.lifecycle.Callable;

import org.mule.api.lifecycle.Initialisable;

import org.mule.api.lifecycle.InitialisationException;

import org.mule.api.lifecycle.RecoverableException;

import org.mule.api.registry.MuleRegistry;

import org.mule.client.DefaultLocalMuleClient;

import org.mule.component.simple.LogComponent;

import org.mule.construct.Flow;

import org.mule.endpoint.DefaultInboundEndpoint;

import org.mule.transport.service.DefaultTransportServiceDescriptor;



import ws.prova.api2.ProvaCommunicator;

import ws.prova.api2.ProvaCommunicatorImpl;

import ws.prova.esb2.ProvaAgent;

import ws.prova.kernel2.ProvaList;

import ws.prova.reference2.ProvaConstantImpl;

import de.fub.csw.constant.StringConstants;



/**

 * @author never86

 *

 */

/**

 * @author never86

 * 

 */

public class HttpEndpointUMOImpl extends LogComponent implements Initialisable,

		Callable, FlowConstructAware, ProvaAgent {



	protected static transient Log logger = LogFactory

			.getLog(HttpEndpointUMOImpl.class);



	private String agentName;

	private FlowConstruct fc;

	private ProvaCommunicator comm;

	private String tmpAgent;

	String req_content = "";



	protected static ConcurrentMap<String, ProvaCommunicator> communicators = new ConcurrentHashMap<String, ProvaCommunicator>();



	/**

	 * Called once to set up this Prova message object

	 */

	public void  initialise() throws InitialisationException,

			RecoverableException {

		String rulebase = "";

		DefaultInboundEndpoint inboundEndpoint = (DefaultInboundEndpoint) ((Flow) fc)

				.getMessageSource();

		if (inboundEndpoint.getProperties() != null) {

			if (inboundEndpoint.getProperties().containsKey("rulebase"))

				rulebase = (String) inboundEndpoint.getProperties().get(

						"rulebase");

		}

		agentName = inboundEndpoint.getName();



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



		StringConstants.appDir = fc.getMuleContext().getRegistry()

				.get("app.home");

	}



	/*

	 * Process an inbound message that arrives on this endpoint

	 * 

	 * @see

	 * org.mule.component.simple.LogComponent#onCall(org.mule.api.MuleEventContext

	 * )

	 */

	public Object onCall(MuleEventContext context) throws Exception {

		MuleMessage inbound = context.getMessage();

		String incomingHttpMsg = "";

		ProvaList incomingProvaMsg = null;

		try {

			// translate incoming String to Prova message

			incomingHttpMsg = URLDecoder.decode(inbound.getPayloadAsString(),

					inbound.getEncoding());

			int pos = incomingHttpMsg.indexOf("payload=");

			incomingHttpMsg = incomingHttpMsg.replace("%20", " ");

			if (pos != -1) {

				// the message is from user, i.e., the request

				req_content = incomingHttpMsg.substring(pos + 8);

				incomingProvaMsg = (ProvaList) new RuleMLIDL2ProvaList()

						.transform(req_content);

			} 

//			else {

//				// the message is from other Prova agent, i.e., the answer

//				req_content = incomingHttpMsg;

//				incomingProvaMsg = (ProvaList) new String2ProvaList()

//						.transform(req_content);

//			}



		} catch (Exception ex) {

			ex.printStackTrace();

			logger.error("Translation of message into Prova message failed: "

					+ req_content);

			return "Translation of the following request into Prova message failed:\n\n"

					+ req_content;

		}



		logger.info("AGENT: " + getAgentName() + " received the message:"

				+ incomingProvaMsg);



		// the message is from Prova agent, i.e., the answer

		if (!incomingProvaMsg.performative().equals("query-sync")) {

			// Add the message to the asynchronous Prova Communicator queue

			comm.addMsg(incomingProvaMsg);

			context.setStopFurtherProcessing(true);

			return null;

		} else {

			// the message is from user, and a temporary UMO is registered

				// to act as the user

			tmpAgent = UUID.randomUUID() + "";

			incomingProvaMsg.getFixed()[2] = ProvaConstantImpl.create(tmpAgent);

			try {

				MuleRegistry helper = fc.getMuleContext().getRegistry();

				EndpointBuilder builder = new DefaultTransportServiceDescriptor(

						tmpAgent, new Properties(), null)

						.createEndpointBuilder("jms://topic:" + tmpAgent);

				helper.registerEndpointBuilder(tmpAgent, builder);

				logger.info("A temporary UMO '" + tmpAgent + "' was registered.");

			} catch (Exception e) {

				e.printStackTrace();

				context.setStopFurtherProcessing(true);

				return e.toString()

						+ " The user '"

						+ tmpAgent

						+ "'is already registered. Please use another user name as sender address."; // return

			}

			// Add the message to the asynchronous Prova Communicator queue

			comm.addMsg(incomingProvaMsg);

			context.setStopFurtherProcessing(true);

			// collect synchronously all answers

			MuleMessage m = null;

			String answer = "";

			MuleClient client = new DefaultLocalMuleClient(fc.getMuleContext());

			int timeout = 1000000; // default timeout of receiving workflow

									// results

			m = client.request("jms://topic:" + tmpAgent, timeout);

			if (m != null) {

				String payload = (String) new ProvaList2HTML(req_content)

						.transform(m.getPayload());

				answer = answer + payload;

			}



			// unregister temp UMO

			try {

				fc.getMuleContext().getRegistry().unregisterEndpoint(tmpAgent);

				logger.info("The temporary UMO '" + tmpAgent + "' was unregistered.");

			} catch (Exception exx) {

				logger.error("Can not unregister synchronous UMO for "

						+ tmpAgent);

				logger.error(exx);

				return exx.toString();

			}

			

			InfiniteLoopDetector.unregisterWf(incomingProvaMsg.getFixed()[0].toString());

			return answer;

		}

	}



	

	/*

	 * (non-Javadoc)

	 * 

	 * @see ws.prova.esb2.ProvaAgent#getAgentName()

	 */

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

		try {

			MuleClient client = new DefaultLocalMuleClient(fc.getMuleContext());

			client.dispatch(receiver, provaList, null);



			logger.info("AGENT:" + getAgentName() + " forwards " + provaList

					+ " To:" + receiver);



		} catch (MalformedEndpointException e) {

			e.printStackTrace();

		}



	}



	/*

	 * (non-Javadoc)

	 * 

	 * @see ws.prova.esb2.ProvaAgent#receive(ws.prova.kernel2.ProvaList)

	 */

	@Override

	public void receive(ProvaList arg0) throws Exception {

		// TODO Auto-generated method stub



	}



	@Override

	public void setFlowConstruct(FlowConstruct flowConstruct) {

		this.fc = flowConstruct;

	}



}