package net.imglib2.script.analysis;

import net.imglib2.IterableRealInterval;
import net.imglib2.script.analysis.fn.NumericReduce;
import net.imglib2.script.math.fn.IFunction;
import net.imglib2.type.numeric.RealType;

/** Find the maximum value.
 * 
 * @see ImgMin
 * @author Albert Cardona
 */
public class ImgMax extends NumericReduce
{
	private static final long serialVersionUID = 1L;

	public ImgMax(final IFunction fn) throws Exception {
		super(fn);
	}
	
	public ImgMax(final IterableRealInterval<? extends RealType<?>> img) throws Exception {
		super(img);
	}

	@Override
	protected final double reduce(final double r, final double v) {
		return Math.max(r, v);
	}
}