package mpicbg.imglib.scripting.rgb2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb2.fn.ChannelOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the alpha pixel value. */
public class Alpha extends ChannelOp {

	public Alpha(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getShift() { return 24; }
}