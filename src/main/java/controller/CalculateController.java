package controller;

import static spark.Spark.after;
import static spark.Spark.before;
import static spark.Spark.post;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    	 post("/calculate/:client_id/:groupapp_id/:filename/:sheetname", (request, response) -> 
    	 {
    		
    		 System.out.println(request.headers("token"));
    		 JSONParser parser = new JSONParser();
    		 
    		 
    		 String clientId = request.params(":client_id");
    		 String groupappId = request.params(":groupapp_Id");
    		 String fileName = request.params(":filename");
    		 String sheetName = request.params(":sheetname");
    		 JSONObject data =  (JSONObject)parser.parse(request.body());
    		 
    		 
    		 //---creo el path
    		 String path = Conf.spreadsheetPath+"/"+clientId+"/"+groupappId+"/"+fileName;
    		 
    		 
    		 ExcelManager excelManager = new ExcelManager(path, fileName, sheetName, data);
    		 excelManager.calculate();
    
    		  return  excelManager.resultData.toJSONString();
    		 
    		 
    		    
    		});
    	
    }
    
		
	
	
	

	

}
