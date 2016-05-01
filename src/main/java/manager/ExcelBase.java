/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import classes.CellValue;

/**
 * 
 * @author rodrigo
 */
@SuppressWarnings("unchecked")
public class ExcelBase
{

	//public FileInputStream fis;
	private Workbook wb;
	private Sheet sheet;
	private FormulaEvaluator evaluator;

	/**
	 * 
	 * son las celdas que ya se han modificado
	 * se necesitan almacenar para poder notificar los
	 * cambios en modificaciones posteriores
	 */
	private Map<String,Boolean> changedCells;


    


	public ExcelBase(String path, String fileName, String sheetName) throws IOException, EncryptedDocumentException, InvalidFormatException
	{

		// System.setProperty ("org.apache.poi.util.POILogger",
		// "org.apache.poi.util.SystemOutLogger");
		// System.setProperty ("poi.log.level", POILogger.INFO + "");
		// evaluator.setDebugEvaluationOutputForNextEval(true);

	
       
	
		wb = WorkbookFactory.create(new File(path));
		
		//fis = new FileInputStream(url);
		
		//wb = new HSSFWorkbook(fis); // or new XSSFWorkbook("/somepath/test.xls")
		
		//WorkbookEvaluator.registerFunction ( "PEARSON", new Pearson() );
		
		
		
		
		sheet = wb.getSheet(sheetName);
		evaluator = wb.getCreationHelper().createFormulaEvaluator();
		
		
		
		
	}

	public void setCellValue(String cellName, double value)
	{

		CellReference cellReference = new CellReference(cellName);

		Row row = sheet.getRow(cellReference.getRow());

		Cell cell = row.getCell(cellReference.getCol());

		if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA)
		{
			
		//	System.out.println("cell : " + cellName);
		//	System.out.println("value : " + cellName);
			
			cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
		}
		
		
		if(changedCells.containsKey(cellName))
		{
			
			changedCells.put(cellName, true);
		}
		else
		{
			//si es la primera ves que se pide esta celda
			changedCells.put(cellName, false);
		}

		
		
		
		cell.setCellValue(value);

	}

	public String getFormatString(String cellCordinate)
	{

		String result;

		CellReference cellReference = new CellReference(cellCordinate);

		Row row = sheet.getRow(cellReference.getRow());

		Cell cell = row.getCell(cellReference.getCol());

		result = cell.getCellStyle().getDataFormatString();

		return result;

	}




	public CellValue getCellValue(Cell cell) 
	{

		
		CellValue cellValue = new CellValue();
	
		
		
	
		if(cell != null)
		{
		// evaluator.evaluateFormulaCell(cell);

		int cellTypeEvaluator = evaluator.evaluateFormulaCell(cell);

		int cellType = cellTypeEvaluator > -1 ? cellTypeEvaluator : cell
				.getCellType();

		switch (cellType)
		{

		case HSSFCell.CELL_TYPE_BLANK:

			// cellValue.put("type",HSSFCell.CELL_TYPE_BLANK);
			cellValue.value = cell.getStringCellValue();
			
			break;

		case HSSFCell.CELL_TYPE_NUMERIC:

			// cellValue.put("type",HSSFCell.CELL_TYPE_NUMERIC);
			cellValue.value = Double.toString(cell.getNumericCellValue());

			break;

		case HSSFCell.CELL_TYPE_STRING:

			// cellValue.put("type",HSSFCell.CELL_TYPE_STRING);
			cellValue.value = cell.getStringCellValue();

			break;

		case HSSFCell.CELL_TYPE_ERROR:

			cellValue.error = HSSFCell.CELL_TYPE_ERROR;
			cellValue.value = Byte.toString(cell.getErrorCellValue());

			break;

		case HSSFCell.CELL_TYPE_FORMULA:

			// cellValue.put("type", HSSFCell.CELL_TYPE_ERROR);
			cellValue.value = cell.getCellFormula();

			break;

		default:

			// cellValue.put("type",HSSFCell.CELL_TYPE_STRING);
			cellValue.value = cell.getCellStyle().getDataFormatString();
			
			

		}

		// si la celda es pocentaje multiplico por 100

		if (cell.getCellStyle().getDataFormatString().contains("%")
				&& cellType == HSSFCell.CELL_TYPE_NUMERIC)
		{

			Double perceValue = Double.parseDouble(cellValue.value) * 100;
			
			cellValue.value = perceValue.toString();

		}

		
		}
		else
		{
			
			cellValue.value = null;
			
		}
	
		
		return cellValue;
	}


	public CellValue getCellValue(String cellName) 
	{

		
		
		
		CellReference cellReference = new CellReference(cellName);

		Row row = sheet.getRow(cellReference.getRow());

		Cell cell = row.getCell(cellReference.getCol());


	

		if(changedCells.containsKey(cellName))
		{
			if(changedCells.get(cellName))
			{
				changedCells.put(cellName,false);
				notifyUpdateCell(cellName);
				
			}
			
		}
		
		
		
		return getCellValue(cell);
		
	
	}
	
	
	/*
	public JSONObject getCellValueJson(String cellCoordinate) 
	{

		CellValue cellValue = getCellValue(cellCoordinate);
		
		
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
	public void printCell(String cellName)
	{
		
		System.out.println(cellName+" "+ getCellValue(cellName).value);
		
	}
	
	/*
	public JSONObject getCellJSONValue(String cellCordinate) 
	{

		JSONObject cellJson = new JSONObject();

		CellValue cellValue = getCellValue(cellCordinate);
		
		
		cellJson.put("value", cellValue.value);
		

		if(cellValue.error != 0)
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
	public void notifyUpdateCell(String cellName)
	{

		CellReference cellReference = new CellReference(cellName);

		Row row = sheet.getRow(cellReference.getRow());

		Cell cell = row.getCell(cellReference.getCol());

		evaluator.notifyUpdateCell(cell);

		
	}
	
	
	
	public void notifyUpdateCell(Cell cell)
	{

		
		evaluator.notifyUpdateCell(cell);

		
	}
	
	
	/**
	 * 
	 * elimina todo el cache de calculos, lo calculos
	 * se tendran que reacer luego de esto.
	 */
	public void clearAllCachedResultValues()
	{
		
		evaluator.clearAllCachedResultValues();
		
	}

	// --------------------------------------------------------------------------------------------
	// funciones para la obtencion de datos, aqui se obtienen los datos de las planillas luego de ser seteados
	//todos las valores enviados por el cliente

	
	
	
	
	

	public boolean isNumeric(String s)
	{
		return s.matches("[-+]?\\d*\\.?\\d+");

	}

}
