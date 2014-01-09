/**
 * 
 */
package ws.prova.mule.impl;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;

/**
 * @author Zhili Zhao
 *
 */
public class ProvaList2HTML extends AbstractTransformer {
	
	private String req_content;
	
	public ProvaList2HTML(String req_content){
		this.req_content = req_content;
	}

	/* (non-Javadoc)
	 * @see org.mule.transformer.AbstractTransformer#doTransform(java.lang.Object, java.lang.String)
	 */
	@Override
	protected Object doTransform(Object payload, String enc)
			throws TransformerException {
		ProvaList pList = (ProvaList) ((ProvaList) payload).getFixed()[4];
		
		Document doc = null;
		String htmlContent = "";
		try {
			doc = DocumentHelper.parseText(req_content);
			Element root = doc.getRootElement();
			htmlContent = "<table cellpadding=\"3\" cellspacing=\"3\" border=\"1\"><tr><td height=\"25\"><span class=\"STYLE4\">&nbsp;<b>Workflow</b>: "
					+ root.elementText("Fun")
					+ ".</span></td></tr><tr><td height=\"25\"><span class=\"STYLE4\"  style=\"line-height:1.2\">&nbsp;<b>Description</b>: "
					+ root.element("label").element("Expr").elementText("Ind")+ "</span></td></tr>";
			for(int i= 1; i<pList.getFixed().length;i++){
				ProvaObject obj = pList.getFixed()[i];
				if(obj instanceof ProvaConstant)
					htmlContent += constantToHtml((Element)root.elements().get(i+1), (ProvaConstant) obj, "&nbsp;&nbsp;");
				if(obj instanceof ProvaList){
					htmlContent += provaListToHtml((Element) root.elements().get(i+1), (ProvaList) obj, "&nbsp;&nbsp;");
				}
					
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
		
		htmlContent += "</table>";
	
		return htmlContent;
	}
	
	private String constantToHtml(Element ele, ProvaConstant obj, String space) {
		String indContent = "<tr><td height=\"20\">" + space;
		String value = "";
		if (ele.attribute("default") != null)
			value = ele.attributeValue("default");
			indContent += "&nbsp;&nbsp;name=\""
					+ ele.attributeValue("name") + "\"&nbsp;&nbsp; value = \""+obj+"\"&nbsp;&nbsp;<span class=\"STYLE4\">("
					+ ele.attributeValue("meta") + ")</span>";
		indContent += "</td></tr>";
		return indContent;
	}

	private String provaListToHtml(Element ele, ProvaList pList, String space) {
		String provaListContent = space
				+ "<tr><td><table width=\"100%\"  border=\"0\">";
		for(int i=0; i<pList.getFixed().length;i++){
			ProvaObject obj = pList.getFixed()[i];
			if(obj instanceof ProvaConstant && i == 0)
				provaListContent +="<tr><td height=\"20\"><b>" + space + obj + "</b></td></tr>";
			else if(obj instanceof ProvaConstant && i != 0)
				provaListContent +=constantToHtml((Element)ele.elements().get(i), (ProvaConstant) obj, space+"&nbsp;&nbsp;");
			else if(obj instanceof ProvaList){
				provaListContent +=provaListToHtml((Element) ele.elements().get(i), (ProvaList) obj, space+"&nbsp;&nbsp;");
			}
		}
		
		provaListContent += "</table></td></tr>";
		return provaListContent;
	}


}
