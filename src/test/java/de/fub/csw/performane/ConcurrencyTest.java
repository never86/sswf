package de.fub.csw.performane;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ConcurrencyTest extends Thread {

	private String tag = "";
	private static double timer = 0;
	private static int count = 0;
	private static long start = 0;

	public ConcurrencyTest(String tag2) {
		// TODO Auto-generated constructor stub
		tag = tag2;
	}

	public void run() {
		long start1 = System.currentTimeMillis();
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><interface>   <label>  <Expr> <Fun uri=\"dc:description\"/> <Ind>Test the system performance.</Ind>     </Expr>   </label>    <Fun per=\"value\">concurrencyTest</Fun>    <Expr>     <Fun meta=\"Workflow input(s)\">inArgs</Fun>      <Var mode=\" \" type=\"java.lang.Double\" meta=\"Workflow execution time\" default=\""
				+ 100
				+ "\" name=\"WfExeTime\">"
				+ 100
				+ "</Var>      </Expr>    <Expr>     <Fun meta=\"Workflow ouput(s)\">outArgs</Fun>      <Var mode=\"-\" meta=\"Result\" default=\"Result\" name=\"Result\"/>   </Expr> </interface>";
		
		String getURL = "http://localhost:8888/?payload="
				+ URLEncoder.encode(message);
		
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
				System.out.print("Workflow " + tag + " is successful.   ");
			}

			reader.close();
			count++;
			double time = (System.currentTimeMillis() - start1) / 1000.0;
			double throughput = (count / ((System.currentTimeMillis() - start) / 60000.0));
			timer += time;
			System.out.println("Total tasks: " + count + "  Total time: "
					+ timer + "s   Average: " + timer / count
					+ "s  Throughtput: " + throughput);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {
		start = System.currentTimeMillis();
		for (int i = 0; i < 2; i++) {
			System.out.println(i);
			ConcurrencyTest bp = new ConcurrencyTest(i + "");
			bp.start();
		}

	}

}
