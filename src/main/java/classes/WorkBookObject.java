package classes;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * datos de cada libro de calculo usado por el sistema
 * @author rodrigo
 *
 */
public class WorkBookObject
{

	/**
	 * workBookInfo heredado de la informacion enviada por el cliente
	 */
	public WorkBookInfo workBookInfo;
	
	public Workbook workBook;
	public Map<String, Sheet> sheets = new HashMap<String, Sheet>();
	public FormulaEvaluator evaluator;
	
	
	
}
