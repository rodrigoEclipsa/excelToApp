/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;

import classes.CellData;

/**
 * 
 * @author rodrigo
 */
@SuppressWarnings("unchecked")
public class ExcelBase
{

	//protected FileInputStream fis;
	private Workbook wb;
	
	
	protected Map<String, Sheet> sheets = new HashMap<String, Sheet>();
	
	private FormulaEvaluator evaluator;

	/**
	 * 
	 * son las celdas que ya se han modificado
	 * se necesitan almacenar para poder notificar los
	 * cambios en modificaciones posteriores
	 */
//	private Map<String,Boolean> changedCells;


    


	protected ExcelBase(String path, String fileName, String[] sheetNames) throws IOException, EncryptedDocumentException, InvalidFormatException
	{

		// System.setProperty ("org.apache.poi.util.POILogger",
		// "org.apache.poi.util.SystemOutLogger");
		// System.setProperty ("poi.log.level", POILogger.INFO + "");
		// evaluator.setDebugEvaluationOutputForNextEval(true);

	
       
		FileInputStream fis = new FileInputStream(path);
	
		wb = WorkbookFactory.create(fis);
		
	//	wb = WorkbookFactory.create(new File(path));
		
		
		
		//wb = new HSSFWorkbook(fis); // or new XSSFWorkbook("/somepath/test.xls")
		
		//WorkbookEvaluator.registerFunction ( "PEARSON", new Pearson() );
	
		//agrego las hojas de calculo
		for (String sheetNameItem : sheetNames)
		{
		
			sheets.put(sheetNameItem,wb.getSheet(sheetNameItem)); 
		}
		
		
		
		evaluator = wb.getCreationHelper().createFormulaEvaluator();
		
		
	}

	
	
	
		protected void setCellValue(String cellName, String value,boolean notifyUpdate)
		{
			
			boolean isNumeric = isNumeric(value);
			
			Cell cell = getCellByCellName(cellName);

			if(cell == null)
		    cell = createCellBlank(cellName);
			
			
			//si es una formula cambio el tipo de celda para setear un numero
			//es el caso de las celdas mixtas que pueden ser formula o un valor ingresado por el usuario
			if (cell.getCellType() == Cell.CELL_TYPE_FORMULA)
			{
				
			//	System.out.println("cell : " + cellName);
			//	System.out.println("value : " + cellName);
				
				if(isNumeric)
				{
					cell.setCellType(Cell.CELL_TYPE_NUMERIC);
				
				}
				else
				{
					
					cell.setCellType(Cell.CELL_TYPE_STRING);	
				}
				
			}
		
			
			if(isNumeric)
			{
				
				cell.setCellValue(Double.parseDouble(value));
			}
			else
			{
				
				cell.setCellValue(value);
			}
		
			
				
			
				if(notifyUpdate)
				{
					this.notifyUpdateCell(cell);
					
				}
					
			
		}
		

		
	
	
	
	protected String getFormatString(String cellName)
	{

		String result = "";

	
		
		Cell cell = getCellByCellName(cellName);

		//es nulo si la celda nunca se ha usado
		if(cell != null)
		result = cell.getCellStyle().getDataFormatString();

		return result;

	}




	private CellData getCellValue(Cell cell) 
	{

		
		CellData cellData = new CellData();
	
		
	
	
		if(cell != null)
		{
		// evaluator.evaluateFormulaCell(cell);

		int cellTypeEvaluator = evaluator.evaluateFormulaCell(cell);

		int cellType = cellTypeEvaluator > -1 ? cellTypeEvaluator : cell
				.getCellType();

		switch (cellType)
		{

		case Cell.CELL_TYPE_BLANK:

			// cellData.put("type",HSSFCell.CELL_TYPE_BLANK);
			cellData.value = cell.getStringCellValue();
			
			break;

		case Cell.CELL_TYPE_NUMERIC:

			// cellData.put("type",HSSFCell.CELL_TYPE_NUMERIC);
			cellData.value = Double.toString(cell.getNumericCellValue());

			break;

		case Cell.CELL_TYPE_STRING:

			// cellData.put("type",HSSFCell.CELL_TYPE_STRING);
			cellData.value = cell.getStringCellValue();

			break;

		case Cell.CELL_TYPE_ERROR:

			cellData.error = FormulaError.forInt(cell.getErrorCellValue()).name();
			
			
			cellData.value = "ERROR";

			break;

		case Cell.CELL_TYPE_FORMULA:

			// cellData.put("type", HSSFCell.CELL_TYPE_ERROR);
			cellData.value = cell.getCellFormula();

			break;

		default:

			// cellData.put("type",HSSFCell.CELL_TYPE_STRING);
			cellData.value = cell.getCellStyle().getDataFormatString();
			
			

		}

		// si la celda es pocentaje multiplico por 100

		if (cell.getCellStyle().getDataFormatString().contains("%")
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


	protected CellData getCellValue(String cellName) 
	{

	
		Cell cell = getCellByCellName(cellName);

		return getCellValue(cell);
		
	
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
		
		System.out.println(cellName+" "+ getCellValue(cellName).value);
		
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

		

		Cell cell = getCellByCellName(cellName);

		evaluator.notifyUpdateCell(cell);

		
	}
	
	
	
	protected void notifyUpdateCell(Cell cell)
	{

		
		evaluator.notifyUpdateCell(cell);

		
	}
	
	
	/**
	 * 
	 * elimina todo el cache de calculos, lo calculos
	 * se tendran que reacer luego de esto.
	 */
	protected void clearAllCachedResultValues()
	{
		
		evaluator.clearAllCachedResultValues();
		
	}

	// --------------------------------------------------------------------------------------------
	// funciones para la obtencion de datos, aqui se obtienen los datos de las planillas luego de ser seteados
	//todos las valores enviados por el cliente

	
	/**
	 * devuelve el objeto Sheet segun el CellName, este debe tener un formato
	 * Hoja1!a1 donde Hoja1 es el nombre de la hoja y a1 es el nombre de la celda
	 * @return
	 */
	private CellData getSheetAndRefByCellName(String cellName)
	{
		CellData cellData = new CellData();
		
			
		String[] cellNameSplit = cellName.split("!");
		cellData.sheet = sheets.get(cellNameSplit[0]);
		cellData.cellRef = cellNameSplit[1];
	
		
		
	  return cellData;
	}
	
	protected Cell getCellByCellName(String cellName)
	{
		
		Cell cell = null;
		
		CellData cellData = getSheetAndRefByCellName(cellName);
		
		CellReference cellReference = new CellReference(cellData.cellRef);

		Row row = cellData.sheet.getRow(cellReference.getRow());

		//la fila puede no existir
		if(row != null)
		{
			
		cell = row.getCell(cellReference.getCol());
		
		}
		
		
		return cell;
		
	}
	
	
	
	protected Cell createCellBlank(String cellName)
	{
		System.out.println("se crea la celda : " + cellName);

		CellData cellData = getSheetAndRefByCellName(cellName);
		
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

}
