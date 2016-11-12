package util;

import java.util.ArrayList;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonValue;

public class GeneralUtil
{
/*
	public static String toJson(Object object) {
		return new Gson().toJson(object);
		}
		public static ResponseTransformer json() {
		return JsonUtil::toJson;
		}
	*/
	public static boolean isNumeric(String str)
	{
		
		return str.matches("[-+]?\\d*\\.?\\d+");

	}
	
	/**
	 * si el string tiene formato de celda excel
	 * nombreLibro!nombreHoja!celdaCordenada
	 * @param str
	 * @return
	 */
	public static boolean isVariableExcel(String str)
	{
		
		return str.matches("^.*!.*!.*");

	}
	
	public static ArrayList<String> getArrayString(JsonArray jsonArray)
	{
		ArrayList<String> data = new ArrayList<String>();
		
		for (JsonValue jsonValue : jsonArray)
		{
			
			data.add(
					isNumeric(jsonValue.toString()) ? jsonValue.toString() :
						jsonValue.asString()
					);
		}
		
		return data;
		
	}
	
	
	
	public static String prettyPrintJSONAsString(String jsonString) {

	    int tabCount = 0;
	    StringBuffer prettyPrintJson = new StringBuffer();
	    String lineSeparator = "\r\n";
	    String tab = "  ";
	    boolean ignoreNext = false;
	    boolean inQuote = false;

	    char character;

	    /* Loop through each character to style the output */
	    for (int i = 0; i < jsonString.length(); i++) {

	        character = jsonString.charAt(i);

	        if (inQuote) {

	            if (ignoreNext) {
	                ignoreNext = false;
	            } else if (character == '"') {
	                inQuote = !inQuote;
	            }
	            prettyPrintJson.append(character);
	        } else {

	            if (ignoreNext ? ignoreNext = !ignoreNext : ignoreNext);

	            switch (character) {

	            case '[':
	                ++tabCount;
	                prettyPrintJson.append(character);
	                prettyPrintJson.append(lineSeparator);
	                printIndent(tabCount, prettyPrintJson, tab);
	                break;

	            case ']':
	                --tabCount;
	                prettyPrintJson.append(lineSeparator);
	                printIndent(tabCount, prettyPrintJson, tab);
	                prettyPrintJson.append(character);
	                break;

	            case '{':
	                ++tabCount;
	                prettyPrintJson.append(character);
	                prettyPrintJson.append(lineSeparator);
	                printIndent(tabCount, prettyPrintJson, tab);
	                break;

	            case '}':
	                --tabCount;
	                prettyPrintJson.append(lineSeparator);
	                printIndent(tabCount, prettyPrintJson, tab);
	                prettyPrintJson.append(character);
	                break;

	            case '"':
	                inQuote = !inQuote;
	                prettyPrintJson.append(character);
	                break;

	            case ',':
	                prettyPrintJson.append(character);
	                prettyPrintJson.append(lineSeparator);
	                printIndent(tabCount, prettyPrintJson, tab);
	                break;

	            case ':':
	                prettyPrintJson.append(character + " ");
	                break;

	            case '\\':
	                prettyPrintJson.append(character);
	                ignoreNext = true;
	                break;

	            default:
	                prettyPrintJson.append(character);
	                break;
	            }
	        }
	    }

	    return prettyPrintJson.toString();
	}

	private static void printIndent(int count, StringBuffer stringBuffer, String indent) {
	    for (int i = 0; i < count; i++) {
	        stringBuffer.append(indent);
	    }
	}
	
	
}
