/**
 * @className TaskValidation.java
 * 
 * Copyright (c) 2010-2014 Corporate Semantic Web.
 * Königin-Luise-Straße 24 - 26 14195 Berlin, Free University Berlin, Germany
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fub.csw;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import ws.prova.api2.ProvaCommunicator;
import ws.prova.api2.ProvaCommunicatorImpl;
import ws.prova.exchange.ProvaSolution;
import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaKnowledgeBase;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.kernel2.ProvaResultSet;
import ws.prova.kernel2.ProvaRule;
import ws.prova.kernel2.ProvaVariablePtr;
import ws.prova.parser2.ProvaParserImpl;
import ws.prova.parser2.ProvaParsingException;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaKnowledgeBaseImpl;
import ws.prova.reference2.ProvaListImpl;
import ws.prova.reference2.ProvaResultSetImpl;
import ws.prova.reference2.ProvaVariableImpl;
import ws.prova.reference2.ProvaVariablePtrImpl;
import de.fub.csw.constant.StringConstants;
import de.fub.csw.log.LogTool;

/**
 * class description
 * 
 * @version 0.1
 * 
 * @date 2012-1-01
 * 
 * @author Zhili Zhao
 */
public class TaskAux {

	private static String regex = "(\\#[^,]*!)";

	private static LogTool logTool = new LogTool(TaskAux.class);
	private static Logger logger = logTool.getLogger();

	private static ProvaCommunicator comm;

	static org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory dcf = null;

	// /**
	// * Validate if a user specification of task and its description in
	// * Task-Service ontology is matched
	// *
	// * @param serviceName
	// * @param inList
	// * @param outList
	// * @return
	// */
	//
	// public static synchronized String taskValidation(Object taskID,
	// Object serviceName) {
	// String isMatched = "unmatched";
	// Document doc = null;
	// List inputElements = null;
	// List ouputElements = null;
	// Element taskElement = null;
	// try {
	// doc = new SAXReader().read(new URL(StringConstants.SWF_ONTOLOGY));
	//
	// Iterator iter = doc.getRootElement().elementIterator();
	// while (iter.hasNext()) {
	// Element ele = (Element) iter.next();
	// if (ele.attribute("ID") != null
	// && ele.attributeValue("ID").equalsIgnoreCase(
	// serviceName.toString())) {
	// taskElement = ele;
	// break;
	// }
	// }
	// inputElements = taskElement.elements("hasInput");
	// ouputElements = taskElement.elements("hasOutput");
	// } catch (MalformedURLException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (DocumentException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// ProvaList inList, outList;
	// AbstractTask task = TaskManagementCenter.taskMap.get(taskID);
	// if (task.isModified()) {
	// inList = task.getNewInlist();
	// outList = task.getNewOutList();
	// } else {
	// inList = task.getInList();
	// outList = task.getOutList();
	// }
	//
	// ProvaObject[] inputs = inList.getFixed();
	// ProvaObject[] ouputs = outList.getFixed();
	//
	// if (inputs.length != inputElements.size()
	// || ouputs.length != ouputElements.size()) {
	// return "unmatched";
	// }
	//
	// for (int i = 0; i < inputElements.size(); i++) {
	// Element inputElement = ((Element) inputElements.get(i))
	// .element("Input");
	// int parameterPosition = Integer.parseInt(inputElement.elementText(
	// "sequence").trim());
	// String parameterType = inputElement.elementText("parameterType")
	// .trim();
	// ProvaConstant object = (ProvaConstant) inputs[parameterPosition - 1];
	// if (isTypesMatched(object.getObject().getClass().getName(),
	// parameterType))
	// continue;
	// else {
	// return "unmatched";
	// }
	// }
	//
	// isMatched = "matched";
	//
	// return isMatched;
	// }

	// /**
	// * @param type
	// * @param parameterType
	// * @return
	// */
	// private static boolean isTypesMatched(String provaType, String
	// parameterType) {
	// // TODO Auto-generated method stub
	// if (provaType.equalsIgnoreCase("java.lang.Double")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#decimal"))
	// return true;
	// if (provaType.equalsIgnoreCase("java.lang.Double")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#float"))
	// return true;
	// if (provaType.equalsIgnoreCase("java.lang.Integer")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#int"))
	// return true;
	// if (provaType.equalsIgnoreCase("java.lang.Double")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#long"))
	// return true;
	// if (provaType.equalsIgnoreCase("java.lang.Double")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#short"))
	// return true;
	// if (provaType.equalsIgnoreCase("java.lang.Double")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#double"))
	// return true;
	// if (provaType.equalsIgnoreCase("java.lang.Boolean")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#boolean"))
	// return true;
	// if (provaType.equalsIgnoreCase("java.lang.String")
	// && parameterType
	// .equalsIgnoreCase("http://www.w3.org/2001/XMLSchema#string"))
	// return true;
	// return false;
	// }

