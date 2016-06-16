package exceltoapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import main.Main;
import spark.Spark;
import spark.utils.IOUtils;

public class CalculateControllerTest
{

	
	
	
	@BeforeClass
	public static void beforeClass()
	{
		Main.main(null);
	}

	@AfterClass
	public static void afterClass()
	{
		Spark.stop();
	}
	
	
	
	private String getJsonFile(String jsonType)
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

	@Test
	public void simpleCalculate() throws IOException
	{
		
		String fileContent = getJsonFile("simpleCalculate.json");
		
		System.out.println("fileContent : " + fileContent);
		
	

		  		TestResponse res = request("POST", "/calculate/1/1",fileContent);
		  		
		  		//JsonObject json = res.json();

		  		System.out.println("se recibio : \n" + res.body);

		  		assertEquals(200, res.status);

		
	
	}

	private TestResponse request(String method, String path, String body)
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
		

			
			byte[] outputInBytes = body.getBytes("UTF-8");
			OutputStream os = connection.getOutputStream();
			os.write( outputInBytes );    
			os.close();
			
			
			String responseBody = IOUtils.toString(connection.getInputStream());
			return new TestResponse(connection.getResponseCode(), responseBody);

		} catch (IOException e)
		{
			e.printStackTrace();
			fail("Sending request failed: " + e.getMessage());
			return null;
		}

	}

	private static class TestResponse
	{

		public final String body;
		public final int status;

		public TestResponse(int status, String body)
		{
			this.status = status;
			this.body = body;
		}

		public JsonObject json()
		{

			JsonObject data = Json.parse(body).asObject();

			return data;
		}
	}

}
