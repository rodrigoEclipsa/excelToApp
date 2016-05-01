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
	private JSONObject resultData;

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
	 * carga una celda a cellresult
	 * 
	 * @param cellName
	 */
	private void getCellResult(String cellName)
	{
		// System.out.println(cellName);

		// ResultsentCells.put(cellName, getCellJSONValue(cellName));

	}

	/**
	 * 
	 * agrega una celda con un alias, para calcular tablas dinamicas
	 * 
	 * 
	 * @param cellName
	 * @param asCell
	 *            : alias de celda
	 */

	private void getCellResult(String cellName, String asCell)
	{

		// ResultsentCells.put(asCell, getCellJSONValue(cellName));

	}

	/**
	 * 
	 * setea todas las celdas variables enviadas por el cliente
	 * 
	 */
	private void setCalculableVar(JSONObject calculableVar) throws Exception
	{

		for (Object cellKey : calculableVar.keySet())
		{

			// System.out.println("se modifica : "+ cellKey.toString());

			String valueCell = calculableVar.get(cellKey).toString();

			String cellExcelFormat = this.getFormatString(cellKey.toString());

			// System.out.println(cellKey.toString()+"\n");

			// System.out.println(sentCells.get(cellKey).toString()+"\n");

			// System.out.println(cellExcelFormat+"\n");

			// si la celda es pocentaje divido por 100
			if (cellExcelFormat.contains("%")
					&& isNumeric(calculableVar.get(cellKey).toString()))
			{

				this.setCellValue(cellKey.toString(),
						Double.parseDouble(valueCell) / 100);

			} else
			{

				this.setCellValue(cellKey.toString(),
						Double.parseDouble(valueCell));

			}

		}

	}

	private JSONObject getResultDataTable()
	{

		JSONObject calculateResult = new JSONObject();
		
		JSONObject dataTable = (JSONObject) sentData.get("dataTable");
		
		String evaluateFormula = (String)dataTable.get("evaluateFormula");
		String input0 = (String)dataTable.get("input0");
		String input1 = (String)dataTable.get("input1");
		
		//String evaluateFormula = (String)dataTable.get("evaluateFormula");
		
		
		//notifyUpdateCell(cellName);
		
		
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
	private JSONArray getResultDinamicComponent() throws Exception
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

			setCalculableVar((JSONObject) calculableVarItem);

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
									
							setCellValue(cellNameGen, (Double)getResult.get(getResultKey));
							
							continue;
							
						}
						
					}
					
				}
				

			}

			calculateResult.add(getResult);

		}

		return calculateResult;

	}

	/**
	 * recibe un array de celdas para calcular y obtener
	 * 
	 * @param requestResult
	 * @return
	 */
	private JSONObject getResult(JSONArray requestResult) throws Exception
	{

		JSONObject calculateResult = new JSONObject();

		// recorro todas las celdas que se requieren obtener
		for (Object requestResultItem : requestResult)
		{

			CellValue cellValue = this.getCellValue((String) requestResultItem);

			calculateResult.put(requestResultItem, cellValue.value);

		}

		return calculateResult;

	}

	/**
	 * 
	 * proceso de calculo 
	 * 0- se resuelven los componentes dinamicos 
	 * 1- se resulve las tablas de datos
	 * 2- se setean las variables 
	 * 3- se obtienen los resultados 
	 * 
	 * 
	 * 
	 * 
	 * @throws Exception
	 */
	public void calculate() throws Exception
	{

		System.out.println("iniciando proceso de calculo...");

		switch (processStadge)
		{

		case 0:

			if (sentData.containsKey("nInstances"))
			{
				JSONObject nInstances = new JSONObject();

				nInstances.put("calculateResult", getResultDinamicComponent());

				resultData.put("nInstances", nInstances);

			}

			nextProcess();
			break;
			
		case 1:
			
			if(sentData.containsKey("dataTable"))
			{
			getResultDataTable();
			
			}
			nextProcess();
			
		 break;


		case 2:

			JSONObject calculableVar = (JSONObject) sentData
					.get("calculableVar");
			
			setCalculableVar(calculableVar);

			nextProcess();

			break;

		case 3:
			JSONArray requestResult = (JSONArray) sentData.get("requestResult");
			resultData.put("calculateResult", getResult(requestResult));

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

	/*
	 * private void getData_feedlootPorcentual() {
	 * 
	 * getCellResult("e11"); getCellResult("e13");
	 * 
	 * getCellResult("e17");
	 * 
	 * getCellResult("m13"); getCellResult("m15"); getCellResult("m17");
	 * getCellResult("e22");
	 * 
	 * getCellResult("g24");
	 * 
	 * getCellResult("e28"); getCellResult("g28");
	 * 
	 * getCellResult("g30");
	 * 
	 * getCellResult("g32"); getCellResult("e34"); getCellResult("m22");
	 * getCellResult("o22");
	 * 
	 * getCellResult("o24");
	 * 
	 * getCellResult("m28"); getCellResult("o28");
	 * 
	 * getCellResult("o30");
	 * 
	 * getCellResult("o34"); getCellResult("k38"); getCellResult("k41");
	 * getCellResult("k44");
	 * 
	 * getCellResult("g11");
	 * 
	 * getCellResult("c38");
	 * 
	 * getCellResult("c41"); getCellResult("c44");
	 * 
	 * getCellResult("o22");
	 * 
	 * getCellResult("e26");
	 * 
	 * getCellResult("g26");
	 * 
	 * getCellResult("b1");
	 * 
	 * getCellResult("b1");
	 * 
	 * //getCellResult("k48"); getCellResult("k50"); getCellResult("k52");
	 * getCellResult("k54");
	 * 
	 * getCellResult("n48"); getCellResult("n50"); getCellResult("n52");
	 * getCellResult("n54");
	 * 
	 * 
	 * getCellResult("t81"); getCellResult("t82"); getCellResult("t83");
	 * getCellResult("t85"); getCellResult("t86"); getCellResult("t87");
	 * 
	 * 
	 * 
	 * 
	 * 
	 * // ------------------------------------------------------resuelvo //
	 * valores de tabla dinamica // ---------------------------------------tabla
	 * 1
	 * 
	 * // ------------------320
	 * 
	 * setCellValue("e9", Double.parseDouble(sentCells.get("t69").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("u68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "u69");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("v68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "v69");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("w68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "w69");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("x68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "x69");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("y68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "y69");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("z68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "z69");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("aa68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "aa69");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("ab68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "ab69");
	 * 
	 * // --------------340
	 * 
	 * setCellValue("e9", Double.parseDouble(sentCells.get("t70").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("u68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "u70");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("v68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "v70");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("w68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "w70");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("x68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "x70");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("y68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "y70");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("z68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "z70");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("aa68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "aa70");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("ab68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "ab70");
	 * 
	 * // --------------360
	 * 
	 * setCellValue("e9", Double.parseDouble(sentCells.get("t71").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("u68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "u71");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("v68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "v71");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("w68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "w71");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("x68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "x71");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("y68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "y71");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("z68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "z71");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("aa68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "aa71");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("ab68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "ab71");
	 * 
	 * // --------------380
	 * 
	 * setCellValue("e9", Double.parseDouble(sentCells.get("t72").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("u68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "u72");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("v68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "v72");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("w68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "w72");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("x68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "x72");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("y68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "y72");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("z68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "z72");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("aa68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "aa72");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("ab68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "ab72");
	 * 
	 * // --------------400
	 * 
	 * setCellValue("e9", Double.parseDouble(sentCells.get("t73").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("u68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "u73");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("v68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "v73");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("w68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "w73");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("x68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "x73");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("y68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "y73");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("z68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "z73");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("aa68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "aa73");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("ab68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "ab73");
	 * 
	 * // --------------420
	 * 
	 * setCellValue("e9", Double.parseDouble(sentCells.get("t74").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("u68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "u74");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("v68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "v74");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("w68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "w74");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("x68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "x74");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("y68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "y74");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("z68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "z74");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("aa68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "aa74");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("ab68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "ab74");
	 * 
	 * // --------------440
	 * 
	 * setCellValue("e9", Double.parseDouble(sentCells.get("t75").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("u68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "u75");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("v68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "v75");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("w68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "w75");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("x68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "x75");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("y68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "y75");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("z68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "z75");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("aa68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "aa75");
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("ab68").toString()));
	 * notifyUpdateCell("e7"); getCellResult("t68", "ab75");
	 * 
	 * // restableco valores cambiados para calcular la segunda tabla
	 * 
	 * setCellValue("e7", Double.parseDouble(sentCells.get("e7").toString()));
	 * notifyUpdateCell("e7"); setCellValue("e9",
	 * Double.parseDouble(sentCells.get("e9").toString()));
	 * notifyUpdateCell("e9");
	 * 
	 * // ---------------------------------------------------------------tabla
	 * // 2
	 * 
	 * // ------------------------------------- //System.out.print("averrr : " +
	 * getCellValue("t81").get("value"));
	 * 
	 * setCellValue("m30", Double.parseDouble(getCellValue("t81").value));
	 * notifyUpdateCell("m30");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("u80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "u81");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("v80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "v81");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("w80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "w81");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("x80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "x81");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("y80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "y81");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("z80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "z81");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("aa80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "aa81");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("ab80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "ab81");
	 * 
	 * // --------------------------
	 * 
	 * setCellValue("m30", Double.parseDouble(getCellValue("t82").value));
	 * notifyUpdateCell("m30");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("u80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "u82");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("v80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "v82");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("w80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "w82");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("x80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "x82");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("y80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "y82");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("z80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "z82");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("aa80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "aa82");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("ab80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "ab82");
	 * 
	 * // --------------------------
	 * 
	 * setCellValue("m30", Double.parseDouble(getCellValue("t83").value));
	 * notifyUpdateCell("m30");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("u80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "u83");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("v80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "v83");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("w80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "w83");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("x80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "x83");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("y80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "y83");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("z80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "z83");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("aa80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "aa83");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("ab80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "ab83");
	 * 
	 * // --------------------------
	 * 
	 * setCellValue("m30", Double.parseDouble(getCellValue("t84").value));
	 * notifyUpdateCell("m30");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("u80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "u84");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("v80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "v84");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("w80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "w84");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("x80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "x84");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("y80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "y84");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("z80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "z84");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("aa80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "aa84");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("ab80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "ab84");
	 * 
	 * // --------------------------
	 * 
	 * setCellValue("m30", Double.parseDouble(getCellValue("t85").value));
	 * notifyUpdateCell("m30");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("u80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "u85");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("v80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "v85");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("w80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "w85");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("x80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "x85");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("y80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "y85");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("z80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "z85");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("aa80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "aa85");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("ab80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "ab85");
	 * 
	 * // --------------------------
	 * 
	 * setCellValue("m30", Double.parseDouble(getCellValue("t86").value));
	 * notifyUpdateCell("m30");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("u80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "u86");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("v80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "v86");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("w80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "w86");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("x80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "x86");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("y80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "y86");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("z80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "z86");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("aa80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "aa86");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("ab80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "ab86");
	 * 
	 * // --------------------------
	 * 
	 * setCellValue("m30", Double.parseDouble(getCellValue("t87").value));
	 * notifyUpdateCell("m30");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("u80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "u87");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("v80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "v87");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("w80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "w87");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("x80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "x87");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("y80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "y87");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("z80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "z87");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("aa80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "aa87");
	 * 
	 * setCellValue("e5", Double.parseDouble(sentCells.get("ab80").toString()));
	 * notifyUpdateCell("e5"); getCellResult("t80", "ab87");
	 * 
	 * // ********************************************************
	 * 
	 * // -----------------------------------------------------------
	 * 
	 * }
	 * 
	 */

}