	public static ProvaList prepareReactionGroupResults(Object result) {
		List resList = (List) result;
		List objects = new ArrayList();
		objects.add(ProvaConstantImpl.create("outArgs"));
		for (int i = 0; i < resList.size(); i++) {
			ProvaList l = (ProvaList) resList.get(i);
			for (int j = 0; j < l.getFixed().length; j++)
				objects.add(l.getFixed()[j]);
		}
		ProvaObject[] provaObjects = new ProvaObject[objects.size()];
		for (int k = 0; k < objects.size(); k++)
			provaObjects[k] = (ProvaObject) objects.get(k);

		ProvaList list = ProvaListImpl.create(provaObjects);

		return list;
	}

	/**
	 * Transalte SWRLExpression in Task-Service ontology to Prova rule
	 * 
	 * @param object
	 * @return
	 */
	private static String transalteSWRLExpression2Prova(Element object) {
		try {
			// Create transformer factory
			TransformerFactory factory = TransformerFactory.newInstance();

			// Use the factory to create a template containing the xsl file
			Templates template = factory.newTemplates(new StreamSource(
					new FileInputStream(StringConstants.appDir+File.separator+"conf"+File.separator+"swrl2prova_condition.xslt")));

			Transformer xformer = template.newTransformer();
			Source source = new StreamSource(
					inputStreamFromString(StringConstants.ENTITIES_DECLARATION
							+ object.asXML()));
			StringWriter sw = new StringWriter();
			Result result = new StreamResult(sw);
			// Apply the xsl file to the source file and write the result to the
			// output file
			xformer.transform(source, result);
			return sw.toString().substring(
					sw.toString().indexOf("validateSWRLExpression"),
					sw.toString().lastIndexOf(","))
					+ ".";
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage());
		} catch (TransformerException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		logger.error("The translation from SWRL procondition expression to Prove rule is failed");

		return null;
	}

	/**
	 * Invoke Web Service dynamically
	 * 
	 * @param operationName
	 * @param taskName
	 * @param wsdlAddress
	 * @param paraList
	 * @return
	 */
	public static synchronized ProvaList dynamicInvokeService(Object taskID,
			Object operationName, Object wsdlAddress) {
		if (dcf == null)
			dcf = JaxWsDynamicClientFactory.newInstance();
		ProvaList inputList;
		AbstractTask task = TaskManagementCenter.taskMap.get(taskID);
		if (task.isModified())
			inputList = task.getNewInlist();
		else
			inputList = task.getInList();

		Object[] results = null;
		try {
			org.apache.cxf.endpoint.Client client = dcf
					.createClient(wsdlAddress.toString());
			Object[] parameters = new Object[inputList.getFixed().length];
			for (int i = 0; i < inputList.getFixed().length; i++) {
				Object para = ((ProvaConstant) inputList.getFixed()[i])
						.getObject();
				parameters[i] = para;
			}
			results = client.invoke(operationName.toString(), parameters);
		} catch (Exception e) {
			logger.error(e);
		}

		int num = 0;
		if (results != null)
			num = results.length;

		ProvaObject taskNameObject = ProvaConstantImpl.create(task
				.getTaskName());

		ProvaObject taskIDObject = ProvaConstantImpl.create(taskID);

		ProvaObject[] inObjects = new ProvaObject[inputList.getFixed().length + 1];
		inObjects[0] = ProvaConstantImpl.create("inArgs");
		for (int i = 1; i < inObjects.length; i++)
			inObjects[i] = inputList.getFixed()[i - 1];

		ProvaObject[] outObjects = new ProvaObject[1 + num];
		outObjects[0] = ProvaConstantImpl.create("outArgs");
		for (int i = 1; i <= num; i++)
			outObjects[i] = ProvaConstantImpl.create(results[i - 1]);

		ProvaList list = ProvaListImpl.create(new ProvaObject[] { taskIDObject,
				ProvaListImpl.create(inObjects),
				ProvaListImpl.create(outObjects) });

		return list;

	}

