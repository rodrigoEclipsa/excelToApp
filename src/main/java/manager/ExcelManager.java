package manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

import classes.CellData;
import classes.WorkBookInfo;
import classes.WorkBookObject;
import conf.Conf;
import util.GeneralUtil;


public class ExcelManager extends ExcelBase
{

	final static Logger logger = Logger.getLogger(ExcelManager.class);
	
	private JsonObject sentData;
	public JsonObject resultData = new JsonObject();

	
	public static int JSON_NUMBER = 2;
	public static int JSON_BOOLEAN = 3;
	public static int JSON_STRING = 1;

	private int processStadge = 0;

	public ExcelManager(ArrayList<WorkBookInfo> arrWorkBookInfo,
			JsonObject sentData) throws FileNotFoundException, IOException,
					EncryptedDocumentException, InvalidFormatException
	{

		super(arrWorkBookInfo);

		this.sentData = sentData;

	}

	
	
	/**
	 * 
	 * proceso de calculo 
	 * 
	 * 1- se setean las variables 
	 * 2- se obtienen los resultados 
	 * 3- se resulve las tablas de datos
	 * 4- se resuelven los componentes dinamicos 
	 * 
	 * 
	 * 
	 * @throws Exception
	 */
	public void calculate() throws Exception
	{

		System.out.println("proceso de calculo etapa..." + processStadge);

		switch (processStadge)
		{

		case 0:

			if(sentData.get("calculableVar") != null)
			{
			JsonObject calculableVar =  sentData
					.get("calculableVar").asObject();
			
			setCalculableVar(calculableVar,false);
			
			}
			nextProcess();

			break;

		case 1:
			
			if(sentData.get("requestResult") != null)
			{
				JsonArray requestResult = sentData.get("requestResult").asArray();
			
				////////busco comodin *, sirve para calcular todas las celdas con formulas
				//y las calcula
			   if(!requestResult.isEmpty())
			   {
				
				  String[] firstElement = requestResult.get(0).asString().split("!");
				  String fileName = firstElement[0];
				  String sheetName = firstElement[1];
				  String cellName =firstElement[2];
				
				if(cellName.equals("*"))
				{
					resultData.add("calculatedResult", getResult(getAllFormulaCell(fileName, sheetName)));	
				}
				else
				{
					resultData.add("calculatedResult", getResult(requestResult));	
				}
			   
			   }
			   else
			   {
				   //si esta vacio igual genero el objeto(vacio), se requiere en otros estados
				   resultData.add("calculatedResult", getResult(requestResult));	   
			   }
			
			
			
			}
			
			nextProcess();
			
			break;
		
		case 2:

			
			if (sentData.get("nInstances") != null)
			{
				
				resultData.add("nInstances", getResultDinamicComponent());
			
			}
		
			nextProcess();
			
			break;
			
		case 3:
			
			if(sentData.get("dataTable") != null)
			{
			
				if(resultData.get("calculatedResult") == null)
				{
					JsonArray calculatedResult = new JsonArray();
					resultData.add("calculatedResult", calculatedResult);
				}
				
				getResultDataTable(resultData.get("calculatedResult").asObject());
					
				//JsonObject dataTable = new JsonObject();
				
			//	dataTable.add("calculatedResult",getResultDataTable());
				
			   // resultData.add("dataTable", dataTable);
			
			}
			
			nextProcess();
			
		 break;

		case 4:
			System.out.println("fin de proceso de calculo");
			
		break;

		default:
			
			throw new Exception("no existe el proceso");

		}

	}

