package imglib.ops.operator.binary;

import imglib.ops.operator.BinaryOperator;

public final class Difference implements BinaryOperator
{
	@Override
	public double computeValue(final double input1, final double input2)
	{
		return Math.abs(input1 - input2);
	}
	
}

