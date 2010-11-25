package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.BinaryOperation;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RealType;

public class Add extends BinaryOperation
{
	public Add(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Add(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Add(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Add(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Add(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Add(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Add(final IFunction left, final Number val) {
		super(left, val);
	}

	public Add(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Add(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Add(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return a().eval() + b().eval();
	}
}