	/**
	 * 
	 * este metodo se utiliza para preveer procesos que contengan partes
	 * asincronicas, y trasnformarlo en algo secuencial sincrono
	 * 
	 */
	private void nextProcess() throws Exception
	{

		processStadge++;

		calculate();
	}

	
	
	
	/**
	 * 
	 * setea todas las celdas variables enviadas por el cliente
	 * 
	 */
	private void setCalculableVar(JsonObject calculableVar,boolean notifyUpdateAll) 
	{

		for (Member cellKey : calculableVar)
		{

			String valueCell = calculableVar.get(cellKey.getName()).toString();
			setCalculableVar(cellKey.getName(),valueCell,notifyUpdateAll);
			
		}

	}
	
	
	private void setCalculableVar(String cellName,String valueCell,boolean notifyUpdateAll) 
	{

		// System.out.println("se modifica : "+ cellKey.toString());

	
		String cellExcelFormat = this.getFormatString(cellName);

		// System.out.println(cellKey.toString()+"\n");

		// System.out.println(sentCells.get(cellKey).toString()+"\n");

		// System.out.println(cellExcelFormat+"\n");

		// si la celda es pocentaje divido por 100
		
		Double calculateAux;
		
		if (cellExcelFormat.contains("%")
				&& GeneralUtil.isNumeric(valueCell))
		{

			calculateAux = Double.parseDouble(valueCell) / 100;
			this.setCellValue(cellName,
					calculateAux.toString(),notifyUpdateAll);

		} 
		else
		{

			this.setCellValue(cellName,
					valueCell,notifyUpdateAll);
		}
		

	}

	
   /**
    * 
    * @return
    * devuelve un objeto que reprecenta todos los resultados de las tablas de datos
    */
	private void getResultDataTable(JsonObject calculatedResult)
	{

		JsonArray dataTable =  sentData.get("dataTable").asArray();
		
		for (JsonValue dataTableItem : dataTable)
		{
			
		String evaluateFormula = dataTableItem.asObject().get("evaluateFormula").asString();
		String cellInput0 = dataTableItem.asObject().get("cellInput0") != null ?
				dataTableItem.asObject().get("cellInput0").asString() : null;
		String cellInput1 = dataTableItem.asObject().get("cellInput1") != null ?
				dataTableItem.asObject().get("cellInput1").asString() : null;
		
		//guardo los valores de las celdas para restaurarlo para el proximo ciclo
		CellData cellDataInput0 = null;
		CellData cellDataInput1 = null;
		if(cellInput0 != null)
		cellDataInput0 = getCellData(cellInput0);
	
		if(cellInput1 != null)
		cellDataInput1 = getCellData(cellInput1);
		
				
		JsonArray calculableVar = dataTableItem.asObject().get("calculableVar").asArray();
		
		//String evaluateFormula = (String)dataTable.get("evaluateFormula");
		
		//notifyUpdateCell(cellName);
		
		for (JsonValue calculableVarItem : calculableVar)
		{
			
			JsonObject calculableVarItemObj =  calculableVarItem.asObject();
			
			if(calculableVarItemObj.get("input0") != null)
			{
			if(!calculableVarItemObj.get("input0").isNull())
			{
			    String cellInput0Value =GeneralUtil.isNumeric(calculableVarItemObj.get("input0").toString()) ?
			    		calculableVarItemObj.get("input0").toString() : calculableVarItemObj.get("input0").asString();

               
				setCalculableVar(cellInput0,
						GeneralUtil.isVariableExcel(cellInput0Value) ?  getCellData(cellInput0Value).value :
							cellInput0Value
						,true);
				
			    
			}
			}
			
			if(calculableVarItemObj.get("input1") != null)
			{
			if(!calculableVarItemObj.get("input1").isNull())
			{
				String cellInput1Value =GeneralUtil.isNumeric(calculableVarItemObj.get("input1").toString()) ?
			    		calculableVarItemObj.get("input1").toString() : calculableVarItemObj.get("input1").asString();

				
				setCalculableVar(cellInput1,
						GeneralUtil.isVariableExcel(cellInput1Value) ?  getCellData(cellInput1Value).value :
							cellInput1Value
						,true);
			}
			}
				
			calculatedResult.add(calculableVarItemObj.get("setResult").asString()
					, getCellData(evaluateFormula).value);
			
			
		}
		
		//restaurar valores de las celdas
		if(cellInput0 != null)
		setCellValue(cellInput0, cellDataInput0.value,true);
		if(cellInput1 != null)
		setCellValue(cellInput1, cellDataInput1.value,true);
		
		}
		
	}

	
	/**
	 * 
	 * setea y obtiene los resultados de cada componente dinamico, 
	 * asigna los valores segun el mapa de asignacion "mapResultColumn"
	 * 
	 * @return
	 * @throws Exception
	 */
	private JsonArray getResultDinamicComponent() 
	{

		JsonArray resultComponents = new JsonArray();
		JsonObject resultObj;
     	JsonArray calculatedResult;

		JsonArray nInstances = sentData.get("nInstances").asArray();

		JsonArray calculableVar;
		JsonArray requestResult;
		JsonObject getResult;
		Integer indicateRowMap;
		boolean isContentMapResultColumn;
		
		for (JsonValue nInstancesItem : nInstances)
		{
		
		 calculatedResult = new JsonArray();
		
		 calculableVar = nInstancesItem.asObject().get("calculableVar").asArray();

		 requestResult =  nInstancesItem.asObject().get("requestResult").asArray();

		 indicateRowMap = 0;
		
		 isContentMapResultColumn = !nInstancesItem.asObject().get("mapResultColumn").isNull();
		
		// recorro todas las celdas que se requieren obtener
		for (JsonValue calculableVarItem : calculableVar)
		{

			setCalculableVar( calculableVarItem.asObject(),true);
			getResult = getResult(requestResult);

			// si contiene un mapa de asignacion a columnas
			if (isContentMapResultColumn)
			{
				
				JsonObject mapResultColumn = nInstancesItem.asObject().get("mapResultColumn").asObject();
			
				//aumento el numero de fila
				indicateRowMap++;
				
				
				//asigno resultado a las columnas
				for (Member mapResultColumnKey : mapResultColumn)
				{
					for (Member getResultKey : getResult)
					{
						
						//verifico si existen celdas a asignar
						if(mapResultColumn.get(mapResultColumnKey.getName()).asString().equals(getResultKey.getName()))
						{
							
							String cellNameGen = mapResultColumnKey.getName()+indicateRowMap.toString();		
							setCellValue(cellNameGen, getResult.get(getResultKey.getName()).toString(),false);
							
							continue;
							
						}
						
					}
					
				}
				

			}

			calculatedResult.add(getResult);

		}

		resultObj = new JsonObject(); 
		resultObj.add("calculatedResult", calculatedResult);
		
		//resulevo los calculos luego de que se hallan mapeado los datos
        JsonArray resultAfterNinstances = nInstancesItem.asObject().get("resultAfterNinstances").asArray();
		
		JsonObject result = new JsonObject();
		CellData cellData = null;
		for (JsonValue resultAfterNinstancesItem : resultAfterNinstances)
		{
			cellData = getCellData(resultAfterNinstancesItem.asString());
			setValidTypeJson(resultAfterNinstancesItem.asString(),result,cellData.value);
			
		}
		
		resultObj.add("resultAfterNinstances", result);
		resultComponents.add(resultObj);
		
		}
		
		return resultComponents;

	}
	
	
	
	
	

