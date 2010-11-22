package mpicbg.imglib.function.operations.op;

import java.util.Set;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public final class OpI< A extends RealType<A> > implements Op<A> {

	private final Operation<A> op, other;
	private final Cursor<A> cr;
	private A tmp;

	public OpI(final Operation<A> other, final Image<A> right, final Operation<A> op) {
		this.cr = right.createCursor();
		this.other = other;
		this.op = op;
	}

	@Override
	public final void compute(final A output) {
		other.compute(tmp);
		op.compute(tmp, cr.getType(), output);
	}

	@Override
	public final void fwd() {
		other.fwd();
		cr.fwd();
	}

	@Override
	public final void getImages(final Set<Image<A>> images) {
		other.getImages(images);
		images.add(cr.getImage());
	}

	@Override
	public void init(final A ref) {
		tmp = ref.createVariable();
		other.init(ref);
	}
}
