package net.imglib2.script.view;

import java.util.List;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.script.algorithm.fn.RandomAccessibleIntervalImgProxy;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

public class Extend<T extends RealType<T>> extends RandomAccessibleIntervalImgProxy<T>
{
	public Extend(final RandomAccessibleInterval<T> img, final long[] offset, final long[] dimension, final Number value) {
		super(Views.offsetInterval(Views.extendValue(img, AlgorithmUtil.type(img, value.doubleValue())), offset, dimension));
	}

	public Extend(final RandomAccessibleInterval<T> img, final List<Number> offset, final List<Number> dimension, final Number value) {
		this(img, AlgorithmUtil.asLongArray(offset), AlgorithmUtil.asLongArray(dimension), value);
	}
	
	public Extend(final RandomAccessibleInterval<T> img, final long[] dimension, final Number value) {
		super(Views.offsetInterval(
				Views.extendValue(img, AlgorithmUtil.type(img, value.doubleValue())),
				new long[Math.max(img.numDimensions(), dimension.length)],
				dimension));
	}
	
	public Extend(final RandomAccessibleInterval<T> img, final List<Number> dimension, final Number value) {
		this(img, AlgorithmUtil.asLongArray(dimension), value);
	}
	
	public Extend(final RandomAccessibleInterval<T> img, final Number value) {
		super(Views.interval(Views.extendValue(img, AlgorithmUtil.type(img, value.doubleValue())), img));
	}
	
	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img, final long[] offset, final long[] dimension) {
		this(img, offset, dimension, 0);
	}

	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img, final List<Number> offset, final List<Number> dimension) {
		this(img, offset, dimension, 0);
	}
	
	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img, final long[] dimension) {
		this(img, dimension, 0);
	}
	
	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img, final List<Number> dimension) {
		this(img, dimension, 0);
	}
	
	/** Defaults to an out of bounds value of 0. */
	@SuppressWarnings("boxing")
	public Extend(final RandomAccessibleInterval<T> img) {
		this(img, 0);
	}
}
