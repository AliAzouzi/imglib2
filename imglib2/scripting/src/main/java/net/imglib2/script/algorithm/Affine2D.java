package net.imglib2.script.algorithm;

import java.awt.geom.AffineTransform;

import net.imglib2.img.Img;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.script.math.fn.IFunction;

/** 
* Expects matrix values in the same order that {@link AffineTransform} uses.
* 
* The constructors accept either an {@link Image} or an {@link IFunction} from which an {@link Image} is generated. */
public class Affine2D<N extends NumericType<N>> extends Affine3D<N>
{
	/** Affine transform the image with the best interpolation mode available. */
	@SuppressWarnings("boxing")
	public Affine2D(final Object fn,
			final Number scaleX, final Number shearX,
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY) throws Exception {
		this(fn, scaleX, shearX,
				 shearY, scaleY,
				 translateX, translateY,
				 Affine3D.BEST, 0);
	}

	/** Affine transform the image with the best interpolation mode available. */
	public Affine2D(final Object fn,
			final Number scaleX, final Number shearX,
			final Number shearY, final Number scaleY,
			final Number translateX, final Number translateY,
			final Mode mode, final Number outside) throws Exception {
		super(fn, scaleX.floatValue(), shearX.floatValue(),
				  shearY.floatValue(), scaleY.floatValue(),
				  translateX.floatValue(), translateY.floatValue(),
				  mode, outside);
	}

	public Affine2D(final Object fn, final AffineTransform aff) throws Exception {
		this(fn, aff, Affine3D.BEST);
	}

	@SuppressWarnings("boxing")
	public Affine2D(final Object fn, final AffineTransform aff, final Mode mode) throws Exception {
		this(fn, aff, mode, 0);
	}

	public Affine2D(final Object fn, final AffineTransform aff, final Number outside) throws Exception {
		this(fn, aff, Affine3D.BEST, outside);
	}

	public Affine2D(final Object fn, final AffineTransform aff, final Mode mode, final Number outside) throws Exception {
		super(fn, (float)aff.getScaleX(), (float)aff.getShearX(),
				  (float)aff.getShearY(), (float)aff.getScaleY(),
				  (float)aff.getTranslateX(), (float)aff.getTranslateY(),
				  mode, outside);
	}

	public Affine2D(final Object fn,
					final float scaleX, final float shearX,
					final float shearY, final float scaleY,
					final float translateX, final float translateY,
					final Mode mode, final OutOfBoundsFactory<N,Img<N>> oobf) throws Exception
	{
		super(fn, scaleX, shearX,
				  shearY, scaleY,
				  translateX, translateY,
				  mode, oobf);
	}
}
