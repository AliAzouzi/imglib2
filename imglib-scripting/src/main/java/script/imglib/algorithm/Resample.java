package script.imglib.algorithm;

import script.imglib.algorithm.fn.AbstractAffine3D.Mode;
import script.imglib.algorithm.fn.ImgProxy;
import script.imglib.color.Alpha;
import script.imglib.color.Blue;
import script.imglib.color.Green;
import script.imglib.color.RGBA;
import script.imglib.color.Red;
import script.imglib.math.Compute;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.interpolation.linear.LinearInterpolatorFactory;
import mpicbg.imglib.interpolation.nearestneighbor.NearestNeighborInterpolatorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.RGBALegacyType;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.real.FloatType;

/** Resample an image in all its dimensions by a given scaling factor and interpolation mode.
 *  
 *  An image of 2000x2000 pixels, when resampled by 2, will result in an image of dimensions 4000x4000.
 *  
 *  Mathematically this is not a scaling operation and can be proved to be wrong.
 *  For proper scaling, see {@link Scale2D} and {@link Scale3D}. */
public class Resample<N extends NumericType<N>> extends ImgProxy<N>
{
	static public final Mode LINEAR = Affine3D.LINEAR;
	static public final Mode NEAREST_NEIGHBOR = Affine3D.NEAREST_NEIGHBOR;
	static public final Mode BEST = Affine3D.BEST;

	/** Resample an {@link Image} with the best possible mode. */
	public Resample(final Img<N> img, final Number scale) throws Exception {
		this(img, asDimArray(img, scale), BEST);
	}

	public Resample(final Img<N> img, final Number scale, final Mode mode) throws Exception {
		this(img, asDimArray(img, scale), mode);
	}

	public Resample(final Img<N> img, final int[] dimensions) throws Exception {
		this(img, dimensions, BEST);
	}

	public Resample(final Img<N> img, final int[] dimensions, final Mode mode) throws Exception {
		super(process(img, dimensions, mode).getContainer(), img.createType());
	}

	static private final int[] asDimArray(final Img<?> img, final Number scale) {
		final int[] dim = new int[img.numDimensions()];
		final double s = scale.doubleValue();
		for (int i=0; i<dim.length; i++) {
			dim[i] = (int)((img.dimension(i) * s) + 0.5);
		}
		return dim;
	}

	@SuppressWarnings("unchecked")
	static private final <N extends NumericType<N>> Img<N> process(final Img<N> img, int[] dim, final Mode mode) throws Exception {
		// Pad dim array with missing dimensions
		if (dim.length != img.numDimensions()) {
			int[] d = new int[img.numDimensions()];
			int i = 0;
			for (; i<dim.length; i++) d[i] = dim[i];
			for (; i<img.numDimensions(); i++) d[i] = img.dimension(i);
			dim = d;
		}
		final Type<?> type = img.firstElement().createVariable();
		if (RGBALegacyType.class.isAssignableFrom(type.getClass())) { // type instanceof RGBALegacyType fails to compile
			return (Img)processRGBA((Img)img, dim, mode);
		} else if (type instanceof RealType<?>) {
			return (Img)processReal((Img)img, dim, mode);
		} else {
			throw new Exception("Affine transform: cannot handle type " + type.getClass());
		}
	}

	static private final Img<RGBALegacyType> processRGBA(final Img<RGBALegacyType> img, final int[] dim, final Mode mode) throws Exception {
		// Process each channel independently and then compose them back
		return new RGBA(processReal(Compute.inFloats(new Red(img)), dim, mode),
						processReal(Compute.inFloats(new Green(img)), dim, mode),
						processReal(Compute.inFloats(new Blue(img)), dim, mode),
						processReal(Compute.inFloats(new Alpha(img)), dim, mode)).asImage();
	}

	static private final <T extends RealType<T>> Img<T> processReal(final Img<T> img, final long[] dim, final Mode mode) throws Exception {

		final Img<T> res = img.factory().create(dim, img.firstElement().createVariable());

		InterpolatorFactory<T> ifac;
		switch (mode) {
		case LINEAR:
			ifac = new LinearInterpolatorFactory<T>(new OutOfBoundsMirrorFactory<T,Img<T>>(OutOfBoundMirrorFactory.Boundary.SINGLE));
			break;
		case NEAREST_NEIGHBOR:
			ifac = new NearestNeighborInterpolatorFactory<T>(new OutOfBoundsStrategyMirrorFactory<T>());
			break;
		default:
			throw new Exception("Resample: unknown mode!");
		}

		final Interpolator<T> inter = ifac.createInterpolator(img);
		final LocalizableCursor<T> c2 = res.createLocalizableCursor();
		final float[] s = new float[dim.length];
		for (int i=0; i<s.length; i++) s[i] = (float)img.getDimension(i) / dim[i];
		final int[] d = new int[dim.length];
		final float[] p = new float[dim.length];
		while (c2.hasNext()) {
			c2.fwd();
			c2.getPosition(d);
			for (int i=0; i<d.length; i++) p[i] = d[i] * s[i];
			inter.moveTo(p);
			c2.getType().set(inter.getType());			
		}
		return res;
	}
}