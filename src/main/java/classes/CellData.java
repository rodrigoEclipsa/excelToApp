package classes;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class CellData
{

	/*
	 * libro
	 */
	public WorkBookObject workBookObject;
	
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
	
	
	/**
	 * cell ref
	 */
	public Cell cell;
	
	
}
