package customFunction;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.Fixed3ArgFunction;

public class DAVERAGE extends Fixed3ArgFunction
{

	@Override
	public ValueEval evaluate(int srcRowIndex, int srcColumnIndex,
			ValueEval arg0, ValueEval arg1, ValueEval arg2)
	{
	/*
		  try
		{
			ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
		} catch (EvaluationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		*/
		//evalu
		return new NumberEval(2);
	}

}
