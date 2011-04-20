package net.imglib2.script.math;

import net.imglib2.script.math.fn.BinaryOperation;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Max extends BinaryOperation
{
	public Max(final Img<? extends RealType<?>> left, final Img<? extends RealType<?>> right) {
		super(left, right);
	}

	public Max(final IFunction fn, final Img<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Max(final Img<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Max(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Max(final Img<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Max(final Number val,final Img<? extends RealType<?>> right) {
		super(val, right);
	}

	public Max(final IFunction left, final Number val) {
		super(left, val);
	}

	public Max(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Max(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Max(final Object... elems) throws Exception {
		super(elems);
	}

	@Override
	public final double eval() {
		return Math.max(a().eval(), b().eval());
	}
}
