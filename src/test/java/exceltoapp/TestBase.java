package exceltoapp;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import main.ExcelToAppMain;
import spark.Spark;
import spark.utils.IOUtils;

public class TestBase
{
	
	
	
	@BeforeClass
	public static void beforeClass()
	{
		ExcelToAppMain.main(null);
	}

	@AfterClass
	public static void afterClass()
	{
		Spark.stop();
	}
	
	protected String getJsonFile(String jsonType)
	{
		
		  String fileContent="";
		  String path = CalculateControllerTest.class.getResource("/json/"+jsonType).getPath();
		  	  
		try
		{
			
	      FileReader f = new FileReader(path);
	      BufferedReader b = new BufferedReader(f);
	      String line;
	      while((line = b.readLine())!=null)
	      {
	    	  fileContent += line+"\n";
	      }
	       
	      
	      b.close();
	      
	      
		}
		catch(IOException error)
		{
			
			  System.out.println(error.getMessage());
		}

	
		return fileContent;
		
		
		
	}

	protected TestResponse request(String method, String path, String body)
	{

		try
		{
			URL url = new URL("http://localhost:4567" + path);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod(method);

			connection.setDoOutput(true);
			connection.connect();

			//connection.setRequestProperty("Content-Type","application/json");  
		    
			if(body != null)
			{
			byte[] outputInBytes = body.getBytes("UTF-8");
			OutputStream os = connection.getOutputStream();
			os.write( outputInBytes );    
			os.close();
			}
			String responseBody = IOUtils.toString(connection.getInputStream());
			
			return new TestResponse(connection.getResponseCode(), responseBody);
			

		} catch (IOException e)
		{
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}

	}

	protected static class TestResponse
	{

		public String body;
		public int status;

		public TestResponse(int status, String body)
		{
			this.status = status;
			this.body = body;
		}
		
		public TestResponse(int status)
		{
			this.status = status;
			
		}

		public JsonObject json()
		{

			JsonObject data = Json.parse(body).asObject();

			return data;
		}
	}
	
	
}
