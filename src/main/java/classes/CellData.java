package classes;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class CellData
{

	/*
	 * libro
	 */
	public Workbook workBook;
	
	/**
	 * valor de la celda
	 */
	public String value;
	
	/**
	 * hoja 
	 */
	public Sheet sheet;
	
	/**
	 * nombre de celda
	 */
	public String cellRef;
	
	/**
	 * codigo de error
	 */
	public String error;
	
	
}
