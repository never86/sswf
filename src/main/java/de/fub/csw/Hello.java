package de.fub.csw;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;


public class Hello {
	
	private static Object obj1, obj2, obj3;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
//		org.apache.cxf.endpoint.Client client = dcf.createClient("http://aspalliance.com/quickstart/aspplus/samples/services/MathService/VB/MathService.asmx?wsdl");
		org.apache.cxf.endpoint.Client client = dcf.createClient("http://websrv.cs.fsu.edu/~engelen/calc.wsdl");

		Object[] result = null;
		try {
			result = client.invoke("add",15.3,4.7);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(result[0]);
	}
	
	public static void setT1(Object obj){
		obj1 = obj;
	}
	
	public static void setT2(Object obj){
		obj2 = obj;
	}
	
	public static void setT3(Object obj){
		obj3 = obj;
		System.out.println(obj1+"   "+obj2+"  "+obj3);
		System.out.println("AAAAAAAAAAAAAAAAAAAAAAA");
	}

}