	/**
	 * recibe un array de celdas para calcular y obtener
	 * 
	 * @param requestResult
	 *
	 * si es true se notifican cambios en todas las celdas
	 * @return
	 */
	private JsonObject getResult(JsonArray requestResult) 
	{

		JsonObject calculatedResult = new JsonObject();
		CellData cellData;
		// recorro todas las celdas que se requieren obtener
		for (JsonValue requestResultItem : requestResult)
		{
			//verifico si el valor es un rango o una celda..
			String[] cellNameSplit = requestResultItem.asString().split(Conf.splitStr);
			String workBookName = cellNameSplit[0];
			String sheetName = cellNameSplit[1];
			String cellName = cellNameSplit[2];
			
			String[] cellNameRange = cellName.split(":");
			//si es un rango lo proceso como tal
			if(cellNameRange.length > 1)
			{
				
				WorkBookObject workBookObject = getWorkBookObjectByName(workBookName);
				Sheet sheet = workBookObject.sheets.get(sheetName);
				
				CellReference cellReference;
				CellRangeAddress range = CellRangeAddress.valueOf(cellName);
				JsonArray calculatedResultRange = new JsonArray();
				for (int i=range.getFirstRow(); i<=range.getLastRow(); i++)
				{
					 Row r = sheet.getRow(i);
					 
					 for (Cell c : r) 
				     {
						 cellReference = new CellReference(c);
						 //logger.debug(cellReference.formatAsString());
						 String cellNameOrigin = workBookName+Conf.splitStr+sheetName+Conf.splitStr+cellReference.formatAsString();
						 cellData = this.getCellData(cellNameOrigin);
						 setValidTypeJsonArray(requestResultItem.asString(),calculatedResultRange,cellData.value);
						 
				     }
					 
				}
				
				calculatedResult.add(cellName, calculatedResultRange);
				
			}
			else
			{
			
			
			cellData = this.getCellData(requestResultItem.asString());
			setValidTypeJson(requestResultItem.asString(),calculatedResult,cellData.value);
			
			//System.out.println(requestResultItem.toString() + " : " + cellValue.value);
			
			}
			
		}

		return calculatedResult;
	}
	
