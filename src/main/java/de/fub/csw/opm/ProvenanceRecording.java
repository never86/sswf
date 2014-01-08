/**
 * @className ProvenanceRecording.java
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

import java.util.Date;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import org.openprovenance.model.EmbeddedAnnotation;
import org.openprovenance.model.Process;

import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import de.fub.csw.AbstractTask;
import de.fub.csw.TaskManagementCenter;
import de.fub.csw.constant.ProvenanceConstants;

/**
 * class description
 * 
 * @version 0.1
 * 
 * @date 2012-3-30
 * 
 * @author Zhili Zhao
 */

public class ProvenanceRecording {

	public static Map<String, IdentityHashMap<Object, String>> dataMap = new HashMap<String, IdentityHashMap<Object, String>>();

	/**
	 * Record provenance information after service is compelted
	 * 
	 * @param cid
	 *            : conversation id
	 * @param taskID
	 *            : service id
	 * @param output
	 *            : contains taskName, inputs and outputs
	 * @param agentName
	 *            : the agent which is responsible the service
	 */
	public static void taskCompleted(Object cid, Object taskID,
			Object service, Object serviceType, Object address,
			Object startTime, ProvaList output, Object agentName) {
try{
		ProvaObject[] objects = output.getFixed();

		AbstractTask task = TaskManagementCenter.taskMap.get(taskID);

		// create a task execution process
		Process p = OpenProvenanceModel.createProcess(cid, taskID,
				task.getTaskName() + "(" + taskID + ")");

		EmbeddedAnnotation ann1 = OpenProvenanceModel.oFactory
				.newEmbeddedAnnotation("a1_" + taskID, "service id", service,
						OpenProvenanceModel.accountsMap.get(cid.toString()));

		EmbeddedAnnotation ann2 = OpenProvenanceModel.oFactory
				.newEmbeddedAnnotation("a2_" + taskID, "type", serviceType,
						OpenProvenanceModel.accountsMap.get(cid.toString()));

		if (address.toString().indexOf("/") != -1)
			address = address.toString().replaceAll("/", "\\\\\\\\");

		EmbeddedAnnotation ann3 = OpenProvenanceModel.oFactory
				.newEmbeddedAnnotation("a3_" + taskID, "address", address,
						OpenProvenanceModel.accountsMap.get(cid.toString()));

		EmbeddedAnnotation ann4 = OpenProvenanceModel.oFactory
				.newEmbeddedAnnotation("a4_" + taskID, "started at", startTime,
						OpenProvenanceModel.accountsMap.get(cid.toString()));

		EmbeddedAnnotation ann5 = OpenProvenanceModel.oFactory
				.newEmbeddedAnnotation("a5_" + taskID, "completed at",
						new Date().toLocaleString(),
						OpenProvenanceModel.accountsMap.get(cid.toString()));

		OpenProvenanceModel.oFactory.addAnnotation(p, ann1);
		OpenProvenanceModel.oFactory.addAnnotation(p, ann2);
		OpenProvenanceModel.oFactory.addAnnotation(p, ann3);
		OpenProvenanceModel.oFactory.addAnnotation(p, ann4);
		OpenProvenanceModel.oFactory.addAnnotation(p, ann5);

		// create a agent which controls the process and their dependency
		OpenProvenanceModel.createAgent(cid, agentName, agentName);
		OpenProvenanceModel.createWasControlledBy(cid, taskID, agentName,
				"wasControlledBy");

		// retrieve the inputs of the task and build their dependencies with the
		// task process
		ProvaObject[] inObjects = ((ProvaList) objects[1]).getFixed();

		for (int i = 0; i < inObjects.length - 1; i++) {
			String taid = taskID + "_i" + i;
			if (OpenProvenanceModel.assignObjects.containsKey(taid)) {
				String aid = OpenProvenanceModel.assignObjects.get(taid);
				OpenProvenanceModel.createUsed(cid, taskID, aid, "hasInput_"
						+ i);
			} else {
				OpenProvenanceModel.createArtifact(cid, taid,
						inObjects[i + 1].toString());
				OpenProvenanceModel.createUsed(cid, taskID, taid, "hasInput_"
						+ i);
			}
		}

		// retrieve the outputs of the task and build their dependencies with
		// the
		// task process
		ProvaObject[] outObjects = ((ProvaList) objects[2]).getFixed();

		for (int i = 0; i < outObjects.length - 1; i++) {
			Object obj = ((ProvaConstant) outObjects[i + 1]).getObject();
			// String taid = taskID + "_o" + i;
			// if (OpenProvenanceModel.assignObjects.containsKey(taid)) {
			// String aid = OpenProvenanceModel.assignObjects.get(taid);
			// OpenProvenanceModel.createWasGeneratedBy(cid, aid, taskName,
			// "wasGeneratedBy_" + i);
			// } else
			{
				OpenProvenanceModel.createArtifact(cid, taskID + "_o" + i,
						outObjects[i + 1].toString());
				OpenProvenanceModel.createWasGeneratedBy(cid,
						taskID + "_o" + i, taskID, "wasGeneratedBy_" + i);
			}
		}
}catch(Exception e){
	e.printStackTrace();
}
	}
	

