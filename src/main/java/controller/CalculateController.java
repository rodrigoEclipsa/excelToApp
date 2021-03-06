package controller;

import static spark.Spark.post;

import java.util.ArrayList;

import org.apache.log4j.Logger;

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
	final static Logger logger = Logger.getLogger(CalculateController.class);
	
	public CalculateController()
	{
		
	
		
	//--------armo el api
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
    	 post("/calculate/:clientId/:groupappId", (request, response) -> 
    	 {
    		 
    		try
    		{
          
             //---------------------------
    		 String clientId = request.params(":clientId");
    		 String groupappId = request.params(":groupappId");
    		
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
				 logger.debug("se busca el archivo... " + workBookInfo.path);
				workBookInfo.sheetsNames = GeneralUtil.getArrayString(workBookItem.asObject().get("sheetsNames").asArray());
				
			    arrWorkBookInfo.add(workBookInfo);
    			 
			 }
    		 
    		 ExcelManager excelManager = new ExcelManager(arrWorkBookInfo, data);
    		 excelManager.calculate();
    		 
    		 String jsonOutput = excelManager.resultData.toString();
    		 logger.debug("json output :\n " + jsonOutput);
             
    		 return  jsonOutput; 
    		}
    		catch(Exception error)
    		{
    			error.printStackTrace();
    			 logger.error(error.getMessage());
    			 throw error;
    			//return "se produjo un error";
    			
    		}
    		 
    		
    		 
    		 
    		    
    		});
    	
    }
    
		
	
	
	

	

}
