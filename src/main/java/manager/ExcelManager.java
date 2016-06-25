package manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonObject.Member;
import com.eclipsesource.json.JsonValue;

import classes.CellData;
import classes.WorkBookInfo;

@SuppressWarnings("unchecked")
public class ExcelManager extends ExcelBase
{

	private JsonObject sentData;
	public JsonObject resultData = new JsonObject();


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

			JsonObject calculableVar =  sentData
					.get("calculableVar").asObject();
			
			setCalculableVar(calculableVar,false);
			

			nextProcess();

			break;

		case 1:
			
			if(sentData.get("requestResult") != null)
			{
				
			JsonArray requestResult = sentData.get("requestResult").asArray();
			resultData.add("calculateResult", getResult(requestResult));

			nextProcess();
			
			}
			
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
				
				JsonObject dataTable = new JsonObject();
				
				dataTable.add("calculateResult",getResultDataTable());
				
			    resultData.add("dataTable", dataTable);
			
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
				&& isNumeric(valueCell))
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

	
	
	

	private JsonObject getResultDataTable() 
	{

		JsonObject calculateResult = new JsonObject();
		
		JsonObject dataTable =  sentData.get("dataTable").asObject();
		
		String evaluateFormula = dataTable.get("evaluateFormula").asString();
		String cellInput0 = dataTable.get("cellInput0").asString();
		String cellInput1 = dataTable.get("cellInput1").asString();
		
		JsonArray calculableVar = dataTable.get("calculableVar").asArray();
		
		//String evaluateFormula = (String)dataTable.get("evaluateFormula");
		
		
		//notifyUpdateCell(cellName);
		
		
		for (JsonValue calculableVarItem : calculableVar)
		{
			
			JsonObject calculableVarItemObj =  calculableVarItem.asObject();
			
			
			
			if(!calculableVarItemObj.get("input0").isNull())
			{
			
				
				setCalculableVar(cellInput0,
						calculableVarItemObj.get("input0").toString() , 
						true);
			}
			
			
			if(!calculableVarItemObj.get("input1").isNull())
			{
			
				setCalculableVar(cellInput1,
						calculableVarItemObj.get("input1").toString(), 
						true);
			}
			
			
			calculateResult.add(calculableVarItemObj.get("setResult").asString()
					, getCellValue(evaluateFormula).value);
			
			
		}
		
		
		
		return calculateResult;
		
		
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
     	JsonArray calculateResult;

		JsonArray nInstances = sentData.get("nInstances").asArray();

		JsonArray calculableVar;
		JsonArray requestResult;
		JsonObject getResult;
		Integer indicateRowMap;
		boolean isContentMapResultColumn;
		
		for (JsonValue nInstancesItem : nInstances)
		{
		
		 calculateResult = new JsonArray();
		
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
							setCellValue(cellNameGen, getResult.get(getResultKey.getName()).asString(),false);
							
							continue;
							
						}
						
					}
					
				}
				

			}

			calculateResult.add(getResult);

		}

		resultObj = new JsonObject(); 
		resultObj.add("calculateResult", calculateResult);
		
		//resulevo los calculos luego de que se hallan mapeado los datos
        JsonArray resultAfterNinstances = nInstancesItem.asObject().get("resultAfterNinstances").asArray();
		
		JsonObject result = new JsonObject();
		
		for (JsonValue resultAfterNinstancesItem : resultAfterNinstances)
		{
			result.add(resultAfterNinstancesItem.asString(), 
					getCellValue(resultAfterNinstancesItem.asString()).value);
		
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
	 * @param notifyAll
	 * si es true se notifican cambios en todas las celdas
	 * @return
	 */
	private JsonObject getResult(JsonArray requestResult) 
	{

		JsonObject calculateResult = new JsonObject();

		// recorro todas las celdas que se requieren obtener
		for (JsonValue requestResultItem : requestResult)
		{

			
			
			CellData cellValue = this.getCellValue(requestResultItem.asString());

			calculateResult.set(requestResultItem.asString(), cellValue.value);

			System.out.println(requestResultItem.toString() + " : " + cellValue.value);
			
		}

		
		return calculateResult;

	}

	
	

}
