package ws.prova.mule.impl;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.transformer.AbstractTransformer;

import ws.prova.kernel2.ProvaConstant;
import ws.prova.kernel2.ProvaList;
import ws.prova.kernel2.ProvaObject;
import ws.prova.kernel2.ProvaVariable;
import ws.prova.reference2.ProvaConstantImpl;

/**
 * <code>Prova2RuleMLTranslator</code> translates a Prova Message into a
 * Reaction RuleML Message which can be interchanged
 * 
 * @author <a href="mailto:adrian.paschke@gmx.de">Adrian Paschke</a>
 * @version
 */
public class Prova2RuleMLTranslator extends AbstractTransformer {

	protected static transient Log logger = LogFactory
			.getLog(Prova2RuleMLTranslator.class);

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = -408128452488674866L;

	private static final String spacerItem = "   ";

	public Prova2RuleMLTranslator() {
		super();
		this.registerSourceType(Object.class);
	}

	private boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

	private String serializeProvaVariable(ProvaVariable ct, String spacer) {
		String serializedVariable = "";
		String variable = ct.toString();
		String type = "";
		if (variable.indexOf(":") != -1 && variable.indexOf("://") == -1) {
			type = variable.substring(variable.lastIndexOf(":") + 1);
			variable = variable.substring(0, variable.lastIndexOf(":"));
		}

		if (variable.indexOf(".") != -1) {
			type = variable.substring(0, variable.lastIndexOf("."));
			variable = variable.substring(variable.lastIndexOf(".") + 1);
		}

		if (isNumeric(variable))
			variable = "@" + variable;
		if (variable.startsWith("<")) {
			variable = variable.substring(1, variable.length() - 1);
			variable = "@" + variable;
		}
		if (type.equals(""))
			serializedVariable = serializedVariable + spacer + spacerItem
					+ "<Var>" + variable + "</Var>\n";
		else
			serializedVariable = serializedVariable + spacer + spacerItem
					+ "<Var type=\"" + type + "\">" + variable + "</Var>\n";
		return serializedVariable;
	}

	private String serializeProvaConstant(Object ct, String spacer) {
		String serializedConstant = "";
		ProvaConstant constantObject = (ProvaConstant) ct;
		if(constantObject.getObject() instanceof ProvaVariable)
			return serializeProvaVariable((ProvaVariable)constantObject.getObject(),spacer);
		String constant = ct.toString();
		String type = constantObject.getObject().getClass().getName();

		if (!type.equalsIgnoreCase(""))
			serializedConstant = serializedConstant + spacer + spacerItem
					+ "<Ind type=\"" + type + "\">" + constant + "</Ind>\n";
		else
			serializedConstant = serializedConstant + spacer + spacerItem
					+ "<Ind>" + constant + "</Ind>\n";
		return serializedConstant;
	}

	private String serializeProvaList(ProvaList ct, String spacer,
			boolean isAnswer) {

		ProvaObject[] tokens = ct.getFixed();

		String serializedProvaList = "";
		if (tokens.length > 1) {
			if (tokens[0].toString().indexOf(" ") == -1) { // Expr complex
				serializedProvaList = serializedProvaList + spacer + spacerItem
						+ "<Expr>\n";
				for (int i = 0; i < tokens.length; i++) {
					if (i == 0)
						serializedProvaList = serializedProvaList + spacer
								+ spacerItem + spacerItem + "<Fun>" + tokens[i]
								+ "</Fun>\n";
					else {
						Object t = tokens[i];
						serializedProvaList = serializedProvaList
								+ serializedProvaObject(t, isAnswer, spacer
										+ spacerItem);
					}
				}
			}
			if (tokens.length > 0)
				serializedProvaList = serializedProvaList + spacer + spacerItem
						+ "</Expr>\n";
		}
		return serializedProvaList;
	}

