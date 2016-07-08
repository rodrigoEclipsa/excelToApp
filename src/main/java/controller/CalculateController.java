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
    	
		
		

		before((request, response) -> {
			
			
			
		   // boolean authenticated = true;
		     
		   // if (!authenticated) {
		     //   halt(401, "You are not welcome here");
			
		    //}
			
			
		});


		after((request, response) -> {
			// response.header("Content-Encoding", "gzip");
		});
			
		
		
		
		
		
		
		
    	/**
    	 * 
    	 *  String clientData  = request.getParameter("data");
      String sheetName  = request.getParameter("sheetname");
      String flange  = request.getParameter("flange");  
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
    		 
    		 System.out.println(request.headers("token"));
    		
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
    		 
    		 
    		    
    		});
    	
    }
    
		
	
	
	

	

}