	/**
	 * @param cid
	 *            : the conversation id of each workflow instance
	 * @param inputs
	 *            : the user inputs of a workflow
	 */
	public static void workflowStarted(Object cid, Object workflowName,
			ProvaList inputs) {
		// create a dataflow account for whole workflow
		String startProcessID = ProvenanceConstants.WORKFLOW_STARTED_PROCESS_ID
				+ "_" + workflowName;
		OpenProvenanceModel.createAccount(cid,
				ProvenanceConstants.DATAFLOW_ACCOUNT);
		// create the started process of workflow
		Process p = OpenProvenanceModel.createProcess(cid, startProcessID,
				ProvenanceConstants.WORKFLOW_STARTED_PROCESS_NAME + " ("
						+ workflowName + ")");
		EmbeddedAnnotation ann1 = OpenProvenanceModel.oFactory
				.newEmbeddedAnnotation("a1_" + cid, "started at",
						new Date().toLocaleString(),
						OpenProvenanceModel.accountsMap.get(cid.toString()));
		OpenProvenanceModel.oFactory.addAnnotation(p, ann1);
		// create the agent which controls the started process
		OpenProvenanceModel.createAgent(cid,
				ProvenanceConstants.WORKFLOW_ENGINE_ID,
				ProvenanceConstants.WORKFLOW_ENGINE_NAME);
		// create the dependency between the agent and the process
		OpenProvenanceModel.createWasControlledBy(cid, startProcessID,
				ProvenanceConstants.WORKFLOW_ENGINE_ID, "wasControlledBy");

		IdentityHashMap<Object, String> map = null;
		if (dataMap.containsKey(cid.toString()))
			map = dataMap.get(cid.toString());
		else
			map = new IdentityHashMap<Object, String>();
		// create the dependency between workflow inputs(artifacts) and the
		// workflow started process
		ProvaObject[] inObjects = inputs.getFixed();
		for (int i = 1; i < inObjects.length; i++) {
			Object obj = ((ProvaConstant) inObjects[i]).getObject();
			if (!map.containsKey(obj)) {
				OpenProvenanceModel.createArtifact(cid, startProcessID + "_i"
						+ (i - 1), inObjects[i].toString());
				OpenProvenanceModel.createUsed(cid, startProcessID,
						startProcessID + "_i" + (i - 1), "workflow_hasInput_"
								+ (i - 1));
				map.put(obj, startProcessID + "_i" + (i - 1));
			} else {
				OpenProvenanceModel.createUsed(cid, startProcessID,
						map.get(obj) , "workflow_hasInput_"
								+ (i - 1));
			}
		}

		if (!dataMap.containsKey(cid.toString()))
			dataMap.put(cid.toString(), map);
	}

	/**
	 * Record the provenance information after workflow is completed
	 * 
	 * @param cid
	 *            : conversation id
	 * @param outputs
	 *            : workflow output list
	 */
	public static void workflowCompleted(Object cid, Object workflowName,
			ProvaList outputs) {
		// create the completed process of workflow
		String completedProcessID = ProvenanceConstants.WORKFLOW_COMPLETED_PROCESS_ID
				+ "_" + workflowName;
		Process p = OpenProvenanceModel.createProcess(cid, completedProcessID,
				ProvenanceConstants.WORKFLOW_COMPLETED_PROCESS_NAME + " ("
						+ workflowName + ")");
		EmbeddedAnnotation ann1 = OpenProvenanceModel.oFactory
				.newEmbeddedAnnotation("a1_" + cid, "completed at",
						new Date().toLocaleString(),
						OpenProvenanceModel.accountsMap.get(cid.toString()));
		OpenProvenanceModel.oFactory.addAnnotation(p, ann1);
		// create the agent which controls the completed process
		OpenProvenanceModel.createAgent(cid,
				ProvenanceConstants.WORKFLOW_ENGINE_ID,
				ProvenanceConstants.WORKFLOW_ENGINE_NAME);
		// create the dependency between the agent and the process
		OpenProvenanceModel.createWasControlledBy(cid, completedProcessID,
				ProvenanceConstants.WORKFLOW_ENGINE_ID, "wasControlledBy");
		recordTaskInput(cid, completedProcessID, outputs);
		// create the dependency between workflow outputs(artifacts) and the
		// workflow completed process
		ProvaObject[] outObjects = outputs.getFixed();

		for (int i = 0; i < outObjects.length; i++) {
			String taid = completedProcessID + "_i" + i;
			// it is not necessary to create the explicit artifacts for workflow
			// output if it has the dependency with exist artifacts
			if (OpenProvenanceModel.assignObjects.containsKey(taid)) {
				String aid = OpenProvenanceModel.assignObjects.get(taid);
				OpenProvenanceModel.createUsed(cid, completedProcessID, aid,
						"usedAsWorkflowOutput_" + i);
			}
		}

	}

