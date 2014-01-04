package de.fub.csw.log;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import de.fub.csw.constant.StringConstants;


public class LogTool{
	private Logger logger;

	public LogTool(Object obj){
		logger = Logger.getLogger(obj.getClass());	
		PropertyConfigurator.configure(StringConstants.appDir+File.separator+"conf"+File.separator+"log4j.xml");		
	}
	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}
}
