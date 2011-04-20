package imglib.ops.example.rev3.function;

import imglib.ops.example.rev3.operator.BinaryOperator;

public final class BinaryFunction implements IntegerIndexedScalarFunction
{
	private final IntegerIndexedScalarFunction leftFunction;
	private final IntegerIndexedScalarFunction rightFunction;
	private final BinaryOperator operator;

	public BinaryFunction(BinaryOperator operator, IntegerIndexedScalarFunction leftFunction, IntegerIndexedScalarFunction rightFunction)
	{
		this.leftFunction = leftFunction;
		this.rightFunction = rightFunction;
		this.operator = operator;
	}
	
	@Override
	public double evaluate(int[] position)
	{
		double leftValue = leftFunction.evaluate(position);

		double rightValue = rightFunction.evaluate(position);
		
		return operator.computeValue(leftValue, rightValue);
	}
}

