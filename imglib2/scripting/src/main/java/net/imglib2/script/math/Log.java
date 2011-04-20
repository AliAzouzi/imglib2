package net.imglib2.script.math;

import net.imglib2.script.math.fn.IFunction;
import net.imglib2.script.math.fn.UnaryOperation;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;

public class Log extends UnaryOperation {

	public Log(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Log(final IFunction fn) {
		super(fn);
	}
	public Log(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.log(a().eval());
	}
}
