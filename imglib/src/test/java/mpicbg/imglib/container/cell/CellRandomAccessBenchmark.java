package mpicbg.imglib.container.cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.util.IntervalIndexer;

public class CellRandomAccessBenchmark
{
	long[] dimensions;

	int numValues;

	int[] intData;

	long intDataSum;

	CellContainer< IntType, ? > intImg;
	CellContainer< IntType, ? > intImgCopy;

	public void createSourceData()
	{
		dimensions = new long[] { 480, 480, 102 };

		numValues = 1;
		for ( int d = 0; d < dimensions.length; ++d )
			numValues *= dimensions[ d ];

		intData = new int[ numValues ];
		intDataSum = 0;
		Random random = new Random( 0 );
		for ( int i = 0; i < numValues; ++i )
		{
			intData[ i ] = random.nextInt();
			intDataSum += intData[ i ];
		}

		intImg = new CellContainerFactory< IntType >( 40 ).create( dimensions, new IntType() );
	}


	/**
	 * Fill intImg (a CellContainer with 40x40x40 cells) with data using flat array iteration order.
	 */
	public void fillImage()
	{
		int[] pos = new int[ dimensions.length ];
		RandomAccess< IntType > a = intImg.randomAccess();

		int[] idim = new int[ dimensions.length ];
		for ( int d = 0; d < dimensions.length; ++d )
			idim[ d ] = ( int ) dimensions[ d ];

		for ( int i = 0; i < numValues; ++i )
		{
			IntervalIndexer.indexToPosition( i, idim, pos );
			a.setPosition( pos );
			a.get().set( intData[ i ] );
		}
	}

	
	public void copyWithSourceIteration(Img< IntType > srcImg, Img< IntType > dstImg)
	{
		long[] pos = new long[ dimensions.length ];
		Cursor< IntType > src = srcImg.localizingCursor();
		RandomAccess< IntType > dst = dstImg.randomAccess();
		while( src.hasNext() ) {
			src.fwd();
			src.localize( pos );
			dst.setPosition( pos );
			dst.get().set( src.get() );
		}
	}



	public static Long median( ArrayList<Long> values )
	{
		Collections.sort(values);

		if (values.size() % 2 == 1)
			return values.get((values.size() + 1) / 2 - 1);
		else {
			long lower = values.get(values.size() / 2 - 1);
			long upper = values.get(values.size() / 2);

			return (lower + upper) / 2;
		}
	}

	public interface Benchmark
	{
		public void run();
	}

	public static void benchmark( Benchmark b )
	{
		ArrayList<Long> times = new ArrayList<Long>( 100 );
		final int numRuns = 20;
		for ( int i = 0; i < numRuns; ++i )
		{
			long startTime = System.currentTimeMillis();
			b.run();
			long endTime = System.currentTimeMillis();
			times.add( endTime - startTime );
		}
		for ( int i = 0; i < numRuns; ++i )
		{
			System.out.println( "run " + i + ": " + times.get( i ) + " ms" );
		}
		System.out.println();
		System.out.println( "median: " + median( times ) + " ms" );
		System.out.println();
	}

	public static void main( String[] args )
	{
		final CellRandomAccessBenchmark randomAccessBenchmark = new CellRandomAccessBenchmark();
		randomAccessBenchmark.createSourceData();

		System.out.println( "benchmarking fill" );
		benchmark( new Benchmark()
		{
			public void run()
			{
				randomAccessBenchmark.fillImage();
			}
		} );
		randomAccessBenchmark.intData = null;
		
		randomAccessBenchmark.intImgCopy = new CellContainerFactory< IntType >( 32 ).create( randomAccessBenchmark.dimensions, new IntType() );
		System.out.println( "benchmarking copy to smaller" );
		benchmark( new Benchmark()
		{
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;

		randomAccessBenchmark.intImgCopy = new CellContainerFactory< IntType >( 50 ).create( randomAccessBenchmark.dimensions, new IntType() );
		System.out.println( "benchmarking copy to larger" );
		benchmark( new Benchmark()
		{
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;
		
		randomAccessBenchmark.intImgCopy = new CellContainerFactory< IntType >( new int[] {32, 64, 16} ).create( randomAccessBenchmark.dimensions, new IntType() );
		System.out.println( "benchmarking copy to mixed" );
		benchmark( new Benchmark()
		{
			public void run()
			{
				randomAccessBenchmark.copyWithSourceIteration( randomAccessBenchmark.intImg, randomAccessBenchmark.intImgCopy );
			}
		} );
		randomAccessBenchmark.intImgCopy = null;		
	}
}
