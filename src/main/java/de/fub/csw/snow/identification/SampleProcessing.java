package de.fub.csw.snow.identification;



import java.io.File;

import java.io.FileOutputStream;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;



import de.fub.csw.constant.StringConstants;



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



	public static String processResults(List list, String cid) {

		cid = cid.replace(":", "-");

		String path = StringConstants.appDir + File.separator + "rules"

				+ File.separator + "snow_depth_monitoring"

				+ File.separator + cid + ".txt";

		String content = "";

		for (int i = 0; i < list.size(); i++)

			content += list.get(i) + ";\n";

		FileOutputStream fop = null;

		File file;



		try {



			file = new File(path);

			fop = new FileOutputStream(file);



			// if file doesnt exists, then create it

			if (!file.exists()) {

				file.createNewFile();

			}



			// get the content in bytes

			byte[] contentInBytes = content.getBytes();



			fop.write(contentInBytes);

			fop.flush();

			fop.close();



		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (fop != null) {

					fop.close();

				}

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

		return path;

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