	private String serializedProvaObject(Object t, boolean isAnswer,
			String spacer) {
		String serializedContent = "";
		if (t instanceof ProvaVariable){
			serializedContent = serializeProvaVariable((ProvaVariable) t,
					spacer);
		}
		else if (t instanceof ProvaList){
			serializedContent = serializeProvaList((ProvaList) t, spacer,
					isAnswer);
		}
		else if (t instanceof ProvaConstant && ((ProvaConstant)t).getObject() instanceof ProvaList){
			serializedContent = serializeProvaList((ProvaList) ((ProvaConstant)t).getObject(), spacer,
					isAnswer);
		}else if (t instanceof ProvaConstant){
			serializedContent = serializeProvaConstant(t, spacer);
		}

		return serializedContent;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
	 * 
	 * @return returns the input message if the translation fails
	 */
	public Object doTransform(Object src, String encoding) {
		if (src instanceof ProvaList) {
			try {
				ProvaList pmes = (ProvaList) src;
				ProvaObject[] provaObjects = pmes.getFixed();
				boolean isAnswer;
				if (pmes.performative().equals("stop_communication"))
					return pmes;
				else if (pmes.performative().equals("answer"))
					isAnswer = true;
				else
					isAnswer = false;

				ProvaObject payloadObject = provaObjects[4];

				ProvaObject[] objects = null;
				ArrayList<ProvaObject> payloads = new ArrayList<ProvaObject>();
				if (payloadObject instanceof ProvaList)
					objects = ((ProvaList) payloadObject).getFixed();

				if ((payloadObject instanceof ProvaConstant)
						&& (((ProvaConstant) payloadObject).getObject() instanceof ProvaList)) {
					Object obj = ((ProvaConstant) payloadObject).getObject();
					objects = ((ProvaList) obj).getFixed();
				}

				for (int j = 0; j < objects.length; j++) {
					payloads.add(objects[j]);
				}

				Object conv_id = ((ProvaConstantImpl) provaObjects[0])
						.toString();
				String ruleMLpayload = "";

				if (payloads.size() > 0) {
					if (payloads.get(0).toString().indexOf(" ") == -1) {
						ruleMLpayload = ruleMLpayload + spacerItem + spacerItem
								+ "<Atom>\n";
						for (int i = 0; i < payloads.size(); i++) {
							if (i == 0)
								ruleMLpayload = ruleMLpayload + spacerItem
										+ spacerItem + spacerItem + "<Rel>"
										+ payloads.get(0).toString()
										+ "</Rel>\n";
							else {
								Object t = payloads.get(i);
								ruleMLpayload += serializedProvaObject(t,
										isAnswer, spacerItem + spacerItem
												+ spacerItem);
							}
						}
					}
					ruleMLpayload = ruleMLpayload + spacerItem + spacerItem
							+ "</Atom>\n";
				} else {
					ruleMLpayload = ruleMLpayload + "<Ind>\"";
					ruleMLpayload = ruleMLpayload + payloads.get(0);
					ruleMLpayload = ruleMLpayload + "\"</Ind>\n";
				}

				String rulemlMes = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
						+ "<RuleML "
						+ "xmlns=\"http://ruleml.org/spec\" "
						+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
						+ "xsi:schemaLocation=\"http://ruleml.org/spec http://www.ruleml.org/1.0/xsd/datalog.xsd "
						+ "http://ibis.in.tum.de/research/ReactionRuleML/0.2/rr.xsd\">\n"
						+ "<Message mode=\"outbound\" directive=\""
						+ pmes.performative()
						+ "\">\n"
						+ spacerItem
						+ "<oid>\n"
						+ spacerItem
						+ "<Ind>"
						+ conv_id.toString()
						+ "</Ind>\n"
						+ spacerItem
						+ "</oid>\n"
						+ spacerItem
						+ "<protocol>\n"
						+ spacerItem
						+ spacerItem
						+ "<Ind>esb</Ind>\n"
						+ spacerItem
						+ "</protocol>\n"
						+ spacerItem
						+ "<sender>\n"
						+ spacerItem
						+ spacerItem
						+ "<Ind>"
						+ ((ProvaConstant) provaObjects[2]).toString()
						+ "</Ind>\n"
						+ spacerItem
						+ "</sender>\n"
						+ spacerItem
						+ "<content>\n"
						+ ruleMLpayload
						+ spacerItem
						+ "</content>\n" + "</Message>\n" + "</RuleML>\n";

				return rulemlMes; // return serialized RuleML XML message

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error during translation of RMessage into RuleML - send RMessage instead: "
						+ src);
				logger.error(e);
				return src; // simply return untranslated message
			}
		}

		return src;
	}

}
