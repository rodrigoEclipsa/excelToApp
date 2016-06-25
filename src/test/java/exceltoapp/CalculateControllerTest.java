package exceltoapp;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class CalculateControllerTest extends TestBase
{

	
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

	
	
	@Test
	public void dataTableCalculate() throws IOException
	{
		
		String fileContent = getJsonFile("dataTable.json");
		
		System.out.println("fileContent : " + fileContent);
		
	

		  		TestResponse res = request("POST", "/calculate/1/1",fileContent);
		  		
		  		//JsonObject json = res.json();

		  		System.out.println("se recibio : \n" + res.body);

		  		assertEquals(200, res.status);

	
	}

	
	@Test
	public void nInstancesCalculate() throws IOException
	{
		
		String fileContent = getJsonFile("nInstances.json");
		
		System.out.println("fileContent : " + fileContent);
		
	

		  		TestResponse res = request("POST", "/calculate/1/1",fileContent);
		  		
		  		//JsonObject json = res.json();

		  		System.out.println("se recibio : \n" + res.body);

		  		assertEquals(200, res.status);

	}
	
	
	
	
	

}
