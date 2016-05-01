package customFunction;

import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public class Pearson extends Fixed2ArgFunction
{

	@Override
	public ValueEval evaluate(int arg0, int arg1, ValueEval arg2,
			ValueEval arg3)
	{
		// TODO Auto-generated method stub
		
		
		System.out.println("************* " + arg3);
		
		
		return null;
	}

	
	
	
}
