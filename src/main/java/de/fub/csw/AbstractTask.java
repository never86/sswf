/**
 * @className AbstractTask.java
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

import ws.prova.kernel2.ProvaList;

/**
 * class description 
 * 
 * @version 0.1 
 *  
 * @date 2012-4-5
 * 
 * @author Zhili Zhao
 */

public class AbstractTask {
	
	private String taskName;
	private String taskID;
	private boolean isModified;
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskID() {
		return taskID;
	}
	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}
	public boolean isModified() {
		return isModified;
	}
	public void setModified(boolean isModified) {
		this.isModified = isModified;
	}
	public ProvaList getInList() {
		return inList;
	}
	public void setInList(ProvaList inList) {
		this.inList = inList;
	}
	public ProvaList getOutList() {
		return outList;
	}
	public void setOutList(ProvaList outList) {
		this.outList = outList;
	}
	public ProvaList getNewInlist() {
		return newInlist;
	}
	public void setNewInlist(ProvaList newInlist) {
		this.newInlist = newInlist;
	}
	public ProvaList getNewOutList() {
		return newOutList;
	}
	public void setNewOutList(ProvaList newOutList) {
		this.newOutList = newOutList;
	}
	private ProvaList inList, outList, newInlist, newOutList;

}
