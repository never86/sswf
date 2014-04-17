package de.fub.csw.ant.identification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import ws.prova.kernel2.ProvaList;
import de.fub.csw.constant.StringConstants;

public class Curation {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void log(ProvaList parameters, Object result, Object xid) throws IOException {
		String logFile = StringConstants.appDir + File.separator + "rules"
				+ File.separator + "ant_identification"
				+ File.separator +"identification.log";
	    File file = new File(logFile);
	      
        FileWriter fileWriter = new FileWriter(file,true);
      
        BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
        fileWriter.append("================================="+xid+"=================================\n\n");
        
        fileWriter.append("Ant description:\n"+parameters+"\n\n");
        fileWriter.append("Result:\n"+result+"\n\n");
        fileWriter.append("Time:\n"+new Date().toGMTString()+"\n\n");
       
      
        bufferFileWriter.close();

	}

}
