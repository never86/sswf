package de.fub.csw.protein.prediction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QuickGOClient {

	
	public static List getGOTermsByProteinID(String proteinID) {
		// URL for annotations from QuickGO for one protein
		 List<String> terms=new ArrayList<String>();
       URL u = null;
	try {
		u = new URL("http://www.ebi.ac.uk/QuickGO/GAnnotation?protein="+proteinID+"&format=tsv");
		
		// Connect
	       HttpURLConnection urlConnection = (HttpURLConnection) u.openConnection();
	       // Get data
	       BufferedReader rd=new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
	       // Read data
	       List<String> columns=Arrays.asList(rd.readLine().split("\t"));
			//	       System.out.println(columns);
	       // Collect the unique terms as a sorted set
	       // Find which column contains GO IDs
	       int termIndex=columns.indexOf("GO ID");
	       // Read the annotations line by line
	       String line;
	       while ((line=rd.readLine())!=null) {
	           // Split them into fields
	           String[] fields=line.split("\t");
	           // Record the GO ID
	           terms.add(fields[termIndex]);
	           //break;
	       }
	       // close input when finished
	       rd.close();
	} catch (MalformedURLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
       return terms;
   }

}
