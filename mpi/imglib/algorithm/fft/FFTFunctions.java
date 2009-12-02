package mpi.imglib.algorithm.fft;

import java.util.concurrent.atomic.AtomicInteger;

import edu.mines.jtk.dsp.FftComplex;
import edu.mines.jtk.dsp.FftReal;

import mpi.imglib.container.array.FakeArray;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.array.ArrayLocalizableCursor;
import mpi.imglib.image.Image;
import mpi.imglib.image.ImageFactory;
import mpi.imglib.multithreading.SimpleMultiThreading;
import mpi.imglib.outside.OutsideStrategyFactory;
import mpi.imglib.type.Type;
import mpi.imglib.type.label.FakeType;
import mpi.imglib.type.numeric.ComplexFloatType;
import mpi.imglib.type.numeric.FloatType;

final public class FFTFunctions 
{
	final public static Image<FloatType> computeInverseFFT( final Image<ComplexFloatType> complex, final int numThreads, 
	                                                        final boolean scale, final boolean cropBack,
	                                                        final int[] originalSize, final int[] originalOffset )
	{
		// not enough memory
		if ( complex == null )
			return null;

		// get the number of dimensions		
		final int numDimensions = complex.getNumDimensions();
			
		// the size in dimension 0 of the output image
		final int nfft = ( complex.getDimension( 0 ) - 1 ) * 2;
		
		// the size of the inverse FFT image
		final int dimensionsReal[] = complex.getDimensions();
		dimensionsReal[ 0 ] = nfft;
		
		// create the output image
		final ImageFactory<FloatType> imgFactory = new ImageFactory<FloatType>( new FloatType(), complex.getStorageFactory() );
		final Image<FloatType> realImage;
		
		if ( cropBack )
			realImage = imgFactory.createImage( originalSize );
		else
			realImage = imgFactory.createImage( dimensionsReal );
		
		// not enough memory
		if ( realImage == null )
			return null;
		
		//
		// do fft in all the other dimensions		
		//	
		for ( int d = numDimensions - 1; d > 0; --d )
		{
			final int dim = d;
			
			final AtomicInteger ai = new AtomicInteger();
			final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );

			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread] = new Thread(new Runnable()
				{
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
												
						final int size = complex.getDimension( dim );
						
						final float[] tempIn = new float[ size * 2 ];						
						final FftComplex fftc = new FftComplex( size );
						
						final LocalizableByDimCursor<ComplexFloatType> cursor = complex.createLocalizableByDimCursor(); 

						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the inverse fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						// get all dimensions except the one we are currently doing the fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = complex.getDimension( d );

						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );
						
						final float[] tempOut = new float[ size * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{
								// update all positions except for the one we are currrently doing the inverse fft on
								cursorDim.getPosition( fakeSize );

								tmp[ dim ] = 0;								
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
									if ( d != dim )
										tmp[ d ] = fakeSize[ countDim++ ];
								
								// update the cursor in the input image to the current dimension position
								cursor.setPosition( tmp );
																
								// get the input line
								for ( int i = 0; i < size; ++i )
								{
									tempIn[ i * 2 ] = cursor.getType().getReal();
									tempIn[ i * 2 + 1 ] = cursor.getType().getComplex();
									cursor.fwd( dim );
								}
								
								// compute the inverse fft
								fftc.complexToComplex( 1, tempIn, tempOut );
								
								// update the cursor in the input image to the current dimension position
								cursor.setPosition( tmp );

								// write back result
								if ( scale )
									for ( int i = 0; i < size; ++i )
									{
										cursor.getType().set( tempOut[ i * 2 ] / size, tempOut[ i * 2 + 1 ] / size );
										cursor.fwd( dim );
									}
								else
									for ( int i = 0; i < size; ++i )
									{
										cursor.getType().set( tempOut[ i * 2 ], tempOut[ i * 2 + 1 ] );
										cursor.fwd( dim );
									}
									
							}							
						}
						
						cursor.close();
						cursorDim.close();
					}
				});
			
			SimpleMultiThreading.startAndJoin( threads );						
		}
		
		//
		// compute inverse fft into the real dimension
		//
		final AtomicInteger ai = new AtomicInteger();
		final Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
										
					final int realSize = dimensionsReal[ 0 ];
					final int complexSize = complex.getDimension( 0 );
					final float[] tempIn = new float[ complexSize * 2 ];				
					final FftReal fft = new FftReal( realSize );

					final int cropX1, cropX2;					
					if ( cropBack )
					{
						cropX1 = originalOffset[ 0 ];
						cropX2 = originalOffset[ 0 ] + originalSize[ 0 ];
					}
					else
					{
						cropX1 = 0;
						cropX2 = realSize;
					}
					
					final LocalizableByDimCursor<ComplexFloatType> cursor = complex.createLocalizableByDimCursor(); 
					final LocalizableByDimCursor<FloatType> cursorOut = realImage.createLocalizableByDimCursor(); 
					
					if ( numDimensions > 1 )
					{
						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						for ( int d = 1; d < numDimensions; ++d )
							fakeSize[ d - 1 ] = complex.getDimension( d );
						
						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );

						final float[] tempOut = new float[ realSize ];
																		
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
A:						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.getPosition( fakeSize );

								tmp[ 0 ] = 0;
								if ( cropBack )
								{
									// check that we are not outside the cropped image, then we do not have to compute the
									// inverse fft here
									for ( int d = 1; d < numDimensions; ++d )
									{
										tmp[ d ] = fakeSize[ d - 1 ];
										if ( tmp[ d ] < originalOffset[ d ] || tmp[ d ] >= originalOffset[ d ] + originalSize[ d ] )
											continue A;
									}
								}
								else
								{
									for ( int d = 1; d < numDimensions; ++d )									
										tmp[ d ] = fakeSize[ d - 1 ];
								}

								// set the cursor to the beginning of the correct line
								cursor.setPosition( tmp );
								
								// fill the input array with complex image data
								for ( int i = 0; i < complexSize; ++i )
								{
									tempIn[ i * 2 ] = cursor.getType().getReal();
									tempIn[ i * 2 + 1 ] = cursor.getType().getComplex();
									cursor.fwd( 0 );
								}
																								
								// compute the fft in dimension 0 ( real -> complex )
								fft.complexToReal( 1, tempIn, tempOut );
										
								// set the cursor in the fft output image to the right line								
								if ( cropBack )
									for ( int d = 1; d < numDimensions; ++d )									
										tmp[ d ] -= originalOffset[ d ];									
								
								cursorOut.setPosition( tmp );
								
								// write back the real data
								if ( scale )
									for ( int x = cropX1; x < cropX2; ++x )
									{
										cursorOut.getType().set( tempOut[ x ] / realSize );
										cursorOut.fwd( 0 );
									}
								else
									for ( int x = cropX1; x < cropX2; ++x )
									{
										cursorOut.getType().set( tempOut[ x ] );
										cursorOut.fwd( 0 );
									}
							}
						}
						
						cursorOut.close();
						cursor.close();
						cursorDim.close();						
					}
					else
					{
						// multithreading makes no sense here
						if ( myNumber == 0)
						{
							// set the cursor to 0 in the first (and only) dimension
							cursor.setPosition( 0, 0 );
							
							// get the input data
							// fill the input array with complex image data
							for ( int i = 0; i < complexSize; ++i )
							{
								tempIn[ i * 2 ] = cursor.getType().getReal();
								tempIn[ i * 2 + 1 ] = cursor.getType().getComplex();
								cursor.fwd( 0 );
							}
							
							// compute the fft in dimension 0 ( real -> complex )
							final float[] tempOut = new float[ realSize ];
							fft.complexToReal( 1, tempIn, tempOut );
							
							// set the cursor in the fft output image to 0 in the first (and only) dimension
							cursorOut.setPosition( 0, 0 );
							
							// write back the real data
							if ( scale )
								for ( int x = cropX1; x < cropX2; ++x )
								{
									cursorOut.getType().set( tempOut[ x ] / realSize );
									cursorOut.fwd( 0 );
								}
							else
								for ( int x = cropX1; x < cropX2; ++x )
								{
									cursorOut.getType().set( tempOut[ x ] );
									cursorOut.fwd( 0 );
								}
						}
						cursorOut.close();
						cursor.close();						
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin(threads);
		
		return realImage;
	}
	
	final public static Image<ComplexFloatType> computeFFT( final Image<FloatType> img, final OutsideStrategyFactory<FloatType> outsideFactory,
	                                                        final int[] imageOffset, final int[] imageSize,
	                                                        final int numThreads, final boolean scale )
	{
		final int numDimensions = img.getNumDimensions();
		
		final int complexSize[] = new int[ numDimensions ];
		
		// the size of the first dimension is changed
		complexSize[ 0 ] = ( imageSize[ 0 ]  / 2 + 1);
		
		for ( int d = 1; d < numDimensions; ++d )
			complexSize[ d ] = imageSize[ d ];
		
		final ImageFactory<ComplexFloatType> imgFactory = new ImageFactory<ComplexFloatType>( new ComplexFloatType(), img.getStorageFactory() );
		final Image<ComplexFloatType> fftImage = imgFactory.createImage( complexSize );
		
		// not enough memory
		if ( fftImage == null )
			return null;
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					
					final int realSize = imageSize[ 0 ];
					final int complexSize = fftImage.getDimension( 0 );
							
					final float[] tempIn = new float[ realSize ];				
					final FftReal fft = new FftReal( realSize );
					
					final LocalizableByDimCursor<FloatType> cursor = img.createLocalizableByDimCursor( outsideFactory );
					final LocalizableByDimCursor<ComplexFloatType> cursorOut = fftImage.createLocalizableByDimCursor(); 
					
					if ( numDimensions > 1 )
					{
						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						final int[] tmp2 = new int[ numDimensions ];
						
						for ( int d = 1; d < numDimensions; ++d )
							fakeSize[ d - 1 ] = imageSize[ d ];
						
						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );

						final float[] tempOut = new float[ complexSize * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{							
								// get all dimensions except the one we are currently doing the fft on
								cursorDim.getPosition( fakeSize );

								tmp[ 0 ] = 0;
								tmp2[ 0 ] = -imageOffset[ 0 ];
								
								for ( int d = 1; d < numDimensions; ++d )
								{
									tmp[ d ] = fakeSize[ d - 1 ];
									tmp2[ d ] = fakeSize[ d - 1 ] - imageOffset[ d ];
								}

								// set the cursor to the beginning of the correct line
								cursor.setPosition( tmp2 );
								
								// fill the input array with image data
								for ( int x = 0; x < realSize; ++x )
								{
									tempIn[ x ] = cursor.getType().get();									
									cursor.fwd( 0 );
								}
																
								// compute the fft in dimension 0 ( real -> complex )
								fft.realToComplex( -1, tempIn, tempOut );
									
								// set the cursor in the fft output image to the right line
								cursorOut.setPosition( tmp );
								
								// write back the fft data
								if ( scale )
									for ( int x = 0; x < complexSize; ++x )
									{
										cursorOut.getType().set( tempOut[ x * 2 ] / realSize, tempOut[ x * 2 + 1 ] / realSize );									
										cursorOut.fwd( 0 );
									}
								else
									for ( int x = 0; x < complexSize; ++x )
									{
										cursorOut.getType().set( tempOut[ x * 2 ], tempOut[ x * 2 + 1 ] );									
										cursorOut.fwd( 0 );
									}
							}
						}
						
						cursorOut.close();
						cursor.close();
						cursorDim.close();						
					}
					else
					{
						// multithreading makes no sense here
						if ( myNumber == 0)
						{
							// set the cursor to 0 in the first (and only) dimension
							cursor.setPosition( -imageOffset[ 0 ], 0 );
							
							// get the input data
							for ( int x = 0; x < realSize; ++x )
							{
								tempIn[ x ] = cursor.getType().get();
								cursor.fwd( 0 );
							}
							
							// compute the fft in dimension 0 ( real -> complex )
							final float[] tempOut = new float[ complexSize * 2 ];
							fft.realToComplex( -1, tempIn, tempOut );
							
							// set the cursor in the fft output image to 0 in the first (and only) dimension
							cursorOut.setPosition( 0, 0 );
							
							// write back the fft data
							if ( scale )
								for ( int x = 0; x < complexSize; ++x )
								{
									cursorOut.getType().set( tempOut[ x * 2 ] / realSize, tempOut[ x * 2 + 1 ] / realSize );
									cursorOut.fwd( 0 );
								}
							else
								for ( int x = 0; x < complexSize; ++x )
								{
									cursorOut.getType().set( tempOut[ x * 2 ], tempOut[ x * 2 + 1 ] );									
									cursorOut.fwd( 0 );
								}
								
						}
						cursorOut.close();
						cursor.close();						
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin(threads);
				
		//
		// do fft in all the other dimensions		
		//	
		for ( int d = 1; d < numDimensions; ++d )
		{
			final int dim = d;
			
			ai.set( 0 );
			threads = SimpleMultiThreading.newThreads( numThreads );

			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread] = new Thread(new Runnable()
				{
					public void run()
					{
						final int myNumber = ai.getAndIncrement();
						
						final int size = fftImage.getDimension( dim );
						
						final float[] tempIn = new float[ size * 2 ];						
						final FftComplex fftc = new FftComplex( size );
						
						final LocalizableByDimCursor<ComplexFloatType> cursor = fftImage.createLocalizableByDimCursor(); 

						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						// get all dimensions except the one we are currently doing the fft on
						int countDim = 0;						
						for ( int d = 0; d < numDimensions; ++d )
							if ( d != dim )
								fakeSize[ countDim++ ] = fftImage.getDimension( d );

						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );
						
						final float[] tempOut = new float[ size * 2 ];
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();							

							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{
								// update all positions except for the one we are currrently doing the fft on
								cursorDim.getPosition( fakeSize );

								tmp[ dim ] = 0;								
								countDim = 0;						
								for ( int d = 0; d < numDimensions; ++d )
									if ( d != dim )
										tmp[ d ] = fakeSize[ countDim++ ];
								
								// update the cursor in the input image to the current dimension position
								cursor.setPosition( tmp );
								
								// get the input line
								for ( int i = 0; i < size; ++i )
								{
									tempIn[ i * 2 ] = cursor.getType().getReal();
									tempIn[ i * 2 + 1 ] = cursor.getType().getComplex();
									cursor.fwd( dim );
								}
								
								// compute the fft in dimension dim (complex -> complex) 
								fftc.complexToComplex( -1, tempIn, tempOut);
	
								// set the cursor to the right line
								cursor.setPosition( tmp );
								
								// write back result
								if ( scale )								
									for ( int i = 0; i < size; ++i )
									{
										cursor.getType().set( tempOut[ i * 2 ] / size, tempOut[ i * 2 + 1 ] / size );
										cursor.fwd( dim );
									}
								else
									for ( int i = 0; i < size; ++i )
									{
										cursor.getType().set( tempOut[ i * 2 ], tempOut[ i * 2 + 1 ] );
										cursor.fwd( dim );
									}									
							}
						}
						
						cursor.close();
						cursorDim.close();
					}
				});
			
			SimpleMultiThreading.startAndJoin( threads );
		}
				
		return fftImage;
	}
	
	final private static <T extends Type<T>> void rearrangeQuadrantsSingleDimension( final Image<T> img )
	{
		final int sizeDim = img.getDimension( 0 );					
		final int halfSizeDim = sizeDim / 2;

		final T buffer = img.createType();
		
		final LocalizableByDimCursor<T> cursor1 = img.createLocalizableByDimCursor(); 
		final LocalizableByDimCursor<T> cursor2 = img.createLocalizableByDimCursor(); 

		// update the first cursor in the image to the zero position
		cursor1.setPosition( 0, 0 );
		
		// and a second one to the middle for rapid exchange of the quadrants
		cursor2.setPosition( 0,  halfSizeDim );
						
		// now do a triangle-exchange
		for ( int i = 0; i < halfSizeDim ; ++i )
		{
			// cache first "half" to buffer
			buffer.set( cursor1.getType() );

			// move second "half" to first "half"
			cursor1.getType().set( cursor2.getType() );
			
			// move data in buffer to second "half"
			cursor2.getType().set( buffer );
			
			// move both cursors forward
			cursor1.fwd( 0 ); 
			cursor2.fwd( 0 ); 
		}
		
		cursor1.close();
		cursor2.close();		
	}

	final private static <T extends Type<T>> void rearrangeQuadrantDim( final Image<T> fftImage, final int dim, final int numThreads )
	{
		final int numDimensions = fftImage.getNumDimensions();
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();

					final int sizeDim = fftImage.getDimension( dim );
					final int halfSizeDim = sizeDim / 2;
					
					// if we have an odd image size, e.g. 315 the lower half has an offset of 1
					final int oddOffset = sizeDim % 2;
		
					final T buffer = fftImage.createType();
					
					final LocalizableByDimCursor<T> cursor1 = fftImage.createLocalizableByDimCursor(); 
					final LocalizableByDimCursor<T> cursor2 = fftImage.createLocalizableByDimCursor(); 
		
					/**
					 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
					 */	
					final int[] fakeSize = new int[ numDimensions - 1 ];
					final int[] tmp = new int[ numDimensions ];
					
					// get all dimensions except the one we are currently swapping
					int countDim = 0;						
					for ( int d = 0; d < numDimensions; ++d )
						if ( d != dim )
							fakeSize[ countDim++ ] = fftImage.getDimension( d );
					
					final ArrayLocalizableCursor<FakeType> cursorDim = 
						new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );
		
					// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
					while( cursorDim.hasNext() )
					{
						cursorDim.fwd();
						
						if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
						{							
							// update all positions except for the one we are currrently doing the fft on
							cursorDim.getPosition( fakeSize );
			
							tmp[ dim ] = 0;								
							countDim = 0;						
							for ( int d = 0; d < numDimensions; ++d )
								if ( d != dim )
									tmp[ d ] = fakeSize[ countDim++ ];
							
							// update the first cursor in the image to the zero position
							cursor1.setPosition( tmp );
							
							// and a second one to the middle for rapid exchange of the quadrants
							tmp[ dim ] = halfSizeDim + oddOffset;
							cursor2.setPosition( tmp );
											
							// now do a triangle-exchange
							for ( int i = 0; i < halfSizeDim ; ++i )
							{
								// cache first "half" to buffer
								buffer.set( cursor1.getType() );
			
								// move second "half" to first "half"
								cursor1.getType().set( cursor2.getType() );
								
								// move data in buffer to second "half"
								cursor2.getType().set( buffer );
								
								// move both cursors forward
								cursor1.fwd( dim ); 
								cursor2.fwd( dim ); 
							}
						}
					}
					
					cursor1.close();
					cursor2.close();
					cursorDim.close();
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );						
		
	}
	

	final public static <T extends Type<T>> void rearrangeAllQuadrants( final Image<T> img, final int numThreads )
	{
		final int numDimensions = img.getNumDimensions();

		if ( numDimensions == 1 )
			rearrangeQuadrantsSingleDimension( img );
		else
			for ( int d = 0; d < numDimensions; ++d )
				rearrangeQuadrantDim( img, d, numThreads );
	}
	
	final public static <T extends Type<T>> void rearrangeFFTQuadrants( final Image<T> fftImage, final int numThreads )
	{
		final int numDimensions = fftImage.getNumDimensions();

		if ( numDimensions == 1 )
		{
			final int sizeDim = fftImage.getDimension( 0 );					
			final int halfSizeDim = sizeDim / 2;
			final int sizeDimMinus1 = sizeDim - 1;

			final T buffer = fftImage.createType();
			
			final LocalizableByDimCursor<T> cursor1 = fftImage.createLocalizableByDimCursor(); 
			final LocalizableByDimCursor<T> cursor2 = fftImage.createLocalizableByDimCursor(); 

			// update the first cursor in the image to the zero position
			cursor1.setPosition( 0, 0 );
			
			// and a second one to the middle for rapid exchange of the quadrants
			cursor2.setPosition( 0,  sizeDimMinus1 );
							
			// now do a triangle-exchange
			for ( int i = 0; i < halfSizeDim ; ++i )
			{
				// cache first "half" to buffer
				buffer.set( cursor1.getType() );

				// move second "half" to first "half"
				cursor1.getType().set( cursor2.getType() );
				
				// move data in buffer to second "half"
				cursor2.getType().set( buffer );
				
				// move both cursors forward
				cursor1.fwd( 0 ); 
				cursor2.bck( 0 ); 
			}	
			
			cursor1.close();
			cursor2.close();
		}
		else
		{			
			//swap in dimension 0
			final AtomicInteger ai = new AtomicInteger(0);
			Thread[] threads = SimpleMultiThreading.newThreads( numThreads );
			
			for (int ithread = 0; ithread < threads.length; ++ithread)
				threads[ithread] = new Thread(new Runnable()
				{
					public void run()
					{
						final int myNumber = ai.getAndIncrement();

						final int sizeDim = fftImage.getDimension( 0 );					
						final int halfSizeDim = sizeDim / 2;
						final int sizeDimMinus1 = sizeDim - 1;
			
						final T buffer = fftImage.createType();
						
						final LocalizableByDimCursor<T> cursor1 = fftImage.createLocalizableByDimCursor(); 
						final LocalizableByDimCursor<T> cursor2 = fftImage.createLocalizableByDimCursor(); 
						
						/**
						 * Here we "misuse" a ArrayLocalizableCursor to iterate through all dimensions except the one we are computing the fft in 
						 */	
						final int[] fakeSize = new int[ numDimensions - 1 ];
						final int[] tmp = new int[ numDimensions ];
						
						for ( int d = 1; d < numDimensions; ++d )
							fakeSize[ d - 1 ] = fftImage.getDimension( d );
						
						final ArrayLocalizableCursor<FakeType> cursorDim = 
							new ArrayLocalizableCursor<FakeType>( new FakeArray<FakeType>( fakeSize ), null, new FakeType() );
						
						// iterate over all dimensions except the one we are computing the fft in, which is dim=0 here
						while( cursorDim.hasNext() )
						{
							cursorDim.fwd();
							
							if ( cursorDim.getPosition( 0 ) % numThreads == myNumber )
							{							
								// update all positions except for the one we are currrently doing the fft on
								cursorDim.getPosition( fakeSize );
				
								tmp[ 0 ] = 0;								
								for ( int d = 1; d < numDimensions; ++d )
									tmp[ d ] = fakeSize[ d - 1 ];
								
								// update the first cursor in the image to the zero position
								cursor1.setPosition( tmp );
								
								// and a second one to the middle for rapid exchange of the quadrants
								tmp[ 0 ] = sizeDimMinus1;
								cursor2.setPosition( tmp );
												
								// now do a triangle-exchange
								for ( int i = 0; i < halfSizeDim ; ++i )
								{
									// cache first "half" to buffer
									buffer.set( cursor1.getType() );
				
									// move second "half" to first "half"
									cursor1.getType().set( cursor2.getType() );
									
									// move data in buffer to second "half"
									cursor2.getType().set( buffer );
									
									// move both cursors forward
									cursor1.fwd( 0 ); 
									cursor2.bck( 0 ); 
								}
							}
						}	
						
						cursor1.close();
						cursor2.close();
					}
				});
			
			SimpleMultiThreading.startAndJoin( threads );		
			
			for ( int d = 1; d < numDimensions; ++d )
				rearrangeQuadrantDim( fftImage, d, numThreads );
		}				
	}	
}
