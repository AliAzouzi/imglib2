package mpicbg.imglib.scripting.rgb.fn;

import java.util.Collection;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.IFunction;
import mpicbg.imglib.type.numeric.RGBALegacyType;

/** Extracts the red pixel value. */
public abstract class RGBAOp implements IFunction {

	protected final Cursor<? extends RGBALegacyType> c;

	public RGBAOp(final Image<? extends RGBALegacyType> img) {
		this.c = img.createCursor();
	}

	@Override
	public final void findCursors(final Collection<Cursor<?>> cursors) {
		cursors.add(c);
	}
}