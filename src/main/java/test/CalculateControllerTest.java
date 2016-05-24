package test;

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

	@Test
	public void simpleCalculate() throws IOException
	{
		
		try
		{
	     String cadena;
	      FileReader f = new FileReader("json_exceltoapp.json");
	      BufferedReader b = new BufferedReader(f);
	      while((cadena = b.readLine())!=null) {
	          System.out.println(cadena);
	      }
	      b.close();
		}
		catch(IOException error)
		{
			
			  System.out.println(error.getMessage());
		}

		TestResponse res = request("POST", "/calculate/1/1","{'f55':'55'}");
		// JsonObject json = res.json();

		System.out.println("se recibio : " + res.body);

		assertEquals(200, res.status);

		// assertEquals("john", json.get("name"));
		// assertEquals("john@foobar.com", json.get("email"));
		// assertNotNull(json.get("id"));

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
