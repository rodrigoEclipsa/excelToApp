/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager;

import java.io.File;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
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
//	private Map<String,Boolean> changedCells;


    


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

	

		
		public void setCellValue(String cellName, String value,boolean notifyUpdate)
		{
			
			boolean isNumeric = isNumeric(value);
			
			Cell cell = getCellByCoordinate(cellName);

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
		

		
	
	
	
	public String getFormatString(String cellName)
	{

		String result = "";

	
		
		Cell cell = getCellByCoordinate(cellName);

		//es nulo si la celda nunca se ha usado
		if(cell != null)
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

		case Cell.CELL_TYPE_BLANK:

			// cellValue.put("type",HSSFCell.CELL_TYPE_BLANK);
			cellValue.value = cell.getStringCellValue();
			
			break;

		case Cell.CELL_TYPE_NUMERIC:

			// cellValue.put("type",HSSFCell.CELL_TYPE_NUMERIC);
			cellValue.value = Double.toString(cell.getNumericCellValue());

			break;

		case Cell.CELL_TYPE_STRING:

			// cellValue.put("type",HSSFCell.CELL_TYPE_STRING);
			cellValue.value = cell.getStringCellValue();

			break;

		case Cell.CELL_TYPE_ERROR:

			cellValue.error = Cell.CELL_TYPE_ERROR;
			cellValue.value = Byte.toString(cell.getErrorCellValue());

			break;

		case Cell.CELL_TYPE_FORMULA:

			// cellValue.put("type", HSSFCell.CELL_TYPE_ERROR);
			cellValue.value = cell.getCellFormula();

			break;

		default:

			// cellValue.put("type",HSSFCell.CELL_TYPE_STRING);
			cellValue.value = cell.getCellStyle().getDataFormatString();
			
			

		}

		// si la celda es pocentaje multiplico por 100

		if (cell.getCellStyle().getDataFormatString().contains("%")
				&& cellType == Cell.CELL_TYPE_NUMERIC)
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

	
		Cell cell = getCellByCoordinate(cellName);

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

		

		Cell cell = getCellByCoordinate(cellName);

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

	
	
	
	public Cell getCellByCoordinate(String cellName)
	{
		
		Cell cell = null;
		
		CellReference cellReference = new CellReference(cellName);

		
		
		Row row = sheet.getRow(cellReference.getRow());

		//la fila puede no existir
		if(row != null)
		cell = row.getCell(cellReference.getCol());
		
		
		
		return cell;
		
	}
	
	
	
	public Cell createCellBlank(String cellName)
	{
		System.out.println("se crea la celda : " + cellName);
		
		CellReference cellReference = new CellReference(cellName);

		Row row = sheet.getRow(cellReference.getRow());

		//la fila puede no existir
		if(row == null)
		{
			row = sheet.createRow(cellReference.getRow());
			
		}
		
		
		Cell cell = row.createCell(cellReference.getCol());
		
		return cell;
		
	}

	public boolean isNumeric(String str)
	{
		
		return str.matches("[-+]?\\d*\\.?\\d+");

		
		
	}

}
