package ws.prova.mule.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import de.fub.csw.constant.StringConstants;

import ws.prova.kernel2.ProvaList;

/**
 * <code>RuleML2ProvaTranslator</code> translate a Reaction RuleML message into
 * a Prova message The translator uses the specified XSLT.
 * 
 * If the RuleML message can not be translated, the original RuleML message will
 * be returned
 * 
 * @author <a href="mailto:adrian.paschke@gmx.de">Adrian Paschke</a>
 * @version
 */
public class RuleML2ProvaTranslator extends AbstractTransformer {

	protected static transient Log logger = LogFactory
			.getLog(RuleML2ProvaTranslator.class);

	/**
	 * Serial version
	 */
	private static final long serialVersionUID = -408128452488674866L;

	private TransformerFactory tFactory = TransformerFactory.newInstance();
	private InputStream is = null;
	private Source xmlSource = null;
	private Source xslSource = null;
	private Transformer transformer = null;

	private String xslt = "rrml2prova.xsl";

	public RuleML2ProvaTranslator() {
		super();
		this.registerSourceType(Object.class);
		this.xslt = StringConstants.appDir+File.separator+"conf"+File.separator+"rrml2prova.xsl";
	}

	public RuleML2ProvaTranslator(String _xslt) {
		super();
		this.registerSourceType(Object.class);
		xslt = _xslt;
	}

	public void setXSLT(String _xslt) {
		xslt = _xslt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mule.transformers.AbstractTransformer#doTransform(java.lang.Object)
	 * 
	 * @returns the input message if the translation fails
	 */
	public synchronized Object doTransform(Object src, String encoding)
			throws TransformerException {
		if (src instanceof String) {
			try {

				InputStream in = null;
				try {
					// read model from file on classpath
					in = Thread.currentThread().getContextClassLoader()
							.getResourceAsStream(xslt);
					if (in == null) {
						// read xslt from file given a relative path
						in = new FileInputStream(xslt);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					// read model from URL
					URL url = new URL(xslt);
					in = url.openStream();
				}
				if (in == null) {
					logger.error("Can not load XSLT");
					return src; // no XSLT translator; return untranslated
								// message
				}

				xslSource = new StreamSource(in); // XSLT Source

				// Get the XML input
				String message = src.toString();
				

				if (message.indexOf("'") != -1) {
					String query = message.substring(message.indexOf("'"),
							message.lastIndexOf("'") + 1);
					String encodedQuery = URLEncoder.encode(
							query.substring(1, query.length() - 1), "UTF-8");
					message = message.replace(query, encodedQuery);
				}

				if (message != null && message.length() > 0)
					is = new ByteArrayInputStream(message.getBytes());

				if (is == null) {
					logger.error("XML input message invalid");
					return src;
				}

				
				xmlSource = new StreamSource(is); // XML Source
				// transform XML message into Prova RMessage
				transformer = tFactory.newTransformer(xslSource);

				// Perform the transformation.
				StringWriter provaMessage = new StringWriter();
				transformer
						.transform(xmlSource, new StreamResult(provaMessage));

				// Extract complex objects from String
				String output = provaMessage.toString();
				ProvaList list = (ProvaList) new String2ProvaList().transform(output);

				return list;

			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Error during translation of RuleML message into RMessage");
				logger.error(e);
				return src; // simply return untranslated message
			}

		}
		return src; // no translator found; return untranslated message
	}

}