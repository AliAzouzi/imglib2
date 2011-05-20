package net.imglib2.script.math;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.type.numeric.RealType;

public class Expm1 extends UnaryOperation {

	public Expm1(final IterableRealInterval<? extends RealType<?>> img) {
		super(img);
	}
	public Expm1(final IFunction fn) {
		super(fn);
	}
	public Expm1(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.expm1(a().eval());
	}
}