	/**
	 * Record the provenance information when data assignment happens, there is
	 * a Map object which records these object pairs to avoid redundancy
	 * artifacts
	 * 
	 */

	public static void recordTaskInput(Object cid, Object processID,
			ProvaList inputs) {
		Map map = dataMap.get(cid);

		ProvaObject[] objects = inputs.getFixed();
		for (int i = 1; i < objects.length; i++) {
			Object obj = ((ProvaConstant) objects[i]).getObject();
			if (map.containsKey(obj)) {
				OpenProvenanceModel.assignObjects.put(processID.toString()
						+ "_i" + (i - 1), map.get(obj).toString());
			} else {
				map.put(obj, processID.toString() + "_i" + (i - 1));
			}
		}
	}

	/**
	 * Record the provenance information after a task process is modified by
	 * human
	 * 
	 * @param cid
	 * @param taskName
	 * @param tid
	 * @param oldInputs
	 * @param newInputs
	 */
	public static void humanModified(Object cid, Object tid) {
		// return if oldInputs and newInputs are identical
		AbstractTask task = TaskManagementCenter.taskMap.get(tid);
		if (!task.isModified())
			return;

		// create an exception handling process
		OpenProvenanceModel.createProcess(cid,
				ProvenanceConstants.EXCEPTION_PROCESS_ID + "_" + tid,
				ProvenanceConstants.EXCEPTION_PROCESS_NAME);

		// create a task process
		OpenProvenanceModel.createProcess(cid, tid, task.getTaskName());

		// exception handling process is triggered by task process
		OpenProvenanceModel.createWasTriggeredBy(cid,
				ProvenanceConstants.EXCEPTION_PROCESS_ID + "_" + tid, tid);

		ProvaObject[] oldObjects = task.getInList().getFixed();
		ProvaObject[] newObjects = task.getNewInlist().getFixed();
		for (int i = 0; i < newObjects.length; i++) {
			Object obj1 = ((ProvaConstant) newObjects[i]).getObject();
			Object obj2 = ((ProvaConstant) oldObjects[i]).getObject();
			// create a human steering process if there is any parameter is
			// different
			if (!obj1.equals(obj2)) {
				String aid = "hs_o" + i;
				String oaid = tid + "_i" + i;
				OpenProvenanceModel.createProcess(cid,
						ProvenanceConstants.HUMAN_STEERING_PROCESS_ID + "_"
								+ tid,
						ProvenanceConstants.HUMAN_STEERING_PROCESS_NAME);
				// create an artifact to present the new parameter
				OpenProvenanceModel.createArtifact(cid,
						ProvenanceConstants.HUMAN_STEERING_PROCESS_ID + "_o"
								+ i, newObjects[i].toString());
				// create the dependency between the artifact the human steering
				// process
				OpenProvenanceModel.createWasGeneratedBy(cid, aid,
						ProvenanceConstants.HUMAN_STEERING_PROCESS_ID + "_"
								+ tid, "wasGeneratedBy");
				// the old artifact should be assigned, human steering process
				// used the old artifact to generate its new artifact
				if (OpenProvenanceModel.assignObjects.containsKey(oaid)) {
					OpenProvenanceModel
							.createUsed(
									cid,
									ProvenanceConstants.HUMAN_STEERING_PROCESS_ID
											+ "_" + tid,
									OpenProvenanceModel.assignObjects.get(oaid),
									"used");
					OpenProvenanceModel.assignObjects.remove(oaid);
					OpenProvenanceModel.assignObjects.put(oaid, aid);
				}
			}
		}

		// human steering process is triggered by exception handling process
		OpenProvenanceModel.createWasTriggeredBy(cid,
				ProvenanceConstants.HUMAN_STEERING_PROCESS_ID + "_" + tid,
				ProvenanceConstants.EXCEPTION_PROCESS_ID + "_" + tid);

	}

	public static void recordTaskOutput(Object cid, Object taskID,
			ProvaList output) {
		ProvaObject[] outObjects = output.getFixed();

		IdentityHashMap<Object, String> map = dataMap.get(cid.toString());
		for (int i = 0; i < outObjects.length; i++) {
			Object obj = ((ProvaConstant) outObjects[i]).getObject();
			map.put(obj, taskID.toString() + "_o" + i);
		}
		dataMap.put(cid.toString(), map);
	}

}
