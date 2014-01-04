package de.fub.csw.transformers;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import ws.prova.kernel2.ProvaList;

public class ProvaList2ObjectArrays extends AbstractTransformer{
	
	public static ProvaList inputProvaList;

	@Override
	protected Object doTransform(Object src, String enc)
			throws TransformerException {
		// TODO Auto-generated method stub
		inputProvaList = (ProvaList)src;
		double d1 = 2.3;
		double d2 = 4.1;
		Object[] objs = new Object[2];
		objs[0]=d1;
		objs[1]=d2;
		return objs;
	}

}
