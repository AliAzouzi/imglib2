package script.imglib.color;

import script.imglib.color.fn.ChannelOp;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.ARGBType;

/** Extracts the green pixel value. */
public class Green extends ChannelOp {

	/** Extract the green channel of each pixel, in the range [0, 255]. */
	public Green(final Img<? extends ARGBType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 8; }
}