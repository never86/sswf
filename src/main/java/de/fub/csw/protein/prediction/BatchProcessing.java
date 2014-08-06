package de.fub.csw.protein.prediction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.oreilly.servlet.HttpMessage;

import de.fub.csw.constant.StringConstants;

public class BatchProcessing {

	private static String workDir = StringConstants.userDir + File.separator
			+ "rules" + File.separator + "protein_prediction_analysis"
			+ File.separator;

	private static String trueFile = workDir + "temp" + File.separator
			+ "true.txt";
	private static String falseFile = workDir + "temp" + File.separator
			+ "false.txt";

	public static void send(String protein, String goTerm) {
		URL receiverURL = null;

		try {
			receiverURL = new URL("http://localhost:8888/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpMessage msg = new HttpMessage(receiverURL);
		Properties props = new Properties();

		String finalMessage = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><interface>   <label>  <Expr> <Fun uri=\"dc:description\"/>        <Ind>Protein prediciton results analysis.</Ind>     </Expr>   </label>    <Fun per=\"value\">proteinPredicitonAnalysisProcess</Fun>    <Expr>     <Fun meta=\"Workflow input(s)\">inArgs</Fun>      <Var mode=\" \" type=\"java.lang.String\" meta=\"Protein name\" default=\""
				+ protein
				+ "\" name=\"protein\">"
				+ protein
				+ "</Var>      <Var mode=\" \" type=\"java.lang.String\" meta=\"Gene term\" default=\""
				+ goTerm
				+ "\" name=\"goTerm\">"
				+ goTerm
				+ "</Var>   </Expr>    <Expr>     <Fun meta=\"Workflow ouput(s)\">outArgs</Fun>      <Var mode=\"-\" meta=\"Analysis result\" default=\"Result\" name=\"Result\"/>   </Expr> </interface>";

		props.put("payload", finalMessage);
		try {
			setTagFalse();
			msg.sendPostMessage(props);
		} catch (IOException e) {
			e.printStackTrace();
			System.out
					.println("=======================Sent failed=========================");
		}

		System.out
				.println("=======================Sent successful=========================");

	}

	public static void main(String[] args) throws IOException {
		File fin = new File(workDir + "function_prediction_human.txt");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fin);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));

		String line = null;
		int j = 0;
		while ((line = br.readLine()) != null) {
			System.out.println("Line " + (++j) + ": " + line);
			List list = new ArrayList();
			String[] items = line.split("	");
			for (int i = 0; i < items.length; i++)
				list.add(items[i].trim());
			if (canSubmit()) {
				send(list.get(0).toString(), list.get(1).toString());
			} else {
				while (!canSubmit()) {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (canSubmit())
						break;
				}
				send(list.get(0).toString(), list.get(1).toString());
			}
		}

		br.close();

	}

	public static void setTagTrue() throws IOException {
		File file = new File(falseFile);

		// File (or directory) with new name
		File file2 = new File(trueFile);
		if (file2.exists())
			throw new java.io.IOException("file exists");

		// Rename file (or directory)
		boolean success = file.renameTo(file2);
		if (!success) {
			System.out.println("Changing the file to 'true' failed.");
		} else

			System.out.println("Changing the file to 'true' succeed."
					+ new Date().toGMTString());
	}

	public static void setTagFalse() throws IOException {
		File file = new File(trueFile);

		// File (or directory) with new name
		File file2 = new File(falseFile);
		if (file2.exists())
			throw new java.io.IOException("file exists");

		// Rename file (or directory)
		boolean success = file.renameTo(file2);
		if (!success) {
			System.out.println("Changing the file to 'false' failed.");
		} else
			System.out.println("Changing the file to 'false' succeed."
					+ new Date().toGMTString());
	}

	public static boolean canSubmit() {
		File file = new File(trueFile);
		if (file.exists())
			return true;
		else
			return false;
	}

}
