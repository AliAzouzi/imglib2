package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class Signum extends UnaryOperation {

	public Signum(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public Signum(final IFunction fn) {
		super(fn);
	}
	public Signum(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.signum(a().eval());
	}
}
