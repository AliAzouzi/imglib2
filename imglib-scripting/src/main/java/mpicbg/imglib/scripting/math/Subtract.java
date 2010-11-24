package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.BinaryOperation;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.type.numeric.RealType;

public class Subtract< R extends RealType<R> > extends BinaryOperation<R>
{
	public Subtract(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Subtract(final Operation<R> op, final Image<? extends RealType<?>> right) {
		super(op, right);
	}

	public Subtract(final Image<? extends RealType<?>> left, final Operation<R> op) {
		super(left, op);
	}

	public Subtract(final Operation<R> op1, final Operation<R> op2) {
		super(op1, op2);
	}
	
	public Subtract(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Subtract(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Subtract(final Operation<R> left, final Number val) {
		super(left, val);
	}

	public Subtract(final Number val,final Operation<R> right) {
		super(val, right);
	}

	public Subtract(final Number val1, final Number val2) {
		super(val1, val2);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> input2, final R output ) {
		output.setReal(input1.getRealDouble() - input2.getRealDouble());
	}
}