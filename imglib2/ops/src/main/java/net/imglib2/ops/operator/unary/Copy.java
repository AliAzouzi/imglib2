package net.imglib2.ops.operator.unary;

import net.imglib2.ops.operator.UnaryOperator;

public final class Copy implements UnaryOperator
{
	@Override
	public double computeValue(final double input)
	{
		return input;
	}
}
