package de.fub.csw.snow.identification;

import java.util.ArrayList;
import java.util.List;

public class SampleProcessing {

	/**
	 * @param args
	 */
	public static List getSampleItems(String line) {
		List list = new ArrayList();
		String[] items = line.split(" ");
		for (int i = 0; i < items.length; i++) {
			if (i == 1) {
				String[] dateItems = items[i].split("/");
				for (int j = 0; j < dateItems.length; j++)
					list.add(dateItems[j].trim());
			} else {
				String str = items[i].trim();
				if (!isNumeric(str))
					list.add(str);
				else
					list.add(Double.parseDouble(str));
			}

		}
		return list;
	}

	public static String processResults(List list) {
		String results = "";
		for (int i = 0; i < list.size(); i++)
			results += list.get(i) + ";";
		return results;
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			int chr = str.charAt(i);
			if (chr == 46)
				continue;
			if (chr < 48 || chr > 57)
				return false;
		}
		return true;
	}

}
