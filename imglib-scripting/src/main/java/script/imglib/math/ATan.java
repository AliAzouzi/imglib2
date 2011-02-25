package script.imglib.math;

import script.imglib.math.fn.IFunction;
import script.imglib.math.fn.UnaryOperation;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.type.numeric.RealType;

public class ATan extends UnaryOperation {

	public ATan(final Img<? extends RealType<?>> img) {
		super(img);
	}
	public ATan(final IFunction fn) {
		super(fn);
	}
	public ATan(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.atan(a().eval());
	}
}
