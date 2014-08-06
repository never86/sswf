/**
 * 
 */
package de.fub.csw.util;

import java.util.List;

/**
 * @author never86
 *
 */
public class ProjectUtil {

	public static boolean inList(String term, List<String> terms) {
		for (int i = 0; i < terms.size(); i++) {
			if (term.equalsIgnoreCase(terms.get(i)))
				return true;
		}
		return false;
	}

}
