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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;

import ws.prova.api2.ProvaCommunicator;
import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;
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
	private static Map<String, org.apache.cxf.endpoint.Client> wsClients = new HashMap<String, org.apache.cxf.endpoint.Client>();

	private static ProvaCommunicator comm;

	static org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory dcf = null;

	/*
	 * convert the results collected by reaction groups (ArrayList) to workflow
	 * final results used in swf_engine.prova
	 */
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
			org.apache.cxf.endpoint.Client client = null;
			if (wsClients.containsKey(wsdlAddress.toString()))
				client = wsClients.get(wsdlAddress.toString());
			else
				client = dcf.createClient(wsdlAddress.toString());
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

}
