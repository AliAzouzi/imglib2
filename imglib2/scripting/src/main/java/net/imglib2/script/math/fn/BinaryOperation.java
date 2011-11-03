package net.imglib2.script.math.fn;


import java.util.Collection;

import net.imglib2.IterableRealInterval;
import net.imglib2.RealCursor;
import net.imglib2.type.numeric.RealType;

/* An abstract class to facilitate implementing a function that takes two arguments.
 * Subclasses must call one of the constructors, which take as arguments two out an {@link Img},
 * an {@link IFunction}, or a {@link Number}.
 * 
 * Suppose you want a function that returns the average value of two values. You could write:
<code>
Image<> extends RealType<?>> img1 = ...,
                                img2 = ...;
IFunction avg = new Divide(new Add(img1, img2), 2);
</code>
 * 
 *   Or, instead, define your own Average function:
<code>
public class Average extends BinaryOperation
{
    public Average(final Image<? extends RealType<?>> left, final Image<? extends RealType<?>> right) {
		super(left, right);
	}

	public Average(final IFunction fn, final Image<? extends RealType<?>> right) {
		super(fn, right);
	}

	public Average(final Image<? extends RealType<?>> left, final IFunction fn) {
		super(left, fn);
	}

	public Average(final IFunction fn1, final IFunction fn2) {
		super(fn1, fn2);
	}
	
	public Average(final Image<? extends RealType<?>> left, final Number val) {
		super(left, val);
	}

	public Average(final Number val,final Image<? extends RealType<?>> right) {
		super(val, right);
	}

	public Average(final IFunction left, final Number val) {
		super(left, val);
	}

	public Average(final Number val,final IFunction right) {
		super(val, right);
	}
	
	public Average(final Number val1, final Number val2) {
		super(val1, val2);
	}

	public Average(final Object... elems) throws Exception {
		super(elems);
	}
	
	@Override
	public final double eval() {
		return (a() + b()) / 2;
	}
}
</code>
 */
public abstract class BinaryOperation extends FloatImageOperation
{
	private final IFunction a, b;

	public BinaryOperation(final IterableRealInterval<? extends RealType<?>> left, final IterableRealInterval<? extends RealType<?>> right) {
		this.a = new ImageFunction(left);
		this.b = new ImageFunction(right);
	}

	public BinaryOperation(final IFunction fn, final IterableRealInterval<? extends RealType<?>> right) {
		this.a = fn;
		this.b = new ImageFunction(right);
	}

	public BinaryOperation(final IterableRealInterval<? extends RealType<?>> left, final IFunction fn) {
		this.a = new ImageFunction(left);
		this.b = fn;
	}

	public BinaryOperation(final IFunction fn1, final IFunction fn2) {
		this.a = fn1;
		this.b = fn2;
	}

	public BinaryOperation(final IterableRealInterval<? extends RealType<?>> left, final Number val) {
		this.a = new ImageFunction(left);
		this.b = new NumberFunction(val);
	}

	public BinaryOperation(final Number val,final IterableRealInterval<? extends RealType<?>> right) {
		this.a = new NumberFunction(val);
		this.b = new ImageFunction(right);
	}

	public BinaryOperation(final IFunction fn, final Number val) {
		this.a = fn;
		this.b = new NumberFunction(val);
	}

	public BinaryOperation(final Number val, final IFunction fn) {
		this.a = new NumberFunction(val);
		this.b = fn;
	}

	public BinaryOperation(final Number val1, final Number val2) {
		this.a = new NumberFunction(val1);
		this.b = new NumberFunction(val2);
	}

	/** Compose: "fn(a1, fn(a2, fn(a3, fn(a4, a5))))".
	 *  Will fail with either {@link IllegalArgumentException} or {@link ClassCastException}
	 *  when the @param elem contains instances of classes other than {@link Image},
	 *  {@link Number}, or {@link IFunction}. */
	public BinaryOperation(final Object... elem) throws Exception {
		this.a = Util.wrap(elem[0]);
		IFunction right = Util.wrap(elem[elem.length-1]);
		for (int i=elem.length-2; i>0; i--) {
			IFunction fn = getClass().getConstructor(new Class<?>[]{IFunction.class, IFunction.class})
					.newInstance(Util.wrap(elem[i]), right);
			right = fn;
		}
		this.b = right;
	}

	@Override
	public final void findCursors(final Collection<RealCursor<?>> cursors) {
		a.findCursors(cursors);
		b.findCursors(cursors);
	}

	public final IFunction a() { return a; }
	public final IFunction b() { return b; }

	@Override
	public IFunction duplicate() throws Exception
	{
		return getClass().getConstructor(IFunction.class, IFunction.class).newInstance(a.duplicate(), b.duplicate());
	}
	
	@Override
	public void findImgs(final Collection<IterableRealInterval<?>> iris)
	{
		a.findImgs(iris);
		b.findImgs(iris);
	}
}
