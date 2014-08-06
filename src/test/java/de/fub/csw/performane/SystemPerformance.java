package de.fub.csw.performane;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SystemPerformance {

	public static void concurrencyTest(String wfExecutionTime) throws IOException {
		URL url = null;
		String message = "/?payload=<?xml version=\"1.0\" encoding=\"UTF-8\"?><interface>   <label>  <Expr> <Fun uri=\"dc:description\"/> <Ind>Test the system performance.</Ind>     </Expr>   </label>    <Fun per=\"value\">concurrencyTest</Fun>    <Expr>     <Fun meta=\"Workflow input(s)\">inArgs</Fun>      <Var mode=\" \" type=\"java.lang.String\" meta=\"Workflow execution time\" default=\""
				+ wfExecutionTime
				+ "\" name=\"WfExeTime\">"
				+ wfExecutionTime
				+ "</Var>      </Expr>    <Expr>     <Fun meta=\"Workflow ouput(s)\">outArgs</Fun>      <Var mode=\"-\" meta=\"Result\" default=\"Result\" name=\"Result\"/>   </Expr> </interface>";try {
			url = new URL("http://localhost:8888/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");

		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(message);
		writer.close();

	}
	
	public static void messagingOverheadTest(int tasks) throws IOException {
		URL url = null;
		String message = "/?payload=<?xml version=\"1.0\" encoding=\"UTF-8\"?><interface>   <label>  <Expr> <Fun uri=\"dc:description\"/> <Ind>Test the system performance.</Ind>     </Expr>   </label>    <Fun per=\"value\">concurrencyTest</Fun>    <Expr>     <Fun meta=\"Workflow input(s)\">inArgs</Fun>      <Var mode=\" \" type=\"java.lang.String\" meta=\"Number of Tasks\" default=\""
				+ tasks
				+ "\" name=\"NumberOfTasks\">"
				+ tasks
				+ "</Var>      </Expr>    <Expr>     <Fun meta=\"Workflow ouput(s)\">outArgs</Fun>      <Var mode=\"-\" meta=\"Result\" default=\"Result\" name=\"Result\"/>   </Expr> </interface>";try {
			url = new URL("http://localhost:8888/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");

		OutputStreamWriter writer = new OutputStreamWriter(
				connection.getOutputStream());
		writer.write(message);
		writer.close();

	}
	
	

	public static void main(String[] args) throws IOException {
//		for (int i = 0; i < 3; i++) {
//			concurrencyTest("100L");
//		}
		
		messagingOverheadTest(10);
	}

}
