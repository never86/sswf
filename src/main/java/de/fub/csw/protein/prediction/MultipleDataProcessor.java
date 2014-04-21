package de.fub.csw.protein.prediction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ws.prova.kernel2.ProvaList;
import de.fub.csw.constant.StringConstants;

public class MultipleDataProcessor {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static List getData(String line) {
		List list = new ArrayList();
		String[] items = line.split("	");
		for (int i = 0; i < items.length; i++) 
					list.add(items[i].trim());
		return list;
	}
	
	public static void recording(Object overall, Object ontoReasoning, Object count, Object result) throws IOException {
		String logFile = StringConstants.appDir + File.separator + "rules"
				+ File.separator + "protein_prediction_analysis"
				+ File.separator +"analysis.log";
	    File file = new File(logFile);
	      
        FileWriter fileWriter = new FileWriter(file,true);
      
        BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
        if(Integer.parseInt(count.toString())!=0)
        fileWriter.append(overall+" "+ ontoReasoning+" "+count+" "+result+"\n");
       
      
        bufferFileWriter.close();
	}

}
