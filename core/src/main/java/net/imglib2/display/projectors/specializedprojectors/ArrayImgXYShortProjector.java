package net.imglib2.display.projectors.specializedprojectors;

import net.imglib2.display.projectors.Abstract2DProjector;
import net.imglib2.display.projectors.screenimages.ByteScreenImage;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.numeric.integer.GenericShortType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.util.IntervalIndexer;

/**
 * Fast implementation of a {@link Abstract2DProjector} that selects a 2D data
 * plain from an ShortType ArrayImg. The map method implements a normalization
 * function. The resulting image is a ShortType ArrayImg. *
 * 
 * @author Michael Zinsmaier, Martin Horn, Christian Dietz
 * 
 * @param <A>
 */
public class ArrayImgXYShortProjector< A extends GenericShortType< A >> extends Abstract2DProjector< A, UnsignedShortType >
{

	private final short[] sourceArray;

	private final short[] targetArray;

	private final double min;

	private final double normalizationFactor;

	private final boolean isSigned;

	private final long[] dims;

	/**
	 * Normalizes an ArrayImg and writes the result into target. This can be used in conjunction with {@link ByteScreenImage} for direct displaying.
	 * The normalization is based on a normalization factor and a minimum value with the following dependency:<br>
	 * <br>
	 * normalizationFactor = (typeMax - typeMin) / (newMax - newMin) <br>
	 * min = newMin <br>
	 * <br>
	 * A value is normalized by: normalizedValue = (value - min) * normalizationFactor.<br>
	 * Additionally the result gets clamped to the type range of target (that allows playing with saturation...).
	 *  
	 * @param source Signed/Unsigned input data
	 * @param target Unsigned output
	 * @param normalizationFactor
	 * @param min
	 */
	public ArrayImgXYShortProjector( ArrayImg< A, ShortArray > source, ArrayImg< UnsignedShortType, ShortArray > target, double normalizationFactor, double min)
	{
		super( source.numDimensions() );

		this.isSigned = (source.firstElement() instanceof ShortType);
		this.targetArray = target.update( null ).getCurrentStorageArray();
		this.normalizationFactor = normalizationFactor;
		this.min = min;
		this.dims = new long[ n ];
		source.dimensions( dims );

		sourceArray = source.update( null ).getCurrentStorageArray();
	}

	@Override
	public void map()
	{
		//more detailed documentation of the binary arithmetic can be found in ArrayImgXYByteProjector
		
		double minCopy = min;
		int offset = 0;
		long[] tmpPos = position.clone();
		tmpPos[ 0 ] = 0;
		tmpPos[ 1 ] = 0;

		offset = ( int ) IntervalIndexer.positionToIndex( tmpPos, dims );

		// copy the selected part of the source array (e.g. a xy plane at time t
		// in a video) into the target array.
		System.arraycopy( sourceArray, offset, targetArray, 0, targetArray.length );

		if ( isSigned )
		{
			for ( int i = 0; i < targetArray.length; i++ )
			{
				// Short.MIN => 0 && Short.MAX => 65535 (2^16 - 1)  => unsigned short
				targetArray[ i ] = ( short ) ( targetArray[ i ] - 0x8000 );
			}
			// old => unsigned short minimum
			minCopy += 0x8000;
		}
		if ( normalizationFactor != 1 )
		{
			for ( int i = 0; i < targetArray.length; i++ )
			{
				// normalizedValue = (oldValue - min) * normalizationFactor
				// clamped to 0 .. 65535
				targetArray[ i ] = ( short ) Math.min( 65535, Math.max( 0, ( Math.round( ( (targetArray[ i ] & 0xFFFF) - minCopy) * normalizationFactor ) ) ) );
			}
		}
	}

}
