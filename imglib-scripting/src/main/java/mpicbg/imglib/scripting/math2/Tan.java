package mpicbg.imglib.scripting.math2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math2.fn.IFunction;
import mpicbg.imglib.scripting.math2.fn.UnaryOperation;
import mpicbg.imglib.type.numeric.RealType;

public class Tan extends UnaryOperation {

	public Tan(final Image<? extends RealType<?>> img) {
		super(img);
	}
	public Tan(final IFunction fn) {
		super(fn);
	}
	public Tan(final Number val) {
		super(val);
	}

	@Override
	public final double eval() {
		return Math.tan(a().eval());
	}
}