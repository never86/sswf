/**
 * @className DynamicManagement.java
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaListImpl;

/**
 * class description
 * 
 * @version 0.1
 * 
 * @date 2012-3-22
 * 
 * @author Zhili Zhao
 */

public class TaskManagementCenter {

	public static Map<String, AbstractTask> taskMap = new HashMap<String, AbstractTask>();

//	public static Map<String, Map> taskAgents = new HashMap<String, Map>();
	
//	public static Map<String, Boolean> agents = new HashMap<String, Boolean>();

//	public static Map<String, Map> taskServices = new HashMap<String, Map>();

//	public static Map<String, Map> equivalentTasks = new HashMap<String, Map>();

	public static Map<String, List> workflowMap = new HashMap<String, List>();

	// public static void setTaskModified(Object taskID, ProvaList newInputs,
	// ProvaList newOutputs) {
	// ProvaList oldInputs, oldOutList;
	// AbstractTask task = DynamicManagement.taskMap.get(taskID);
	// if (task.isModified()) {
	// oldInputs = task.getNewInlist();
	// oldOutList = task.getNewOutList();
	// } else {
	// oldInputs = task.getInList();
	// oldOutList = task.getOutList();
	// }
	//
	// if (oldInputs.getFixed().length == newInputs.getFixed().length) {
	// int i = 0;
	// for (i = 0; i < newInputs.getFixed().length; i++) {
	// Object obj1 = ((ProvaConstant) oldInputs.getFixed()[i])
	// .getObject();
	// Object obj2 = ((ProvaConstant) newInputs.getFixed()[i])
	// .getObject();
	// if (obj1.equals(obj2))// compare value
	// continue;
	// else
	// break;
	// }
	// if (i != newInputs.getFixed().length){
	// task.setModified(true);
	// task.setNewInlist(newInputs);
	// task.setNewOutList(newOutputs);
	// }
	//
	// }
	// }
	
//	public static String findOptimalAgent(ArrayList candidateAgents){
//		for (int i = 0; i < candidateAgents.size(); i++) {
//			if (!agents.containsKey(candidateAgents.get(i).toString()))
//				return candidateAgents.get(i).toString();
//		}
//		return "none";
//	}
//	
//	public static void registerUnavailableAgent(Object agent){
//		if(!agents.containsKey(agent.toString()))
//		agents.put(agent.toString(), false);
//	}
//	
//	public static void removeUnavailableAgent(Object agent){
//		if(agents.containsKey(agent.toString()))
//		   agents.remove(agent.toString());
//	}

	public static void registerWorkflow(Object cid, Object workflowName) {
		if (!workflowMap.containsKey(cid.toString())) {
			List list = new ArrayList();
			list.add(workflowName);
			workflowMap.put(cid.toString(), list);
		} else {
			List workflows = workflowMap.get(cid.toString());
			workflows.add(workflowName);
		}
	}
	
	public static String getWfName(Object cid){
		if (!workflowMap.containsKey(cid.toString())) 
			return null;
		else {
			List workflows = workflowMap.get(cid.toString());
			return (String) workflows.get(0);
		}
	}

	public static void cleanWorkflow(Object cid, Object workflowName) {
		if (workflowMap.containsKey(cid.toString())) {
			List workflows = workflowMap.get(cid.toString());
			workflows.add(workflowName);
		}
	}

	public static void registerTask(Object taskID, Object taskName,
			ProvaList inList, ProvaList outList) {
		if (!taskMap.containsKey(taskID.toString())) {
			AbstractTask task = new AbstractTask();
			task.setInList(removeFuncationName(inList));
			task.setOutList(removeFuncationName(outList));

			task.setTaskID(taskID.toString());
			task.setTaskName(taskName.toString());
			task.setModified(false);

			taskMap.put(taskID.toString(), task);
		}
	}

	private static ProvaList removeFuncationName(ProvaList inList) {
		ProvaObject[] objects = inList.getFixed();
		ProvaObject[] objects1 = new ProvaObject[objects.length - 1];
		for (int i = 1; i < objects.length; i++) {
			objects1[i - 1] = objects[i];
		}

		return ProvaListImpl.create(objects1);
	}

//	public static void registerAgentsOfTask(Object taskID, ArrayList agents) {
//
//		if (taskAgents.containsKey(taskID))
//			taskAgents.remove(taskID);
//
//		Map map = new HashMap<Object, String>();
//		for (int i = 0; i < agents.size(); i++) {
//			map.put(agents.get(i), "1");
//		}
//
//		taskAgents.put(taskID.toString(), map);
//	}
//
//	public static void registerServicesOfTask(Object taskID, ArrayList services) {
//		if (taskServices.containsKey(taskID))
//			taskServices.remove(taskID);
//
//		Map map = new HashMap<Object, String>();
//		for (int i = 0; i < services.size(); i++) {
//			map.put(services.get(i), "1");
//		}
//
//		taskServices.put(taskID.toString(), map);
//	}

//	public static String getAlternativeAgent(Object taskID, Object agent) {
//		Map map = taskAgents.get(taskID);
//		map.remove(agent);
//		map.put(agent, "0");
//
//		Iterator iter = map.keySet().iterator();
//		while (iter.hasNext()) {
//			String key = (String) iter.next();
//			if (!key.equalsIgnoreCase(agent.toString())
//					&& map.get(key).equals("1"))
//				return key;
//		}
//
//		return "unavailable";
//	}
//
//	public static synchronized String getAlternativeService(Object taskID,
//			Object service) {
//		Map map = taskServices.get(taskID);
//		map.remove(service);
//		map.put(service, "0");
//
//		Iterator iter = map.keySet().iterator();
//		while (iter.hasNext()) {
//			String key = (String) iter.next();
//			if (!key.equalsIgnoreCase(service.toString())
//					&& map.get(key).equals("1"))
//				return key;
//		}
//
//		return "unavailable";
//	}

//	public static void updateTagOFTaskService(Object taskID) {
//		Map map = taskServices.get(taskID);
//		Map newMap = new HashMap<Object, String>();
//		Iterator iter = map.keySet().iterator();
//		while (iter.hasNext()) {
//			String key = (String) iter.next();
//			newMap.put(key, "1");
//		}
//
//		taskServices.remove(taskID);
//		taskServices.put(taskID.toString(), newMap);
//	}

//	public static void clear(Object taskID) {
//		taskAgents.remove(taskID);
//		taskServices.remove(taskID);
//	}
}
