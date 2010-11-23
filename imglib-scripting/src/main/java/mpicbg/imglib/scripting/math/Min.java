package mpicbg.imglib.scripting.math;

import java.util.Set;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.scripting.math.fn.Operation;
import mpicbg.imglib.scripting.math.op.II;
import mpicbg.imglib.scripting.math.op.IN;
import mpicbg.imglib.scripting.math.op.IOp;
import mpicbg.imglib.scripting.math.op.NI;
import mpicbg.imglib.scripting.math.op.NN;
import mpicbg.imglib.scripting.math.op.NOp;
import mpicbg.imglib.scripting.math.op.Op;
import mpicbg.imglib.scripting.math.op.OpI;
import mpicbg.imglib.scripting.math.op.OpN;
import mpicbg.imglib.scripting.math.op.OpOp;
import mpicbg.imglib.type.numeric.RealType;

public class Min< R extends RealType<R> > implements Operation<R> {

	private final Op<R> inner;

	public Min(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		this.inner = new II<R>(left, right, this);
	}

	public Min(final Operation<R> op, final Image<? extends RealType<?>> right) {
		this.inner = new OpI<R>(op, right, this);
	}

	public Min(final Image<? extends RealType<?>> left, final Operation<R> op) {
		this.inner = new IOp<R>(left, op, this);
	}

	public Min(final Operation<R> op1, final Operation<R> op2) {
		this.inner = new OpOp<R>(op1, op2, this);
	}
	
	public Min(final Image<? extends RealType<?>> left, final Number val) {
		this.inner = new IN<R>(left, val, this);
	}

	public Min(final Number val,final Image<? extends RealType<?>> right) {
		this.inner = new NI<R>(val, right, this);
	}

	public Min(final Operation<R> left, final Number val) {
		this.inner = new OpN<R>(left, val, this);
	}

	public Min(final Number val,final Operation<R> right) {
		this.inner = new NOp<R>(val, right, this);
	}

	public Min(final Number val1, final Number val2) {
		this.inner = new NN<R>(val1, val2, this);
	}

	@Override
	public final void compute( final RealType<?> input1, final RealType<?> input2, final R output ) {
		output.setReal(Math.min(input1.getRealDouble(), input2.getRealDouble()));
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
	public final void getImages(final Set<Image<?>> images) {
		inner.getImages(images);
	}

	@Override
	public final void init(final R ref) {
		inner.init(ref);
	}
}


