
package calc;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.5.1
 * 2013-11-04T15:47:59.649+01:00
 * Generated source version: 2.5.1
 * 
 */
public final class AddPortType_AddHttpEndpoint_Client {

    private static final QName SERVICE_NAME = new QName("http://add.fub.csw.de", "Add");

    private AddPortType_AddHttpEndpoint_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = Add_Service.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        Add_Service ss = new Add_Service(wsdlURL, SERVICE_NAME);
        AddPortType port = ss.getAddHttpEndpoint();  
        
        {
        System.out.println("Invoking add...");
        java.lang.Double _add_d1 = null;
        java.lang.Double _add_d2 = null;
        java.lang.Double _add__return = port.add(_add_d1, _add_d2);
        System.out.println("add.result=" + _add__return);


        }

        System.exit(0);
    }

}
