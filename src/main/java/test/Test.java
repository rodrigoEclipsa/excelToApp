package test;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import manager.ExcelBase;

public class Test extends ExcelBase
{

	public Test(String url, String sheetName, String flange)
			throws FileNotFoundException, IOException, EncryptedDocumentException, InvalidFormatException
	{
		super(url, sheetName, flange);

		
	}

	/**
	 * recorre todos los campos que no son formula en el excel y los calcula
	 * 
	 * 
	 * http://localhost:8080/eclipsa_excel_to_app/servlet?password=G8Ag4hs1&data={}&sheetname=iara_diagnostico.xlsx&flange=PLANTILLA
	 *
	 *http://localhost:8080/eclipsa_excel_to_app/servlet?password=G8Ag4hs1&data={}&sheetname=Margenbrutoganadero18meses.xls&flange=dcria
	 *
	 */

	public void runTest()
	{

		
		/*

		// Decide which rows to process
		int rowStart = Math.min(15, sheet.getFirstRowNum());
		int rowEnd = Math.max(1400, sheet.getLastRowNum());

		for (int rowNum = rowStart; rowNum < rowEnd; rowNum++)
		{
			Row r = sheet.getRow(rowNum);
			if (r == null)
			{
				// This whole row is empty
				// Handle it as needed
				continue;
			}

			int lastColumn = Math.max(r.getLastCellNum(),
					4);

			for (int cn = 0; cn < lastColumn; cn++)
			{
			
				
				Cell cell = r.getCell(cn, Row.RETURN_BLANK_AS_NULL);
				CellReference cr = new CellReference(r.getRowNum(),cn);
				
				
				if (cell == null)
				{
				
					// The spreadsheet is empty in this cell
				
				} 
				else
				{
					
				
				System.out.println(cr.formatAsString()+" : "+getCellValue(cell).value);
					
					
				}
				
				
				
				
			}
		
		
		
		
		}
		
		*/
		
		

	}
	
	
	
	
	
	
	
	public void runTestSpecificCell(String cellCoordinate)
	{
		
		
		System.out.println(cellCoordinate+" : "+getCellValue(cellCoordinate).value);
		
		
	}
	
	
	

}
