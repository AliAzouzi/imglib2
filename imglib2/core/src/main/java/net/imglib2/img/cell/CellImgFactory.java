package net.imglib2.img.cell;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.CharArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;

public class CellImgFactory< T extends NativeType<T> > extends NativeImgFactory< T >
{
	protected int[] defaultCellDimensions = { 10 };

	public CellImgFactory()
	{
	}
	
	public CellImgFactory( final int cellSize )
	{
		defaultCellDimensions[ 0 ] = cellSize;
	}

	public CellImgFactory( final int[] cellDimensions )
	{
		if ( cellDimensions == null || cellDimensions.length == 0 )
		{
			System.err.println( "CellContainerFactory(): cellSize is null. Using equal cell size of " + defaultCellDimensions[0]);
			return;
		}

		for ( int i = 0; i < cellDimensions.length; i++ )
		{
			if ( cellDimensions[ i ] <= 0 )
			{
				System.err.println( "CellContainerFactory(): cell size in dimension " + i + " is <= 0, using a size of " + defaultCellDimensions[ 0 ] + "." );
				cellDimensions[ i ] = defaultCellDimensions[ 0 ];
			}
		}

		defaultCellDimensions = cellDimensions;
	}

	protected long[] checkDimensions( long dimensions[] )
	{
		if ( dimensions == null || dimensions.length == 0 )
		{
			System.err.println( "CellContainerFactory(): dimensionality is null. Creating a 1D cell with size 1." );
			dimensions = new long[] { 1 };
		}

		for ( int i = 0; i < dimensions.length; i++ )
		{
			if ( dimensions[ i ] <= 0 )
			{
				System.err.println( "CellContainerFactory(): size of dimension " + i + " is <= 0, using a size of 1." );
				dimensions[ i ] = 1;
			}
		}

		return dimensions;
	}

	protected int[] checkCellSize( int[] cellDimensions, long[] dimensions )
	{
		if ( cellDimensions == null )
		{
			cellDimensions = new int[ dimensions.length ];
			for ( int i = 0; i < cellDimensions.length; i++ )
				cellDimensions[ i ] = defaultCellDimensions[ ( i < defaultCellDimensions.length ) ? i : 0 ];
		}

		if ( cellDimensions.length != dimensions.length )
		{
			// System.err.println( "CellContainerFactory(): dimensionality of image is unequal to dimensionality of cells, adjusting cell dimensionality." );
			int[] cellDimensionsNew = new int[ dimensions.length ];

			for ( int i = 0; i < dimensions.length; i++ )
			{
				if ( i < cellDimensions.length )
					cellDimensionsNew[ i ] = cellDimensions[ i ];
				else
					cellDimensionsNew[ i ] = defaultCellDimensions[ ( i < defaultCellDimensions.length ) ? i : 0 ];
			}

			cellDimensions = cellDimensionsNew;
		}

		return cellDimensions;
	}

	@Override
	public CellImg< T, ?, ? > create( final long[] dim, final T type )
	{
		return ( CellImg< T, ?, ? > ) type.createSuitableNativeImg( this, dim );
	}

	@Override
	public CellImg< T, BitArray, DefaultCell< BitArray > > createBitInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, BitArray, DefaultCell< BitArray > >( this, new ListImgCells< BitArray >( new BitArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@Override
	public CellImg< T, ByteArray, DefaultCell< ByteArray > > createByteInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, ByteArray, DefaultCell< ByteArray > >( this, new ListImgCells< ByteArray >( new ByteArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@Override
	public CellImg< T, CharArray, DefaultCell< CharArray > > createCharInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, CharArray, DefaultCell< CharArray > >( this, new ListImgCells< CharArray >( new CharArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@Override
	public CellImg< T, ShortArray, DefaultCell< ShortArray > > createShortInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, ShortArray, DefaultCell< ShortArray > >( this, new ListImgCells< ShortArray >( new ShortArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@Override
	public CellImg< T, IntArray, DefaultCell< IntArray > > createIntInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, IntArray, DefaultCell< IntArray > >( this, new ListImgCells< IntArray >( new IntArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@Override
	public CellImg< T, LongArray, DefaultCell< LongArray > > createLongInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, LongArray, DefaultCell< LongArray > >( this, new ListImgCells< LongArray >( new LongArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@Override
	public CellImg< T, FloatArray, DefaultCell< FloatArray > > createFloatInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, FloatArray, DefaultCell< FloatArray > >( this, new ListImgCells< FloatArray >( new FloatArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@Override
	public CellImg< T, DoubleArray, DefaultCell< DoubleArray > > createDoubleInstance( long[] dimensions, int entitiesPerPixel )
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( defaultCellDimensions, dimensions );
		return new CellImg< T, DoubleArray, DefaultCell< DoubleArray > >( this, new ListImgCells< DoubleArray >( new DoubleArray( 1 ), entitiesPerPixel, dimensions, cellSize ) );
	}

	@SuppressWarnings( { "unchecked", "rawtypes" } )
	@Override
	public <S> ImgFactory<S> imgFactory( final S type ) throws IncompatibleTypeException
	{
		if ( NativeType.class.isInstance( type ) )
			return new CellImgFactory( defaultCellDimensions );
		else
			throw new IncompatibleTypeException( this, type.getClass().getCanonicalName() + " does not implement NativeType." );
	}
}
