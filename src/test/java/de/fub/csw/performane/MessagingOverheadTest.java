package de.fub.csw.performane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MessagingOverheadTest {


	public static void messagingOverheadTest(int tasks) {
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><interface>   <label>  <Expr> <Fun uri=\"dc:description\"/> <Ind>Test the system performance.</Ind>     </Expr>   </label>    <Fun per=\"value\">messagingOverheadTest</Fun>    <Expr>     <Fun meta=\"Workflow input(s)\">inArgs</Fun>      <Var mode=\" \" type=\"java.lang.Integer\" meta=\"Number of Tasks\" default=\""
				+ tasks
				+ "\" name=\"NumberOfTasks\">"
				+ tasks
				+ "</Var>      </Expr>    <Expr>     <Fun meta=\"Workflow ouput(s)\">outArgs</Fun>      <Var mode=\"-\" meta=\"Result\" default=\"Result\" name=\"Result\"/>   </Expr> </interface>";

		String getURL = "http://localhost:8888/?payload="
				+ URLEncoder.encode(message);
		
		System.out.println(getURL);

		URL getUrl = null;
		try {
			getUrl = new URL(getURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		URLConnection connection = null;
		try {
			connection = (HttpURLConnection) getUrl.openConnection();
			connection.setConnectTimeout(5000000);
			connection.setReadTimeout(100000000);
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));

			String lines;
			while ((lines = reader.readLine()) != null) {
				System.out.print("Workflow is successful.   ");
			}

			reader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		   long start = System.currentTimeMillis();
		   messagingOverheadTest(10);
		   double time = (System.currentTimeMillis() - start) / 1000.0;
		System.out.println("The whole workflow execution takes " + time +"s.");

	}

}
