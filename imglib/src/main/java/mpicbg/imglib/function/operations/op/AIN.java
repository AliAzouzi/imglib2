package mpicbg.imglib.function.operations.op;

//import java.util.HashMap;
import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public abstract class AIN< R extends RealType<R> > extends AN<R> implements Op<R> {

	protected final Cursor<? extends RealType<?>> c;
	protected final RealType<?> num;
	protected final Operation<R> op;

	public AIN(final Image<? extends RealType<?>> img, final Number val, final Operation<R> op) {
		this.c = img.createCursor();
		this.op = op;
		this.num = asType(val);
	}

	@Override
	public final void fwd() {
		c.fwd();
	}

	@Override
	public void getImages(final Set<Image<? extends RealType<?>>> images) {
		images.add(c.getImage());
	}

	@Override
	public void init(final R ref) {}
}
