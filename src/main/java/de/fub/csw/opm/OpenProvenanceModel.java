/**
 * @className OpenProvenanceModel.java
 * 
 * Copyright (c) 2010-2014 Corporate Semantic Web.
 * Königin-Luise-Straße 24 - 26 14195 Berlin, Free University Berlin, Germany
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fub.csw.opm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.openprovenance.model.Account;
import org.openprovenance.model.Agent;
import org.openprovenance.model.Annotation;
import org.openprovenance.model.Artifact;
import org.openprovenance.model.OPMFactory;
import org.openprovenance.model.OPMGraph;
import org.openprovenance.model.OPMSerialiser;
import org.openprovenance.model.OPMToDot;
import org.openprovenance.model.Overlaps;
import org.openprovenance.model.Process;
import org.openprovenance.model.Used;
import org.openprovenance.model.WasControlledBy;
import org.openprovenance.model.WasGeneratedBy;
import org.openprovenance.model.WasTriggeredBy;

import de.fub.csw.TaskManagementCenter;
import de.fub.csw.constant.StringConstants;

/**
 * class description
 * 
 * @version 0.1
 * 
 * @date 2012-3-30
 * 
 * @author Zhili Zhao
 */

public class OpenProvenanceModel {
	public static OPMGraph graph;
	public static OPMFactory oFactory = new OPMFactory();
	public static Map<String, OPMGraph> graphMap = new HashMap<String, OPMGraph>();
	public static Map<String, List> elementsMap = new HashMap<String, List>();
	public static Map<String, Collection<Account>> accountsMap = new HashMap<String, Collection<Account>>();

	public static Map<String, String> assignObjects = new HashMap<String, String>();

	public static synchronized Artifact createArtifact(Object cid, Object aid,
			Object label) {
		if (getArtifact(cid.toString(), aid.toString()) == null) {
			Artifact artifact = oFactory.newArtifact(aid.toString(),
					accountsMap.get(cid.toString()), label.toString());
			putElementsMap(cid, artifact);
			return artifact;
		}
		return getArtifact(cid.toString(), aid.toString());
	}

	/**
	 * @param cid
	 * @param object
	 */
	private static synchronized void putElementsMap(Object cid, Object object) {
		if (elementsMap.containsKey(cid.toString())) {
			List list = elementsMap.get(cid.toString());
			list.add(object);
			elementsMap.remove(cid.toString());
			elementsMap.put(cid.toString(), list);
		} else {
			List list = new ArrayList();
			list.add(object);
			elementsMap.put(cid.toString(), list);
		}
	}

	public static synchronized Agent createAgent(Object cid, Object aid,
			Object label) {
		if (getAgent(cid.toString(), aid.toString()) == null) {
			Agent agent = oFactory.newAgent(aid.toString(),
					accountsMap.get(cid.toString()), label.toString());
			putElementsMap(cid, agent);
			return agent;
		}
		return getAgent(cid.toString(), aid.toString());
	}

	public static synchronized Process createProcess(Object cid, Object pid,
			Object label) {
		if (getProcess(cid.toString(), pid.toString()) == null) {
			Process process = oFactory.newProcess(pid.toString(),
					accountsMap.get(cid.toString()), label.toString());
			putElementsMap(cid, process);
			return process;
		}

		return getProcess(cid.toString(), pid.toString());
	}

	public static synchronized Used createUsed(Object cid, Object pid,
			Object aid, Object role) {
		Used used = oFactory.newUsed(
				getProcess(cid.toString(), pid.toString()),
				oFactory.newRole(role.toString()),
				getArtifact(cid.toString(), aid.toString()),
				accountsMap.get(cid.toString()));
		putElementsMap(cid, used);
		return used;
	}

	public static synchronized WasGeneratedBy createWasGeneratedBy(Object cid,
			Object aid, Object pid, Object role) {
		WasGeneratedBy wg = oFactory.newWasGeneratedBy(
				getArtifact(cid.toString(), aid.toString()),
				oFactory.newRole(role.toString()),
				getProcess(cid.toString(), pid.toString()),
				accountsMap.get(cid.toString()));
		putElementsMap(cid, wg);
		return wg;
	}

	public static synchronized WasControlledBy createWasControlledBy(
			Object cid, Object pid, Object aid, Object role) {
		WasControlledBy wc = oFactory.newWasControlledBy(
				getProcess(cid.toString(), pid.toString()),
				oFactory.newRole(role.toString()),
				getAgent(cid.toString(), aid.toString()),
				accountsMap.get(cid.toString()));
		putElementsMap(cid, wc);
		return wc;
	}

	public static synchronized WasTriggeredBy createWasTriggeredBy(Object cid,
			Object pid1, Object pid2) {
		WasTriggeredBy wt = oFactory.newWasTriggeredBy(
				getProcess(cid.toString(), pid1.toString()),
				getProcess(cid.toString(), pid2.toString()),
				accountsMap.get(cid.toString()));
		putElementsMap(cid, wt);
		return wt;
	}

	public static synchronized Artifact getArtifact(String cid, String aid) {
		if (!elementsMap.containsKey(cid))
			return null;
		else {
			Iterator iter = elementsMap.get(cid).iterator();
			while (iter.hasNext()) {
				Object object = (Object) iter.next();
				if (object instanceof Artifact) {
					Artifact a = (Artifact) object;
					if (a.getId().equalsIgnoreCase(aid))
						return a;
				}
			}
			return null;
		}
	}

