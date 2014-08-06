package de.fub.csw.protein.prediction;

import java.io.BufferedWriter;

import java.io.File;

import java.io.FileWriter;

import java.io.IOException;

import java.util.ArrayList;

import java.util.List;

import org.apache.commons.logging.Log;

import org.apache.commons.logging.LogFactory;

import de.fub.csw.constant.StringConstants;

public class DataProcessor {
	private static String workDir = StringConstants.appDir + File.separator + "rules"
			+ File.separator + "protein_prediction_analysis"
			+ File.separator;

	protected static transient Log logger = LogFactory
			.getLog(DataProcessor.class);

	public static List getData(String line) {
		List list = new ArrayList();
		String[] items = line.split("	");
		for (int i = 0; i < items.length; i++)
			list.add(items[i].trim());
		return list;
	}

	public static void analysisLogRecording(Object protein,
			Object predictedGOTerm, Object overall, Object ontoReasoning,
			Object count, Object result) throws IOException {
		String logFile = workDir + "analysis_fruit_fly.log";

		File file = new File(logFile);
		// logger.info("The anlaysis summary of protein prediction results has been stored at: "+file.getAbsolutePath());
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
		if (Integer.parseInt(count.toString()) != 0)
			fileWriter.append(protein + " " + predictedGOTerm + " " + overall
					+ " " + ontoReasoning + " " + count + " " + result + "\n");
		bufferFileWriter.close();

	}

	public static void ontoLoadingLogRecording(Object predictedGOTerm,
			Object time) throws IOException {
		String logFile = workDir + "goLoading.log";

		File file = new File(logFile);
		// logger.info("The anlaysis summary of protein prediction results has been stored at: "+file.getAbsolutePath());
		FileWriter fileWriter = new FileWriter(file, true);
		BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);
		fileWriter.append(predictedGOTerm + " " + time + "\n");
		bufferFileWriter.close();

	}

}
