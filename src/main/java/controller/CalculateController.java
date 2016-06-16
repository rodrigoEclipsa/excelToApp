package controller;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.post;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import conf.Conf;
import manager.ExcelManager;



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
    		
    		
    		 //por ahora contemplo 1 solo libro
    		 JsonObject workBook = headData.get("workBooks").asArray().get(0).asObject();
    		 
    		
    		 
    		 String fileName = workBook.get("fileName").asString();
    		 String[] sheetNames = workBook.get("sheetNames").asString().split(",");
    		 
    		 
    		 //---creo el path
    		 String path = Conf.spreadsheetPath+"/"+clientId+"/"+groupappId+"/"+fileName;
    		 
    		 
    		 ExcelManager excelManager = new ExcelManager(path, fileName, sheetNames, data);
    		 excelManager.calculate();
    
    		 
    		
    		 
    		  return  excelManager.resultData.toString();
    		 
    		 
    		    
    		});
    	
    }
    
		
	
	
	

	

}