	/**
	 * @param serviceName
	 * @param userInputs
	 * @param expressionCatergory
	 * @return
	 */
	private static String swrlExpressionProcess(String serviceName,
			ProvaList userInputs) {
		String provaRule = "";

		Document doc = null;
		Element preconditionElement = null;
		Element taskElement = null;
		try {
			doc = new SAXReader().read(new URL(StringConstants.SWF_ONTOLOGY));
			Iterator iter = doc.getRootElement().elementIterator();
			while (iter.hasNext()) {
				Element ele = (Element) iter.next();
				if (ele.attribute("ID") != null
						&& ele.attributeValue("ID").equalsIgnoreCase(
								serviceName.toString())) {
					taskElement = ele;
					break;
				}
			}
			preconditionElement = taskElement.element("hasPrecondition");
			if (preconditionElement == null)
				return "";
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		provaRule = transalteSWRLExpression2Prova(preconditionElement);
		// Each variable in the rule begins with "#" and ends with "!".

		ProvaObject[] objects = userInputs.getFixed();
		Pattern pattern1 = Pattern.compile(regex);
		Matcher m1 = pattern1.matcher(provaRule);
		String variable = "";
		while (m1.find()) {
			variable = m1.group();
			Element ele = (Element) doc
					.selectSingleNode("//service:Input[@rdf:ID='"
							+ variable.substring(1, variable.length() - 1)
							+ "']");
			if (ele != null) {
				provaRule = provaRule.replaceAll(
						variable,
						"'"
								+ objects[Integer.parseInt(ele
										.elementText("sequence")) - 1] + "'");
			}
		}

		return provaRule;
	}

	public static boolean validatePrecondition(Object taskID, Object serviceName) {
		ProvaList userInputs;
		AbstractTask task = TaskManagementCenter.taskMap.get(taskID);
		if (task.isModified()) {
			userInputs = task.getNewInlist();
		} else {
			userInputs = task.getInList();
		}
		String provaRule = "";
		// return true if there is no precondition exists
		if (provaRule == "")
			return true;
		return validateSWRLExpressionTranslate(provaRule, userInputs, null);

	}

	/**
	 * Validate precondition of a task with Prova agent and knowledge base
	 * 
	 * @param provaRules
	 * @param list
	 * @param object
	 * @return
	 */
	private static synchronized boolean validateSWRLExpressionTranslate(
			String provaRule, ProvaList list, Object resultList) {
		boolean validate = false;

		BufferedReader in = new BufferedReader(new StringReader(provaRule));
		comm = new ProvaCommunicatorImpl(
				StringConstants.PROVA_KNOWLEDGE_BASE_AGENT_NAME, null,
				StringConstants.SWF_KB, ProvaCommunicatorImpl.SYNC);

		try {
			comm.consultSync(in, "0", new Object[] {});

			String goal = provaRule.substring(0, provaRule.indexOf(" :-"));
			String input1 = ":- solve(" + goal + ").";

			BufferedReader in1 = new BufferedReader(new StringReader(input1));

			List<ProvaSolution[]> resultSets = comm.consultSync(in1, "0",
					new Object[] {});
			if (resultSets.get(0).length == 0) {
				comm.unconsultSync("0");
				comm.shutdown();
				logger.error("The following rule is unproved: \n'" + provaRule);
				return false;
			} else {
				validate = true;
				comm.unconsultSync("0");
				logger.info("The following rule is proved: \n'" + provaRule);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		comm.shutdown();

		return validate;
	}

	/**
	 * @param number
	 * @return Return a random number to select the available agent and service
	 *         randomly
	 */
	public static int getRandomNumber(Integer number) {
		int num = number.intValue();
		return (int) (Math.random() * (num));
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

	/**
	 * Construct the result from Prova agent
	 * 
	 * @param operation
	 * @param inList
	 * @param outList
	 * @param paraListResult
	 * @return
	 */
	public static ProvaList prepareProvaResult(ProvaList inputList,
			Object taskID, ProvaList paraListResult) {
		ProvaObject taskIDObject = ProvaConstantImpl.create(taskID);

		ProvaList inList, outList;
		AbstractTask task = TaskManagementCenter.taskMap.get(taskID);

		inList = task.getInList();
		outList = task.getOutList();
		ProvaObject[] inObjects = new ProvaObject[inList.getFixed().length + 1];
		inObjects[0] = ProvaConstantImpl.create("inArgs");
		for (int i = 1; i < inObjects.length; i++)
			inObjects[i] = inList.getFixed()[i - 1];

		List outObjectsList = identifyOutputs(inputList, paraListResult);
		ProvaObject[] outObjects = new ProvaObject[outObjectsList.size() + 1];
		outObjects[0] = ProvaConstantImpl.create("outArgs");
		for (int i = 1; i < outObjects.length; i++)
			outObjects[i] = (ProvaObject) outObjectsList.get(i - 1);
		ProvaList list = ProvaListImpl.create(new ProvaObject[] { taskIDObject,
				ProvaListImpl.create(inObjects),
				ProvaListImpl.create(outObjects) });
		return list;
	}

	/**
	 * @param inputList
	 * @param paraListResult
	 * @return
	 */
	private static List identifyOutputs(ProvaList inputList,
			ProvaList resultList) {
		// TODO Auto-generated method stub
		List list = new ArrayList();
		for (int i = 0; i < inputList.getFixed().length; i++) {
			Object obj = inputList.getFixed()[i];
			if (obj instanceof ProvaVariablePtrImpl) {
				list.add(resultList.getFixed()[i]);
			}
			if (obj instanceof ProvaList) {
				List l1 = identifyOutputs((ProvaList) inputList.getFixed()[i],
						(ProvaList) resultList.getFixed()[i]);
				for (int j = 0; j < l1.size(); j++)
					list.add(l1.get(i));
			}
		}
		return list;
	}

	public static ProvaList prepareProvaInput(Object interfaceName,
			Object taskID) {
		String provaInterface = interfaceName.toString();
		AbstractTask task = TaskManagementCenter.taskMap.get(taskID);

		ProvaList inList, outList;
		inList = task.getInList();
		outList = task.getOutList();

		int inputs = inList.getFixed().length;
		for (int i = 0; i < inputs; i++) {
			provaInterface = provaInterface.replaceFirst("\\+",
					inList.getFixed()[i].toString());
		}

		int outputs = outList.getFixed().length;

		for (int i = 0; i < outputs; i++) {
			provaInterface = provaInterface.replaceFirst("\"-\"", "X" + i);
		}

		ProvaList list = parse(provaInterface);
		return list;
	}

	private static ProvaList parse(String t) {
		ProvaKnowledgeBase kb = new ProvaKnowledgeBaseImpl();
		ProvaResultSet resultSet = new ProvaResultSetImpl();
		StringReader sr = new StringReader("interface(" + t
				+ "):- println([\"\"]).\n");
		BufferedReader in = new BufferedReader(sr);

		ProvaParserImpl parser = new ProvaParserImpl("inline1", new Object[] {});
		try {
			List<ProvaRule> rules = parser.parse(kb, resultSet, in);
			// Run each goal
			for (ProvaRule rule : rules) {
				if (rule.getHead() != null)
					return (ProvaList) rule.getHead().getTerms().getFixed()[0];
			}
		} catch (ProvaParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static ProvaList prepareMessage4HumanAgent(Object taskID) {
		ProvaList inList, outList;
		AbstractTask task = TaskManagementCenter.taskMap.get(taskID);
		if (task.isModified()) {
			inList = task.getNewInlist();
			outList = task.getNewOutList();
		} else {
			inList = task.getInList();
			outList = task.getOutList();
		}

		ProvaObject taskNameObject = ProvaConstantImpl.create(task
				.getTaskName());

		ProvaObject taskIDObject = ProvaConstantImpl.create(taskID);

		ProvaObject[] inObjects = new ProvaObject[inList.getFixed().length + 1];
		inObjects[0] = ProvaConstantImpl.create("inArgs");
		for (int i = 1; i < inObjects.length; i++)
			inObjects[i] = inList.getFixed()[i - 1];

		ProvaObject[] outObjects = new ProvaObject[outList.getFixed().length + 1];
		outObjects[0] = ProvaConstantImpl.create("outArgs");
		for (int i = 1; i < outObjects.length; i++)
			outObjects[i] = ProvaVariableImpl
					.create("X"
							+ ((ProvaVariablePtr) outList.getFixed()[i - 1])
									.getIndex());

		ProvaList list = ProvaListImpl.create(new ProvaObject[] {
				taskNameObject, taskIDObject, ProvaListImpl.create(inObjects),
				ProvaListImpl.create(outObjects) });

		return list;
	}

}
