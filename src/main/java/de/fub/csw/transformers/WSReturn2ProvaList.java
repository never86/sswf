package de.fub.csw.transformers;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaList;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;

public class WSReturn2ProvaList extends AbstractTransformer{

	@Override
	protected Object doTransform(Object src, String enc)
			throws TransformerException {
		// TODO Auto-generated method stub
		
		String res = src.toString();
		
		ProvaConstant prefixObj = ProvaConstantImpl.create("outArgs");
		ProvaConstant resObj = ProvaConstantImpl.create(res);
		List objs = new ArrayList();
		objs.add(prefixObj);
		objs.add(resObj);
		ProvaList outputList = ProvaListImpl.create(objs);
		
		ProvaList2ObjectArrays.inputProvaList.getFixed()[3] = ProvaConstantImpl.create("answer");
		ProvaList2ObjectArrays.inputProvaList.getFixed()[4] = outputList;
		
		System.out.println(ProvaList2ObjectArrays.inputProvaList+"SSSSSSSSSSSSSSSSSS:");
		return ProvaList2ObjectArrays.inputProvaList;
	}

}
