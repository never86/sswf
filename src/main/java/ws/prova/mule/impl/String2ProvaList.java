package ws.prova.mule.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.kernel2.ProvaVariable;
import ws.prova.reference2.ProvaConstantImpl;
import ws.prova.reference2.ProvaListImpl;
import ws.prova.reference2.ProvaVariableImpl;

public class String2ProvaList extends AbstractTransformer {

	// public ProvaKnowledgeBase kb = new ProvaKnowledgeBaseImpl();
	private String regex = "\\.?@\\d+=";
	private LinkedList ll = new LinkedList();

	public static void main(String[] args) {
		String b = "[semantic_SWF_Engine, esb, User, query-sync, [workflow,2.3,7.7,7.7]]";
		String a = "RuleML-2010,esb,User,query-sync,[getContact,ruleml2010_GeneralChair,update,Contact]";
		String temp = "semantic_SWF_Engine,esb,User,query-sync,[wcp01_Sequence,[inArgs,2.3,7.7,3.4],[outArgs,Output]]";
		String temp1 = "semantic_SWF_Engine,esb,User,query-sync,[wcp04_ExclusiveChoice,[inArgs,2.3,7.7],[outArgs,Output]]";
		String temp2 = "httpEndpoint:1,esb,httpEndpoint,query,[proteinPredicitonAnalysisProcess,[inArgs,Q9VAN0,GO:0006564],[outArgs,Result]]";
		
		ProvaList list = new String2ProvaList().parseContent(temp2);
		try {
			System.out.println(new Prova2RuleMLTranslator().transform(list));
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String removeQutationMark(String string) {
		// TODO Auto-generated method stub
		if (string.startsWith("\""))
			string = string.substring(1);
		if (string.endsWith("\""))
			string = string.substring(0, string.length() - 1);
		return string;
	}

	private String removeComma(String content) {
		// TODO Auto-generated method stub
		if (content.startsWith(","))
			content = content.substring(1);
		if (content.endsWith(","))
			content = content.substring(0, content.length() - 1);
		return content;
	}

	private ProvaList parseContent(String content) {
		List list = new ArrayList();
		// TODO Auto-generated method stub
		if (content.startsWith("["))
			content = content.substring(1, content.length() - 1);
		if (content.indexOf("=") != -1) {
			if (content.contains(System.getProperty("line.separator")))
				content = content.replaceAll(
						System.getProperty("line.separator"), "");

			content = content.substring(0, content.length() - 1);
			Pattern pattern = Pattern.compile(regex);
			Matcher m = pattern.matcher(content);
			while (m.find()) {
				content = m.replaceAll(",");
				content = "substitutions" + content;
			}
		}
		int tag = 0;
		if (content.indexOf("[") == -1 || content.indexOf("]") == -1) {
			parseSimpleContent(list, content);
		} else
			for (int i = 0; i < content.length(); i++) {
				if (content.charAt(i) == '['
						|| (i == content.length() - 1 && content.charAt(i) != ']')) {
					if ((i - tag) != 0 && ll.isEmpty()) {
						String temp = "";
						if (i == content.length() - 1)
							temp = content.substring(tag, i + 1);
						else
							temp = content.substring(tag, i);
						if (!temp.equalsIgnoreCase(",")) {
							parseSimpleContent(list, temp);
						}
					}
					ll.addFirst(i);
					tag = i;
				} else if (!ll.isEmpty() && content.charAt(i) == ']') {
					int pos = (Integer) ll.removeFirst();
					if (ll.isEmpty())
						list.add(parseContent(content.substring(pos, i + 1)));
					tag = i + 1;
				}

			}
		return ProvaListImpl.create(list);
	}

	private void parseSimpleContent(List list, String content) {
		// TODO Auto-generated method stub
		content = removeQutationMark(content);
		content = removeComma(content);
		if (content.trim().equals(""))
			return;
		String[] objs = content.split(",");
		for (int j = 0; j < objs.length; j++) {
			String temp = objs[j].trim();
			if (temp.equals(""))
				return;
			char firstChar = temp.charAt(0);
			//the items of the content are constants, since the content is a workflow result
			list.add(constructProvaConstant(removeQutationMark(objs[j])));
		}
	}

	/**
	 * @param string
	 * @return
	 */
	private ProvaConstant constructProvaConstant(String constantStr) {
		int tag = constantStr.indexOf(":");
		if (tag != -1) {
			String type = constantStr.substring(0, tag);
			Object javaObject = JavaTypeGenerator.generateJavaTypeObject(type,
					constantStr.substring(tag + 1));
			if (javaObject != null)
				return ProvaConstantImpl.create(javaObject);
			else
				return ProvaConstantImpl.create(constantStr);
		} else
			return ProvaConstantImpl.create(constantStr);
	}

	/**
	 * @param string
	 * @return
	 */
	private ProvaVariable constructProvaVariable(String obj) {
		int tag = obj.indexOf(":");
		if (tag != -1) {
			String type = obj.substring(0, tag);
			if (JavaTypeGenerator.getJavaType(type) != null)
				return ProvaVariableImpl.create(obj.substring(tag) + 1,
						JavaTypeGenerator.getJavaType(type));
			else
				return ProvaVariableImpl.create(obj);
		} else
			return ProvaVariableImpl.create(obj);
	}

	private boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr == 46)
				continue;
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	@Override
	protected Object doTransform(Object src, String enc)
			throws TransformerException {
		String output = src.toString();
		if (output.startsWith("["))
			output = output.substring(1, output.length() - 1);

		String[] items = output.split(",");
		int i = 0;
		if (items[0].indexOf("id") != -1)
			i = 1;
		String id = items[i].substring(0, items[i].length() - i);

		ProvaList list = ProvaListImpl.create(new ProvaObject[] {
				ProvaConstantImpl.create(id),
				ProvaConstantImpl.create(removeQutationMark(items[i + 1])),
				ProvaConstantImpl.create(removeQutationMark(items[i + 2])),
				ProvaConstantImpl.create(removeQutationMark(items[i + 3])),
				parseContent(output.substring(output.indexOf(items[i + 3])
						+ items[i + 3].length() + 1, output.length())) });

		return list;
	}
}
