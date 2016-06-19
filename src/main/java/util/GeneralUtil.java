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
	
	
	public static ArrayList<String> getArrayString(JsonArray jsonArray)
	{
		ArrayList<String> data = new ArrayList<String>();
		
		for (JsonValue jsonValue : jsonArray)
		{
			data.add(jsonValue.asString());
		}
		
		return data;
		
	}
	
	
	
	
	
	
}
