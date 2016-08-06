package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import conf.Conf;
import controller.CalculateController;
import spark.Spark;



public class Main {
	

	
    public static void main(String[] args) {
    	
    	
    	
    	//--------- configuro el server
    	int maxThreads = 8;
    	int minThreads = 2;
    	int timeOutMillis = 30000;
    	Spark.threadPool(maxThreads, minThreads, timeOutMillis);
    	Spark.port(4567);
    	
    	//------------------------------
    	
    	//--------busco archivo de configuracion y asigno datos
		File file = new File("conf/config.properties");
    	Properties prop = new Properties();
    	InputStream stream;
    	
		try
		{
			
			stream = new FileInputStream(file.getPath());
			prop.load(stream);
			
			
			
		} 
		catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
    	
        Conf.spreadsheetPath = prop.getProperty("spreadsheetPath");
		
		
    ///------------------------------------------------------------
    	
       new CalculateController();
    	
    
    
}
    
    
    
}