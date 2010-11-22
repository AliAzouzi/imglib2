package mpicbg.imglib.scripting.math;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.op.Op;
import mpicbg.imglib.scripting.math.op.SingleI;
import mpicbg.imglib.scripting.math.op.SingleN;
import mpicbg.imglib.scripting.math.op.SingleOp;
import mpicbg.imglib.type.numeric.RealType;

public class ACos< R extends RealType<R> > implements Operation<R> {

	final Op<R> inner;

	public ACos(final Image<? extends RealType<?>> img) {
		this.inner = new SingleI<R>(img, this);
	}

	public ACos(final Operation<R> op) {
		this.inner = new SingleOp<R>(op, this);
	}

	public ACos(final Number val) {
		this.inner = new SingleN<R>(val, this);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> ignored, final R output ) {
		output.setReal(Math.acos(input1.getRealDouble()));
	}

	@Override
	public final void fwd() {
		inner.fwd();
	}

	@Override
	public final void compute(final R output) {
		inner.compute(output);
	}

	@Override
	public final void getImages(final Set<Image<? extends RealType<?>>> images) {
		inner.getImages(images);
	}

	@Override
	public final void init(final R ref) {
		inner.init(ref);
	}
}