	public static synchronized void createOPMGraph(Object cid,
			Object workflowName) {
		try{
		Map workflowMap = TaskManagementCenter.workflowMap;
		if (workflowMap.containsKey(cid)) {
			List list = (List) workflowMap.get(cid);
			if (workflowName.toString()
					.equalsIgnoreCase(list.get(0).toString())) {
				List<Process> processes = new ArrayList<Process>();
				List<Overlaps> overlaps = new ArrayList<Overlaps>();
				List<Artifact> artifacts = new ArrayList<Artifact>();
				List<Agent> agents = new ArrayList<Agent>();
				List<Object> objects = new ArrayList<Object>();
				List<Annotation> annotations = new ArrayList<Annotation>();

				if (elementsMap.containsKey(cid)) {
					Iterator iter = elementsMap.get(cid).iterator();
					while (iter.hasNext()) {
						Object object = iter.next();
						if (object instanceof Process)
							processes.add((Process) object);
						else if (object instanceof Overlaps)
							overlaps.add((Overlaps) object);
						else if (object instanceof Artifact)
							artifacts.add((Artifact) object);
						else if (object instanceof Agent)
							agents.add((Agent) object);
						else if (object instanceof Annotation)
							annotations.add((Annotation) object);
						else
							objects.add(object);
					}
				}

				Overlaps[] ols = null;
				if (overlaps.size() != 0)
					ols = (Overlaps[]) overlaps.toArray();
				Process[] ps = null;
				if (processes.size() != 0) {
					ps = new Process[processes.size()];
					for (int i = 0; i < processes.size(); i++)
						ps[i] = processes.get(i);

				}
				Artifact[] as = null;
				if (artifacts.size() != 0) {
					as = new Artifact[artifacts.size()];
					for (int i = 0; i < artifacts.size(); i++)
						as[i] = artifacts.get(i);
				}
				Agent[] ags = null;
				if (agents.size() != 0) {
					ags = new Agent[agents.size()];
					for (int i = 0; i < agents.size(); i++)
						ags[i] = agents.get(i);
				}
				Object[] os = null;
				if (objects.size() != 0) {
					os = new Object[objects.size()];
					for (int i = 0; i < objects.size(); i++)
						os[i] = objects.get(i);
				}
				Annotation[] annos = null;
				if (annotations.size() != 0) {
					annos = new Annotation[annotations.size()];
					for (int i = 0; i < annotations.size(); i++)
						annos[i] = annotations.get(i);
				}

				String graphName = cid + "_" + workflowName.toString();
				OPMGraph graph = oFactory.newOPMGraph(accountsMap.get(cid),
						ols, ps, as, ags, os, annos);
				if (!graphMap.containsKey(graphName))
					graphMap.put(graphName, graph);

				serialize(graphName);
			}
		}
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}

	public static synchronized Agent getAgent(String cid, String aid) {
		if (!elementsMap.containsKey(cid))
			return null;
		else {
			Iterator iter = elementsMap.get(cid).iterator();
			while (iter.hasNext()) {
				Object object = (Object) iter.next();
				if (object instanceof Agent) {
					Agent a = (Agent) object;
					if (a.getId().equalsIgnoreCase(aid))
						return a;
				}
			}
			return null;
		}
	}

	public static synchronized Process getProcess(String cid, String pid) {
		if (!elementsMap.containsKey(cid))
			return null;
		else {
			Iterator iter = elementsMap.get(cid).iterator();
			while (iter.hasNext()) {
				Object object = (Object) iter.next();
				if (object instanceof Process) {
					Process p = (Process) object;
					if (p.getId().equalsIgnoreCase(pid))
						return p;
				}
			}
			return null;
		}
	}

	public static synchronized Collection<Account> getAccount(String cid,
			String pid) {
		if (!accountsMap.containsKey(cid))
			return null;
		else {
			return accountsMap.get(cid.toString());
		}
	}

	public static Collection<Account> createAccount(Object cid,
			Object accountName) {
		Collection<Account> account = Collections.singleton(oFactory
				.newAccount(accountName.toString()));
		if (!accountsMap.containsKey(cid.toString()))
			accountsMap.put(cid.toString(), account);
		return account;
	}

	public static void serialize(Object graphID) {
		String workspace = StringConstants.appDir + File.separator+"logs"+File.separator;
		OPMGraph graph = getOPMGraph(graphID);
		String fileName = graphID.toString().replaceAll(":", "_");
		OPMSerialiser serial = OPMSerialiser.getThreadOPMSerialiser();
		StringWriter sw = new StringWriter();
		try {
			serial.serialiseOPMGraph(sw, graph, true);
			serial.serialiseOPMGraph(new File(
					workspace + fileName + ".xml"),
					graph, true);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		OPMToDot toDot = new OPMToDot(StringConstants.appDir+File.separator+"conf"+File.separator+"defaultConfig.xml"); // with roles
		try {
			toDot.convert(graph, workspace
					+ fileName + ".dot", workspace
					+ fileName + ".pdf");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param conversationID
	 * @return
	 */
	private static synchronized OPMGraph getOPMGraph(Object graphID) {
		// TODO Auto-generated method stub
		return (OPMGraph) graphMap.get(graphID);
	}
}
