/**
 * 
 */
package ws.prova.mule.impl;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;
import ws.prova.reference2.ProvaVariableImpl;

/**
 * @author never86
 *
 */
public class RuleMLIDL2ProvaList  extends AbstractTransformer {


	private ProvaObject parseContent(String payload) {
		payload= payload.replace("%20", " ");
		List list = new ArrayList();
		try {
			Document doc = DocumentHelper.parseText(payload);
			Element root = doc.getRootElement();
			List elems = root.elements();
			for(int i=0; i<elems.size();i++){
				Element ele = (Element) elems.get(i);
				if(ele.getName().equalsIgnoreCase("label")) continue;
				if(ele.getName().equalsIgnoreCase("Fun"))
					list.add(ProvaConstantImpl.create(ele.getTextTrim()));
				if(ele.getName().equalsIgnoreCase("Var"))
					list.add(createProvaVar(ele.attributeValue("mode"), ele.getTextTrim()));
				if(ele.getName().equalsIgnoreCase("Expr"))
					list.add(parseExpr(ele));
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  ProvaListImpl.create(list);
	}

	private ProvaObject createProvaVar(String attributeValue, String textTrim) {
		if(attributeValue.equalsIgnoreCase("-"))
			return ProvaVariableImpl.create(textTrim);
		else
			return ProvaConstantImpl.create(textTrim);
		
	}

	private ProvaList parseExpr(Element exprElem) {
		List list = new ArrayList();
		List elems = exprElem.elements();
		for(int i=0; i<elems.size();i++){
			Element ele = (Element) elems.get(i);
			if(ele.getName().equalsIgnoreCase("Fun"))
				list.add(ProvaConstantImpl.create(ele.getTextTrim()));
			if(ele.getName().equalsIgnoreCase("Var"))
				list.add(createProvaVar(ele.attributeValue("mode"), ele.getTextTrim()));
			if(ele.getName().equalsIgnoreCase("Expr")){
				list.add(parseExpr(ele));
			}
		}
		
		return  ProvaListImpl.create(list);
	}

	@Override
	protected Object doTransform(Object payload, String enc)
			throws TransformerException {
		ProvaList list = ProvaListImpl.create(new ProvaObject[] {
				ProvaConstantImpl.create("semantic_SWF_Engine"),
				ProvaConstantImpl.create("esb"),
				ProvaConstantImpl.create("User"),
				ProvaConstantImpl.create("query-sync"),
				parseContent(payload.toString())});
		return list;
	}
	
}
