package net.imglib2.script.algorithm.fn;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.Positionable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealPositionable;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.view.RandomAccessibleZeroMinIntervalCursor;
import net.imglib2.view.Views;

public class RandomAccessibleImgProxy<T extends NumericType<T>, RAI extends RandomAccessible<T>> implements Img<T> {

	protected final RAI rai;
	protected final long[] dims;
	
	/** Wrap the {@param ra} in this {@link Img},
	 * which is then able to iterate over the domain from 0 to {@param dims}.
	 * 
	 * @param ra
	 * @param dims
	 */
	public RandomAccessibleImgProxy(final RAI rai, final long[] dims) {
		this.rai = rai;
		this.dims = dims;
	}
	
	@Override
	public RandomAccess<T> randomAccess() {
		return rai.randomAccess();
	}

	@Override
	public RandomAccess<T> randomAccess(Interval interval) {
		return rai.randomAccess(interval);
	}

	@Override
	public int numDimensions() {
		return dims.length;
	}

	@Override
	public long min(int d) {
		return 0;
	}

	@Override
	public void min(long[] min) {
		for (int i=0; i<min.length; ++i) {
			min[i] = 0;
		}
	}

	@Override
	public void min(Positionable min) {
		for (int i=0; i<dims.length; ++i) {
			min.setPosition(0, i);
		}
	}

	@Override
	public long max(int d) {
		return dims[d] -1;
	}

	@Override
	public void max(long[] max) {
		for (int i=0; i<dims.length; ++i) {
			max[i] = dims[i] -1;
		}
	}

	@Override
	public void max(Positionable max) {
		for (int i=0; i<dims.length; ++i) {
			max.setPosition(dims[i] -1, i);
		}
	}

	@Override
	public void dimensions(long[] dimensions) {
		for (int i=0; i<dims.length; ++i) {
			dimensions[i] = dims[i];
		}
	}

	@Override
	public long dimension(int d) {
		return dims[d];
	}

	@Override
	public double realMin(int d) {
		return 0;
	}

	@Override
	public void realMin(double[] min) {
		for (int i=0; i<min.length; ++i) {
			min[i] = 0;
		}
	}

	@Override
	public void realMin(RealPositionable min) {
		for (int i=0; i<dims.length; ++i) {
			min.setPosition(0, i);
		}
	}

	@Override
	public double realMax(int d) {
		return dims[d] -1;
	}

	@Override
	public void realMax(double[] max) {
		for (int i=0; i<max.length; ++i) {
			max[i] = dims[i] -1;
		}
	}

	@Override
	public void realMax(RealPositionable max) {
		for (int i=0; i<dims.length; ++i) {
			max.setPosition(dims[i] -1, i);
		}
	}

	@Override
	public Cursor<T> cursor() {
		return new RandomAccessibleZeroMinIntervalCursor<T>(Views.interval(rai, new long[dims.length], dims));
	}

	@Override
	public Cursor<T> localizingCursor() {
		return cursor();
	}

	@Override
	public long size() {
		if (0 == dims.length) return 0;
		long size = 1;
		for (int i=0; i<dims.length; ++i) {
			size *= dims[i];
		}
		return size;
	}

	@Override
	public T firstElement() {
		return cursor().next();
	}

	@Override
	public boolean equalIterationOrder(IterableRealInterval<?> f) {
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return cursor();
	}

	@Override
	public ImgFactory<T> factory() {
		return null;
	}

	@Override
	public RandomAccessibleImgProxy<T,RAI> copy() {
		return new RandomAccessibleImgProxy<T,RAI>(rai, dims.clone());
	}

}
