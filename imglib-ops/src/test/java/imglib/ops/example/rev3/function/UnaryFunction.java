package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.operator.UnaryOperator;

public final class UnaryFunction implements IntegralScalarFunction
{
	private final IntegralScalarFunction inputFunction;
	private final UnaryOperator operator;

	public UnaryFunction(UnaryOperator operator, IntegralScalarFunction inputFunction)
	{
		this.inputFunction = inputFunction;
		this.operator = operator;
	}
	
	@Override
	public double evaluate(int[] position)
	{
		double input = inputFunction.evaluate(position);

		return operator.computeValue(input);
	}
}

