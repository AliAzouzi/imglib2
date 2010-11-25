package mpicbg.imglib.scripting.rgb2;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.rgb2.fn.HSBOp;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the HSB saturation of an RGB pixel. */
public class Brightness extends HSBOp {

	public Brightness(final Image<? extends RGBALegacyType> img) {
		super(img);
	}

	@Override
	protected final int getIndex() { return 2; }
}