	/**
	 * asigna valores JSON validos a un jsonObject
	 * @param cellRef
	 * @param jsonObject
	 * @param value
	 */
	private void setValidTypeJson(String cellRef,JsonObject jsonObject, String value)
	{
		int jsonValidType = jsonValidType(value);
		
		if(jsonValidType == JSON_STRING)
		{
			jsonObject.set(cellRef, value);
		}
		else if(jsonValidType == JSON_NUMBER)
		{
			jsonObject.set(cellRef, Double.parseDouble(value));
		}
		else if(jsonValidType == JSON_BOOLEAN)
		{
			jsonObject.set(cellRef, Boolean.getBoolean(value));
		}
		else
		{
			jsonObject.set(cellRef, value);
		}
		
	}
	
	/**
	 * asigna valores JSON validos a un jsonArray
	 * @param cellRef
	 * @param jsonArray
	 * @param value
	 */
	private void setValidTypeJsonArray(String cellRef,JsonArray jsonArray, String value)
	{
		int jsonValidType = jsonValidType(value);
		
		if(jsonValidType == JSON_STRING)
		{
			
			jsonArray.add(value);
		}
		else if(jsonValidType == JSON_NUMBER)
		{
			jsonArray.add(Double.parseDouble(value));
		}
		else if(jsonValidType == JSON_BOOLEAN)
		{
			jsonArray.add(Boolean.getBoolean(value));
		}
		else
		{
			jsonArray.add(value);
		}
		
	}
	
	private JsonObject getResult(ArrayList<String> requestResult) 
	{

		JsonObject calculatedResult = new JsonObject();

		// recorro todas las celdas que se requieren obtener
		for (String requestResultItem : requestResult)
		{

			CellData cellData = this.getCellData(requestResultItem);
			
			setValidTypeJson(requestResultItem,calculatedResult,cellData.value);

			//System.out.println(requestResultItem.toString() + " : " + cellValue.value);
			
		}

		return calculatedResult;
	}


	/**
	 * devuelve un entero que indica que tipo de dato json valido, por defecto es JSON_STRING
	 * 
	 * 
	 * 
	 * @param value
	 * @return
	 * 
	 * 1- String
	 * 2- Number
	 * 3- boolean
	 * 
	 */
	
	private int jsonValidType(String value)
	{
		
		if(value == null)
		{
			return JSON_STRING;
		}
		
		if(GeneralUtil.isNumeric(value))
		{
			return JSON_NUMBER;
		}
		
		if(value.equals("true") || value.equals("false"))
		{
			return JSON_BOOLEAN;
		}
		
		return JSON_STRING;
		
	}
	

}
