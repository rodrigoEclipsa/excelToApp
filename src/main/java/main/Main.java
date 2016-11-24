package main;

import static spark.Spark.after;
import static spark.Spark.before;

import conf.Conf;
import controller.CalculateController;
import spark.Spark;

public class Main extends BaseMain
{

	public Main()
	{
		super();
    	//--------- configuro el server
    	int maxThreads = 8;
    	int minThreads = 2;
    	int timeOutMillis = 30000;
    	Spark.threadPool(maxThreads, minThreads, timeOutMillis);
    	Spark.port(4567);
    	
    	//------------------------------
    	
    
        Conf.spreadsheetPath = prop.getProperty("spreadsheetPath");
      
    ///------------------------------------------------------------
    	
        
		before((request, response) -> 
		{
			
	   	  response.header("Access-Control-Allow-Origin", "*");
	      response.header("Access-Control-Request-Method","POST");
	      response.header("Access-Control-Allow-Headers","*" );
			
		   // boolean authenticated = true;
		     
		   // if (!authenticated) {
		     //   halt(401, "You are not welcome here");
			
		    //}
			
			
		});
		

		after((request, response) -> {
			// response.header("Content-Encoding", "gzip");
		});
			

		
	    new CalculateController();
    	
	       System.out.println("server init...");
	}

	
}
