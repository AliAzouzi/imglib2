package mpicbg.imglib.function.operations.op;

//import java.util.HashMap;
import java.util.Set;

import mpicbg.imglib.function.operations.Operation;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

public abstract class AOpN< R extends RealType<R> > extends AN<R> implements Op<R> {

	protected final Operation<R> other, op;
	protected final RealType<?> num;
	protected R tmp;

	public AOpN(final Operation<R> other, final Number val, final Operation<R> op) {
		this.other = other;
		this.op = op;
		this.num = asType(val);
	}

	@Override
	public final void fwd() {
		other.fwd();
	}

	@Override
	public void getImages(final Set<Image<? extends RealType<?>>> images) {
		other.getImages(images);
	}

	@Override
	public void init(final R ref) {
		tmp = ref.createVariable();
		other.init(ref);
	}
}
