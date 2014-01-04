/**
 * @className ProvaListUtil.java
 * 
 * Copyright (c) 2010-2014 Corporate Semantic Web.
 * Königin-Luise-Straße 24 - 26 14195 Berlin, Free University Berlin, Germany
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fub.csw.util;

import java.util.ArrayList;

import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;

/**
 * class description
 * 
 * @version 0.1
 * 
 * @date 2012-4-8
 * 
 * @author Zhili Zhao
 */

public class ProvaListUtil {
	public static ProvaList arrayList2ProvaList(ArrayList list) {
		ProvaObject[] constants = new ProvaObject[list.size()];
		for (int i = 0; i < list.size(); i++) {
			if(list.get(i) instanceof ProvaList)
				constants[i] = (ProvaList)list.get(i);
			if(list.get(i) instanceof ProvaConstant)
			constants[i] = ProvaConstantImpl.wrap(list.get(i));
		}
		ProvaList list1 = ProvaListImpl.create(constants);
		return list1;
	}

}
