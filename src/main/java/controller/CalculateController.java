package controller;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.post;

import java.util.ArrayList;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

import classes.WorkBookInfo;
import conf.Conf;
import manager.ExcelManager;
import util.GeneralUtil;



public class CalculateController
{
	
	
	public CalculateController()
	{
		
	
		
	//--------armo el api
    	
		
		

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
			
		
		
	
	/*
    	
		 get("/calculate/login", (request, response) -> 
    	 {
    		
    		 OAuthClientRequest requestauth = OAuthClientRequest
  				   .authorizationLocation("http://exceltoapp.eclipsa.com.ar/administration/oauth2/authorize")
  				   .setClientId("test")
  				   .setResponseType("code")
  				   .setState("1235678")
  				   .setRedirectURI("http://localhost:4567/calculate/login/callbackauth")
  				   .buildQueryMessage();
  	
  		      response.redirect(requestauth.getLocationUri());
  		      
           System.out.println("auth uri: " + requestauth.getLocationUri());
         
    		
           return "hola";
 		});
		 */
		/*
		 get("/calculate/login/callbackauth", (request, response) -> 
    	 {
    		 
    		 System.out.println(request.queryParams("code"));
    		 System.out.println(request.queryParams("state"));
    		 
    		 return "";
    		 
    	 });
		 */
		 
    	 /**
    	  * 
    	  * peticion para calcular
    	  * 
    	  * http://localhost:4567/calculate/1/1/Margenbrutoganadero18meses.xls/dcria
    	  * 
    	  */
    	 post("/calculate/:client_id/:groupapp_id", (request, response) -> 
    	 {
    		 
    		try
    		{
          
             //---------------------------
    		 String clientId = request.params(":client_id");
    		 String groupappId = request.params(":groupapp_Id");
    		
    		 JsonObject data =  Json.parse(request.body()).asObject();
    		
    		 //datos del head
    		 JsonObject headData = data.get("head").asObject();
    		 JsonArray workBooks = headData.get("workBooks").asArray();
    		 ArrayList<WorkBookInfo> arrWorkBookInfo = new ArrayList<WorkBookInfo>();
   
    		 WorkBookInfo workBookInfo;
    		 for (JsonValue workBookItem : workBooks)
			 {
				workBookInfo = new WorkBookInfo();
				workBookInfo.fileName = workBookItem.asObject().get("fileName").asString();
				//ruta del excel
				workBookInfo.path = Conf.spreadsheetPath+"/"+clientId+"/"+groupappId+"/"+workBookItem.asObject().get("fileName").asString();
	           System.out.println("se busca el archivo... " + workBookInfo.path);
				workBookInfo.sheetsNames = GeneralUtil.getArrayString(workBookItem.asObject().get("sheetsNames").asArray());
				
			    arrWorkBookInfo.add(workBookInfo);
    			 
			 }
    		 
    		 ExcelManager excelManager = new ExcelManager(arrWorkBookInfo, data);
    		 excelManager.calculate();
    
    		 return  excelManager.resultData.toString(); 
    		}
    		catch(Exception error)
    		{
    			error.printStackTrace();
    			
    			return "se produjo un error";
    			
    		}
    		 
    		
    		 
    		 
    		    
    		});
    	
    }
    
		
	
	
	

	

}
