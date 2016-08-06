/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import classes.CellData;
import classes.WorkBookInfo;
import classes.WorkBookObject;
import customFunction.DAVERAGE;

/**
 * 
 * @author rodrigo
 */

public class ExcelBase
{

	//protected FileInputStream fis;

	//contiene la informacion de objetos de todos los libros
	private ArrayList<WorkBookObject> arrWorkBookObject =new ArrayList<WorkBookObject>();
	
	//protected Map<String, Sheet> sheets = new HashMap<String, Sheet>();
//	private FormulaEvaluator evaluator;
	/**
	 * 
	 * son las celdas que ya se han modificado
	 * se necesitan almacenar para poder notificar los
	 * cambios en modificaciones posteriores
	 */

	protected ExcelBase(ArrayList<WorkBookInfo> arrWorkBookInfo) throws IOException, EncryptedDocumentException, InvalidFormatException
	{

		// System.setProperty ("org.apache.poi.util.POILogger",
		// "org.apache.poi.util.SystemOutLogger");
		// System.setProperty ("poi.log.level", POILogger.INFO + "");
		// evaluator.setDebugEvaluationOutputForNextEval(true);
		//Collection<String> unsupportedFuncs = WorkbookEvaluator.getNotSupportedFunctionNames ();
		//agrego funciones personalizadas
		WorkbookEvaluator.registerFunction("DAVERAGE", new DAVERAGE());
		
		FileInputStream fis;
		WorkBookObject workBookObject;
		Map<String,FormulaEvaluator> workbooksEvaluator = new HashMap<String, FormulaEvaluator>();
		
		for (WorkBookInfo workBookInfoItem : arrWorkBookInfo)
		{
				 
			 workBookObject = new WorkBookObject();
			 workBookObject.workBookInfo = workBookInfoItem;
		
			 fis = new FileInputStream(workBookInfoItem.path);
			 workBookObject.workBook = WorkbookFactory.create(fis);
			
			 //recorro las hojas del libro
			for (String sheetNameItem : workBookInfoItem.sheetsNames)
			{
				workBookObject.sheets.put(sheetNameItem,workBookObject.workBook.getSheet(sheetNameItem)); 
			}
			
			workBookObject.evaluator = workBookObject.workBook.getCreationHelper().createFormulaEvaluator();
			
			//mapa de referencias de evaluator
			workbooksEvaluator.put(workBookInfoItem.fileName, workBookObject.evaluator);
			
			arrWorkBookObject.add(workBookObject);
			
		}
       
		//asigno referencias todos a todos
		for (WorkBookObject workBookObjectItem : arrWorkBookObject)
		{
			workBookObjectItem.evaluator.setupReferencedWorkbooks(workbooksEvaluator);
		}
	        
	}
	
		protected void setCellValue(String cellName, String value,boolean notifyUpdate)
		{
			
			boolean isNumeric = isNumeric(value);
			CellData cellData = getCellDataByCellName(cellName);

			if(cellData.cell == null)
				cellData.cell = createCellBlank(cellName);
			//si es una formula cambio el tipo de celda para setear un numero
			//es el caso de las celdas mixtas que pueden ser formula o un valor ingresado por el usuario
			if (cellData.cell.getCellType() == Cell.CELL_TYPE_FORMULA)
			{
				
			//	System.out.println("cell : " + cellName);
			//	System.out.println("value : " + cellName);
				
				if(isNumeric)
				{
					cellData.cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				}
				else
				{
					cellData.cell.setCellType(Cell.CELL_TYPE_STRING);	
				}
				
			}
		
			
			if(isNumeric)
			{
				cellData.cell.setCellValue(Double.parseDouble(value));
			}
			else
			{
				
				cellData.cell.setCellValue(value);
			}
		
				if(notifyUpdate)
				{
					this.notifyUpdateCell(cellData);
					
				}
					
		}
		
	protected String getFormatString(String cellName)
	{

		String result = "";

		CellData cellData = getCellDataByCellName(cellName);

		//es nulo si la celda nunca se ha usado
		if(cellData.cell != null)
		result = cellData.cell.getCellStyle().getDataFormatString();

		return result;

	}




	private CellData getCellValue(CellData cellData) 
	{

		//CellData cellData = new CellData();
	
		if(cellData.cell != null)
		{
		// evaluator.evaluateFormulaCell(cell);

		int cellTypeEvaluator = cellData.workBookObject.evaluator.evaluateFormulaCell(cellData.cell);

		int cellType = cellTypeEvaluator > -1 ? cellTypeEvaluator : cellData.cell
				.getCellType();

		switch (cellType)
		{

		case Cell.CELL_TYPE_BLANK:

			// cellData.put("type",HSSFCell.CELL_TYPE_BLANK);
			cellData.value = cellData.cell.getStringCellValue();
			
			break;

		case Cell.CELL_TYPE_NUMERIC:

			// cellData.put("type",HSSFCell.CELL_TYPE_NUMERIC);
			cellData.value = Double.toString(cellData.cell.getNumericCellValue());

			break;

		case Cell.CELL_TYPE_STRING:

			// cellData.put("type",HSSFCell.CELL_TYPE_STRING);
			cellData.value = cellData.cell.getStringCellValue();

			break;

		case Cell.CELL_TYPE_ERROR:

			cellData.error = FormulaError.forInt(cellData.cell.getErrorCellValue()).name();
			cellData.value = "ERROR";

			break;

		case Cell.CELL_TYPE_FORMULA:

			// cellData.put("type", HSSFCell.CELL_TYPE_ERROR);
			cellData.value = cellData.cell.getCellFormula();

			break;

		default:

			// cellData.put("type",HSSFCell.CELL_TYPE_STRING);
			cellData.value = cellData.cell.getCellStyle().getDataFormatString();

		}

		// si la celda es pocentaje multiplico por 100

		if (cellData.cell.getCellStyle().getDataFormatString().contains("%")
				&& cellType == Cell.CELL_TYPE_NUMERIC)
		{

			Double perceValue = Double.parseDouble(cellData.value) * 100;
			cellData.value = perceValue.toString();

		}

		}
		else
		{
			cellData.value = null;
			
		}
	
		
		return cellData;
	}


