package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.scripting.math2.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

/** Returns the natural logarithm of the sum of the argument and 1. */
public class Log1p extends UnaryOperation {

	public Log1p(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Log1p(final IFunction fn) {
		super(fn);
	}
	public Log1p(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.log1p(a().eval());
	}
}