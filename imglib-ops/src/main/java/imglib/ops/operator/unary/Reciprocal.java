package imglib.ops.operator.unary;

import imglib.ops.operator.UnaryOperator;

public class Reciprocal implements UnaryOperator
{
	@Override
	public double computeValue(double input)
	{
		if (input == 0)
			return Double.POSITIVE_INFINITY;
		
		return 1.0 / input;
	}

}