	protected CellData getCellData(String cellName) 
	{

		CellData cellData = getCellDataByCellName(cellName);

		return getCellValue(cellData);
		
	}
	
	
	/*
	protected JSONObject getCellValueJson(String cellCoordinate) 
	{

		CellValue cellData = getCellValue(cellCoordinate);
		
		
		JSONObject json = new JSONObject();
		
		json.computeIfPresent("value", remappingFunction)
	
		
		return json;
		
	
	}

*/
	
	
	/**
	 * 
	 * imprime valores de celdas por pantalla
	 * 
	 */
	protected void printCell(String cellName)
	{
		
		System.out.println(cellName+" "+ getCellData(cellName).value);
		
	}
	
	/*
	protected JSONObject getCellJSONValue(String cellCordinate) 
	{

		JSONObject cellJson = new JSONObject();

		CellValue cellData = getCellValue(cellCordinate);
		
		
		cellJson.put("value", cellData.value);
		

		if(cellData.error != 0)
			cellJson.put("error", HSSFCell.CELL_TYPE_ERROR);
		
		
		return cellJson;
		
		
	}
	*/
	

	
	
	/**
	 * cuando la celda se evalua por segunda ves, tengo que notificar el cambio para actualizar las
	 * celdas dependientes
	 * 
	 * 
	 * @param cellName
	 */
	protected void notifyUpdateCell(String cellName)
	{

		CellData cellData = getCellDataByCellName(cellName);
		cellData.workBookObject.evaluator.notifyUpdateCell(cellData.cell);

	}
	
	protected void notifyUpdateCell(CellData cellData)
	{

		cellData.workBookObject.evaluator.notifyUpdateCell(cellData.cell);
	}
	
	
	/**
	 * 
	 * elimina todo el cache de calculos, lo calculos
	 * se tendran que reacer luego de esto.
	 */
	/*
	protected void clearAllCachedResultValues()
	{
		
		evaluator.clearAllCachedResultValues();
		
	}
*/
	// --------------------------------------------------------------------------------------------
	// funciones para la obtencion de datos, aqui se obtienen los datos de las planillas luego de ser seteados
	//todos las valores enviados por el cliente

	/**
	 * devuelve el workBookObject segun el nombre del libro
	 * si no lo encuentra devuelve null
	 * @param name
	 * @return
	 */
	private WorkBookObject getWorkBookObjectByName(String name)
	{
		WorkBookObject result = null;
		for (WorkBookObject workBookObjectItem : arrWorkBookObject)
		{
			if(workBookObjectItem.workBookInfo.fileName.equals(name))
			{
				result = workBookObjectItem;
				break;
			}
		}
		
		return result;
	}
	
	
	
	
	/**
	 * devuelve cellData segun el CellName, este debe tener un formato
	 * Libro!Sheet!CellRef donde Hoja1 es el nombre de la hoja y a1 es el nombre de la celda
	 * @return
	 */
	private CellData getCellDataByCellName(String cellName)
	{
		
		CellData cellData = new CellData();
	
		String[] cellNameSplit = cellName.split("!");
		
		WorkBookObject workBookObject = getWorkBookObjectByName(cellNameSplit[0]);
		cellData.workBookObject = workBookObject;
		cellData.sheet =  workBookObject.sheets.get(cellNameSplit[1]);
		cellData.cellRef = cellNameSplit[2];
		
		//obtengo objeto cell
		CellReference cellReference = new CellReference(cellData.cellRef);
		Row row = cellData.sheet.getRow(cellReference.getRow());

		//la fila puede no existir
		if(row != null)
		{
			cellData.cell = row.getCell(cellReference.getCol());
			
		}
		
	  return cellData;
	
	}
	
	
	
	
	protected Cell createCellBlank(String cellName)
	{
		System.out.println("se crea la celda : " + cellName);

		CellData cellData = getCellDataByCellName(cellName);
		CellReference cellReference = new CellReference(cellData.cellRef);
		Row row = cellData.sheet.getRow(cellReference.getRow());

		//la fila puede no existir
		if(row == null)
		{
			row = cellData.sheet.createRow(cellReference.getRow());	
		}
		
		Cell cell = row.createCell(cellReference.getCol());
		
		return cell;
		
	}

	protected boolean isNumeric(String str)
	{
		
		return str.matches("[-+]?\\d*\\.?\\d+");

	}
	
	/**
	 * devuelve un array con todas las celdas que contienen formula
	 * 
	 */
	public ArrayList<String> getAllFormulaCell(String fileName,String sheetName)
	{
		ArrayList<String> cells = new ArrayList<>();
		
		WorkBookObject workBookObject = getWorkBookObjectByName(fileName);
		Sheet sheet = workBookObject.sheets.get(sheetName);
		
		CellReference cellReference;
		
		    for (Row r : sheet) 
		    {
		        for (Cell c : r) 
		        {
		         
		            	cellReference = new CellReference(c);
		            	cells.add(fileName+"!"+sheetName+"!"+cellReference.formatAsString());
		            
		        }
		    }
		
		    return cells;
		    
	}
	
	

}
