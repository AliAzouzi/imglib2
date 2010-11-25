package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Log10 extends UnaryOperation {

	public Log10(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Log10(final IFunction fn) {
		super(fn);
	}
	public Log10(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.log10(a().eval());
	}
}