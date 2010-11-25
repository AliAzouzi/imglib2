package mpicbg.imglib.scripting.math;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.scripting.math.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Exp extends UnaryOperation {

	public Exp(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Exp(final IFunction fn) {
		super(fn);
	}
	public Exp(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.exp(a().eval());
	}
}