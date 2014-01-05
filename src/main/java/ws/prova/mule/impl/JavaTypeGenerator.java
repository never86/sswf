/**
 * 
 */
package ws.prova.mule.impl;

/**
 * @author never86
 *
 */
public class JavaTypeGenerator {

	/**
	 * @param type
	 * @return
	 */
	public static Object generateJavaTypeObject(String type, String value) {
		if (type.equalsIgnoreCase("java.lang.Boolean"))
			return java.lang.Boolean.parseBoolean(value);
		else if (type.equalsIgnoreCase("java.lang.Short"))
			return java.lang.Short.parseShort(value);
		else if (type.equalsIgnoreCase("java.lang.Integer"))
			return java.lang.Integer.parseInt(value);
		else if (type.equalsIgnoreCase("java.lang.Long"))
			return java.lang.Long.parseLong(value);
		else if (type.equalsIgnoreCase("java.lang.Float"))
			return java.lang.Float.parseFloat(value);
		else if (type.equalsIgnoreCase("java.lang.Double"))
			return java.lang.Double.parseDouble(value);
		else if (type.equalsIgnoreCase("java.lang.String"))
			return value;
		else
			return null;
	}
	
	public static Class getJavaType(String type) {
		if (type.equalsIgnoreCase("java.lang.Boolean"))
			return java.lang.Boolean.class;
		else if (type.equalsIgnoreCase("java.lang.Short"))
			return java.lang.Short.class;
		else if (type.equalsIgnoreCase("java.lang.Integer"))
			return java.lang.Integer.class;
		else if (type.equalsIgnoreCase("java.lang.Long"))
			return java.lang.Long.class;
		else if (type.equalsIgnoreCase("java.lang.Float"))
			return java.lang.Float.class;
		else if (type.equalsIgnoreCase("java.lang.Double"))
			return java.lang.Double.class;
		else
			return null;
	}

}
