package manager;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import classes.CellValue;

@SuppressWarnings("unchecked")
public class ExcelManager extends ExcelBase
{

	private JSONObject sentData;
	public JSONObject resultData = new JSONObject();



	private int processStadge = 0;

	public ExcelManager(String path, String fileName, String sheetName,
			JSONObject sentData) throws FileNotFoundException, IOException,
					EncryptedDocumentException, InvalidFormatException
	{

		super(path, fileName, sheetName);

		this.sentData = sentData;

	}

	
	/**
	 * 
	 * setea todas las celdas variables enviadas por el cliente
	 * 
	 */
	private void setCalculableVar(JSONObject calculableVar,boolean notifyUpdateAll) 
	{

		for (Object cellKey : calculableVar.keySet())
		{

			String valueCell = calculableVar.get(cellKey).toString();

			setCalculableVar(cellKey.toString(),valueCell,notifyUpdateAll);
			
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
					calculateAux,notifyUpdateAll);

		} 
		else if(isNumeric(valueCell))
		{

			calculateAux = Double.parseDouble(valueCell);
			
			this.setCellValue(cellName,
					calculateAux,notifyUpdateAll);

		}
		else
		{
			//es un string, en un futuro se agregara soporte de fechas
			
			this.setCellValue(cellName,
					valueCell,notifyUpdateAll);
		}

	}

	
	
	

	private JSONObject getResultDataTable() 
	{

		JSONObject calculateResult = new JSONObject();
		
		JSONObject dataTable = (JSONObject) sentData.get("dataTable");
		
		String evaluateFormula = (String)dataTable.get("evaluateFormula");
		String cellInput0 = (String)dataTable.get("cellInput0");
		String cellInput1 = (String)dataTable.get("cellInput1");
		
		JSONArray calculableVar = (JSONArray)dataTable.get("calculableVar");
		
		//String evaluateFormula = (String)dataTable.get("evaluateFormula");
		
		
		//notifyUpdateCell(cellName);
		
		
		for (Object calculableVarItem : calculableVar)
		{
			
			JSONObject calculableVarItemObj = (JSONObject) calculableVarItem;
			
			
			
			if(calculableVarItemObj.containsKey("input0"))
			{
			
				
				setCalculableVar(cellInput0,
						calculableVarItemObj.get("input0").toString() , 
						true);
			}
			
			
			if(calculableVarItemObj.containsKey("input1"))
			{
			
				setCalculableVar(cellInput1,
						calculableVarItemObj.get("input1").toString(), 
						true);
			}
			
			
			calculateResult.put((String)calculableVarItemObj.get("setResult")
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
	private JSONArray getResultDinamicComponent() 
	{

		JSONArray calculateResult = new JSONArray();

		JSONObject nInstances = (JSONObject) sentData.get("nInstances");

		JSONArray calculableVar = (JSONArray) nInstances.get("calculableVar");

		JSONArray requestResult = (JSONArray) nInstances.get("requestResult");

		JSONObject getResult;
		
		Integer indicateRowMap = 0;
		
		boolean isContentMapResultColumn = nInstances.containsKey("mapResultColumn");
		
		
		// recorro todas las celdas que se requieren obtener
		for (Object calculableVarItem : calculableVar)
		{

			setCalculableVar((JSONObject) calculableVarItem,true);

			
			
			getResult = getResult(requestResult);

			
			
			// si contiene un mapa de asignacion a columnas
			if (isContentMapResultColumn)
			{
				
				JSONObject mapResultColumn = (JSONObject) nInstances.get("mapResultColumn");
			
				//aumento el numero de fila
				indicateRowMap++;
				
				//asigno resultado a las columnas
				for (Object mapResultColumnKey : mapResultColumn.keySet())
				{
					for (Object getResultKey : getResult.keySet())
					{
						//verifico si existen celdas a asignar
						if(mapResultColumn.get(mapResultColumnKey).equals(getResultKey))
						{
							
							String cellNameGen = mapResultColumnKey.toString()+indicateRowMap.toString();
									
							setCalculableVar(cellNameGen, getResult.get(getResultKey).toString(),false);
							
							continue;
							
						}
						
					}
					
				}
				

			}

			calculateResult.add(getResult);

		}
		
		
		
		

		return calculateResult;

	}
	
	
	
	private JSONObject getResultAfterNinstances()
	{
		
		JSONArray resultAfterNinstances = (JSONArray) sentData.get("resultAfterNinstances");
		
		JSONObject result = new JSONObject();
		
		for (Object resultAfterNinstancesItem : resultAfterNinstances)
		{

			result.put(resultAfterNinstancesItem, 
					getCellValue((String)resultAfterNinstancesItem).value);
			
			
		}
		
		return result;
		
	}
	

	/**
	 * recibe un array de celdas para calcular y obtener
	 * 
	 * @param requestResult
	 * @param notifyAll
	 * si es true se notifican cambios en todas las celdas
	 * @return
	 */
	private JSONObject getResult(JSONArray requestResult) 
	{

		JSONObject calculateResult = new JSONObject();

		// recorro todas las celdas que se requieren obtener
		for (Object requestResultItem : requestResult)
		{

			
			
			CellValue cellValue = this.getCellValue((String) requestResultItem);

			calculateResult.put(requestResultItem, cellValue.value);

			System.out.println(requestResultItem.toString() + " : " + cellValue.value);
			
		}

		
		return calculateResult;

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

			JSONObject calculableVar = (JSONObject) sentData
					.get("calculableVar");
			
			setCalculableVar(calculableVar,false);
			

			nextProcess();

			break;

		case 1:
			
			JSONArray requestResult = (JSONArray) sentData.get("requestResult");
			resultData.put("calculateResult", getResult(requestResult));

			nextProcess();
			break;
			
		
		
		case 2:

			if (sentData.containsKey("nInstances"))
			{
				JSONObject nInstances = new JSONObject();

				nInstances.put("calculateResult", getResultDinamicComponent());

				resultData.put("nInstances", nInstances);
				
				//ver resultAfter

			}

			
			
			break;
			
		
			
		case 3:
			
			if(sentData.containsKey("dataTable"))
			{
				
				JSONObject dataTable = new JSONObject();
				
				dataTable.put("calculateResult",getResultDataTable());
				
			
			    resultData.put("dataTable", dataTable);
			
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

	

}
