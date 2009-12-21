package mpicbg.imglib.algorithm.fft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.fft.FourierTransform.Rearrangement;
import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
import mpicbg.imglib.outside.OutsideStrategyPeriodicFactory;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.ComplexFloatType;
import mpicbg.imglib.type.numeric.FloatType;

public class PhaseCorrelation<T extends NumericType<T>, S extends NumericType<S>> implements MultiThreaded, Algorithm, Benchmark
{
	final int numDimensions;
	boolean computeFFTinParalell = true;
	Image<T> image1;
	Image<S> image2;
	int numPeaks;
	int[] minOverlapPx;
	float normalizationThreshold;
	boolean verifyWithCrossCorrelation;
	ArrayList<PhaseCorrelationPeak> phaseCorrelationPeaks;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public PhaseCorrelation( final Image<T> image1, final Image<S> image2, final int numPeaks, final boolean verifyWithCrossCorrelation )
	{
		this.image1 = image1;
		this.image2 = image2;
		this.numPeaks = numPeaks;
		this.verifyWithCrossCorrelation = verifyWithCrossCorrelation;

		this.numDimensions = image1.getNumDimensions();
		this.normalizationThreshold = 1E-5f;
		
		this.minOverlapPx = new int[ numDimensions ];		
		setMinimalPixelOverlap( 3 );
		
		setNumThreads();
		processingTime = -1;
	}
	
	public PhaseCorrelation( final Image<T> image1, final Image<S> image2 )
	{
		this( image1, image2, 5, true );
	}
	
	public void setComputeFFTinParalell( final boolean computeFFTinParalell ) { this.computeFFTinParalell = computeFFTinParalell; }
	public void setInvestigateNumPeaks( final int numPeaks ) { this.numPeaks = numPeaks; }
	public void setNormalizationThreshold( final int normalizationThreshold ) { this.normalizationThreshold = normalizationThreshold; }
	public void setVerifyWithCrossCorrelation( final boolean verifyWithCrossCorrelation ) { this.verifyWithCrossCorrelation = verifyWithCrossCorrelation; }
	public void setMinimalPixelOverlap( final int[] minOverlapPx ) { this.minOverlapPx = minOverlapPx.clone(); } 
	public void setMinimalPixelOverlap( final int minOverlapPx ) 
	{ 
		for ( int d = 0; d < numDimensions; ++d )
			this.minOverlapPx[ d ] = minOverlapPx;
	}
	
	public boolean getComputeFFTinParalell() { return computeFFTinParalell; }
	public int getInvestigateNumPeaks() { return numPeaks; }
	public float getNormalizationThreshold() { return normalizationThreshold; }
	public boolean getVerifyWithCrossCorrelation() { return verifyWithCrossCorrelation; }
	public int[] getMinimalPixelOverlap() { return minOverlapPx.clone(); }
	public PhaseCorrelationPeak getShift() { return phaseCorrelationPeaks.get( phaseCorrelationPeaks.size() -1 ); }
	public ArrayList<PhaseCorrelationPeak> getAllShifts() { return phaseCorrelationPeaks; }
	
	@Override
	public boolean process()
	{		
		// get the maximal dimensions of both images
		final int[] maxDim = getMaxDim( image1, image2 );
		
		// compute fourier transforms
		final FourierTransform<T> fft1 = new FourierTransform<T>( image1 );
		final FourierTransform<S> fft2 = new FourierTransform<S>( image2 );
		fft1.setRelativeImageExtension( 0.1f );
		fft2.setRelativeImageExtension( 0.1f );
		fft1.setRelativeFadeOutDistance( 0.1f );
		fft2.setRelativeFadeOutDistance( 0.1f );		
		fft1.setRearrangement( Rearrangement.Unchanged );
		fft2.setRearrangement( Rearrangement.Unchanged );
		fft1.setExtendedOriginalImageSize( maxDim );
		fft2.setExtendedOriginalImageSize( maxDim );
				
		if ( !fft1.checkInput() )
		{
			errorMessage = "Fourier Transform of first image failed: " + fft1.getErrorMessage(); 
			return false;
		}
			
		if ( !fft2.checkInput() )
		{
			errorMessage = "Fourier Transform of second image failed: " + fft2.getErrorMessage(); 
			return false;
		}
		
		//
		// compute the fft's
		//
		if ( !computeFFT( fft1, fft2 ) )
		{
			errorMessage = "Fourier Transform of failed: fft1=" + fft1.getErrorMessage() + " fft2=" + fft2.getErrorMessage();
			return false;
		}
				
		final Image<ComplexFloatType> fftImage1 = fft1.getResult();
		final Image<ComplexFloatType> fftImage2 = fft2.getResult();
		
		//
		// normalize and compute complex conjugate of fftImage2
		//
		normalizeAndConjugate( fftImage1, fftImage2 );
		
		//
		// multiply fftImage1 and fftImage2 which yields the phase correlation spectrum
		//
		multiplyInPlace( fftImage1, fftImage2 );
		
		//
		// invert fftImage1 which contains the phase correlation spectrum
		//
		final InverseFourierTransform<FloatType> invFFT = new InverseFourierTransform<FloatType>( fftImage1, fft1, new FloatType() );
		invFFT.setInPlaceTransform( true );
		invFFT.setCropBackToOriginalSize( false );
		
		if ( !invFFT.checkInput() || !invFFT.process() )
		{
			errorMessage = "Inverse Fourier Transform of failed: " + invFFT.getErrorMessage();
			return false;			
		}

		//
		// close the fft images
		//
		fftImage1.close();
		fftImage2.close();
		
		final Image<FloatType> invPCM = invFFT.getResult();
		
		invPCM.getDisplay().setMinMax();
		invPCM.setName("invPCM");
		ImageJFunctions.copyToImagePlus( invPCM ).show();
		
		//
		// extract the peaks
		//
		phaseCorrelationPeaks = extractPhaseCorrelationPeaks( invPCM, numPeaks, fft1, fft2 );
		
		if ( !verifyWithCrossCorrelation )
			return true;

		verifyWithCrossCorrelation( phaseCorrelationPeaks, invPCM.getDimensions(), image1, image2 );
		
		return true;
	}
	
	protected void verifyWithCrossCorrelation( final ArrayList<PhaseCorrelationPeak> peakList, final int[] dimInvPCM, final Image<T> image1, final Image<S> image2 )
	{
		final boolean[][] coordinates = MathLib.getRecursiveCoordinates( numDimensions );
		
		final ArrayList<PhaseCorrelationPeak> newPeakList = new ArrayList<PhaseCorrelationPeak>();
		
		//
		// get all the different possiblities
		//
		for ( final PhaseCorrelationPeak peak : peakList )
		{
			for ( int i = 0; i < coordinates.length; ++i )
			{
				final boolean[] currentPossiblity = coordinates[ i ];
				
				final int[] peakPosition = peak.getPosition();
				
				for ( int d = 0; d < currentPossiblity.length; ++d )
				{
					if ( currentPossiblity[ d ] )
					{
						if ( peakPosition[ d ]  < 0 )
							peakPosition[ d ] += dimInvPCM[ d ];
						else
							peakPosition[ d ] -= dimInvPCM[ d ];
					}
				}				
				newPeakList.add( new PhaseCorrelationPeak( peakPosition, peak.getPhaseCorrelationPeak() ) );
			}			
		}
		
		
		//
		// test them multithreaded
		//
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );
		final int numThreads = threads.length;
			
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement();
					
					for ( int i = 0; i < newPeakList.size(); ++i )
						if ( i % numThreads == myNumber )
						{
							final PhaseCorrelationPeak peak = newPeakList.get( i );
							peak.setCrossCorrelationPeak( (float)testCrossCorrelation( peak, image1, image2, minOverlapPx ) );
							
							// sort by cross correlation peak
							peak.setSortPhaseCorrelation( false );
						}
											
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );
		
		// update old list and sort
		peakList.clear();
		peakList.addAll( newPeakList );
		Collections.sort( peakList );		
	}

	public static <T extends NumericType<T>, S extends NumericType<S>> double testCrossCorrelation( final PhaseCorrelationPeak peak, final Image<T> image1, final Image<S> image2 )
	{
		return testCrossCorrelation( peak, image1, image2, MathLib.getArrayFromValue( 5, image1.getNumDimensions()) );
	}

	public static <T extends NumericType<T>, S extends NumericType<S>> double testCrossCorrelation( final PhaseCorrelationPeak peak, final Image<T> image1, final Image<S> image2, final int minOverlapPx )
	{
		return testCrossCorrelation( peak, image1, image2, MathLib.getArrayFromValue( minOverlapPx, image1.getNumDimensions()) );
	}
	
	public static <T extends NumericType<T>, S extends NumericType<S>> double testCrossCorrelation( final PhaseCorrelationPeak peak, final Image<T> image1, final Image<S> image2, final int[] minOverlapPx )
	{
		final int numDimensions = image1.getNumDimensions();
		double correlationCoefficient = 0;
		
		final int[] shift = peak.getPosition();
		final int[] overlapSize = new int[ numDimensions ];
		final int[] offsetImage1 = new int[ numDimensions ];
		final int[] offsetImage2 = new int[ numDimensions ];
		
		int numPixels = 1;
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( shift[ d ] >= 0 )
			{
				// two possiblities
				//
				//               shift=start              end
				//                 |					   |
				// A: Image 1 ------------------------------
				//    Image 2      ----------------------------------
				//
				//               shift=start	    end
				//                 |			     |
				// B: Image 1 ------------------------------
				//    Image 2      -------------------
				
				// they are not overlapping ( this might happen due to fft zeropadding and extension
				if ( shift[ d ] >= image1.getDimension( d ) )
					return 0;
				
				offsetImage1[ d ] = shift[ d ];
				offsetImage2[ d ] = 0;
				overlapSize[ d ] = Math.min( image1.getDimension( d ) - shift[ d ],  image2.getDimension( d ) );
			}
			else
			{
				// two possiblities
				//
				//          shift start                	  end
				//            |	   |			`		   |
				// A: Image 1      ------------------------------
				//    Image 2 ------------------------------
				//
				//          shift start	     end
				//            |	   |          |
				// B: Image 1      ------------
				//    Image 2 -------------------
				
				// they are not overlapping ( this might happen due to fft zeropadding and extension
				if ( shift[ d ] >= image2.getDimension( d ) )
					return 0;

				offsetImage1[ d ] = 0;
				offsetImage2[ d ] = -shift[ d ];
				overlapSize[ d ] = Math.min( image2.getDimension( d ) + shift[ d ],  image1.getDimension( d ) );				
			}
			
			numPixels *= overlapSize[ d ];
			
			if ( overlapSize[ d ] < minOverlapPx[ d ] )
				return 0;
		}
		
		final LocalizableByDimCursor<T> cursor1 = image1.createLocalizableByDimCursor();
		final LocalizableByDimCursor<S> cursor2 = image2.createLocalizableByDimCursor();
		
		final RegionOfInterestCursor<T> roiCursor1 = cursor1.createRegionOfInterestCursor( offsetImage1, overlapSize );
		final RegionOfInterestCursor<S> roiCursor2 = cursor2.createRegionOfInterestCursor( offsetImage2, overlapSize );
						
		//
		// compute average
		//
		double avg1 = 0;
		double avg2 = 0;
		
		while ( roiCursor1.hasNext() )
		{
			roiCursor1.fwd();
			roiCursor2.fwd();

			avg1 += cursor1.getType().getReal();
			avg2 += cursor2.getType().getReal();
		}

		avg1 /= (double) numPixels;
		avg2 /= (double) numPixels;
				
		//
		// compute cross correlation
		//
		roiCursor1.reset();
		roiCursor2.reset();
				
		double var1 = 0, var2 = 0;
		double coVar = 0;
		
		while ( roiCursor1.hasNext() )
		{
			roiCursor1.fwd();
			roiCursor2.fwd();

			final float pixel1 = cursor1.getType().getReal();
			final float pixel2 = cursor2.getType().getReal();
			
			final double dist1 = pixel1 - avg1;
			final double dist2 = pixel2 - avg2;

			coVar += dist1 * dist2;
			var1 += dist1 * dist1;
			var2 += dist2 * dist2;
		}		
		
		var1 /= (double) numPixels;
		var2 /= (double) numPixels;
		coVar /= (double) numPixels;

		double stDev1 = Math.sqrt(var1);
		double stDev2 = Math.sqrt(var2);

		// all pixels had the same color....
		if (stDev1 == 0 || stDev2 == 0)
		{
			if ( stDev1 == stDev2 && avg1 == avg2 )
				return 1;
			else
				return 0;
		}

		// compute correlation coeffienct
		correlationCoefficient = coVar / (stDev1 * stDev2);
		
		roiCursor1.close();
		roiCursor2.close();
		cursor1.close();
		cursor2.close();
		
		return correlationCoefficient;
	}
	
	protected ArrayList<PhaseCorrelationPeak> extractPhaseCorrelationPeaks( final Image<FloatType> invPCM, final int numPeaks,
	                                                                        final FourierTransform<?> fft1, final FourierTransform<?> fft2 )
	{
		final ArrayList<PhaseCorrelationPeak> peakList = new ArrayList<PhaseCorrelationPeak>();
		
		for ( int i = 0; i < numPeaks; ++i )
			peakList.add( new PhaseCorrelationPeak( new int[ numDimensions ], Float.MIN_VALUE) );

		final LocalizableByDimCursor<FloatType> cursor = invPCM.createLocalizableByDimCursor( new OutsideStrategyPeriodicFactory<FloatType>() );
		final LocalNeighborhoodCursor<FloatType> localCursor = cursor.createLocalNeighborhoodCursor();
				
		final int[] originalOffset1 = fft1.getOriginalOffset();
		final int[] originalOffset2 = fft2.getOriginalOffset();

		final int[] offset = new int[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
			offset[ d ] = originalOffset2[ d ] - originalOffset1[ d ];
		
		final int[] imgSize = invPCM.getDimensions();

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			
			// set the local cursor to the current position of the mother cursor
			localCursor.update();
			
			// the value we are checking for if it is a maximum
			final float value = cursor.getType().get();
			boolean isMax = true;
			
			// iterate over local environment while value is still the maximum
			while ( localCursor.hasNext() && isMax )
			{
				localCursor.fwd();								
				isMax = ( cursor.getType().get() <= value );
			}
			
			// reset the mothercursor and this cursor
			localCursor.reset();

			if ( isMax )
			{
				float lowestValue = Float.MAX_VALUE;
				int lowestValueIndex = -1;
				
				for ( int i = 0; i < numPeaks; ++i )
				{
					final float v = peakList.get( i ).getPhaseCorrelationPeak();
					
					if ( v < lowestValue )
					{
						lowestValue = v;
						lowestValueIndex = i;
					}
				}
				
				// if this value is bigger than the lowest entry we replace it 
				if ( value > lowestValue )
				{
					// remove lowest entry
					peakList.remove( lowestValueIndex );

					// add new peak
					final int[] position = cursor.getPosition();
					
					for ( int d = 0; d < numDimensions; ++d )
					{
						position[ d ] = ( position[ d ] + offset[ d ] ) % imgSize[ d ];
						
						if ( position[ d ] > imgSize[ d ] / 2 )
							position[ d ] = position[ d ] - imgSize[ d ];
					}

					final PhaseCorrelationPeak pcp = new PhaseCorrelationPeak( position, value );
					pcp.setOriginalInvPCMPosition( cursor.getPosition() );
					peakList.add( pcp );
				}
			}			
		}
		
		// sort list 
		Collections.sort( peakList );
						
		return peakList;
	}
	
	protected static int[] getMaxDim( final Image<?> image1, final Image<?> image2 )
	{
		final int[] maxDim = new int[ image1.getNumDimensions() ];
		
		for ( int d = 0; d < image1.getNumDimensions(); ++d )
			maxDim[ d ] = Math.max( image1.getDimension( d ), image2.getDimension( d ) );
		
		return maxDim;
	}
	
	protected void multiplyInPlace( final Image<ComplexFloatType> fftImage1, final Image<ComplexFloatType> fftImage2 )
	{
		final Cursor<ComplexFloatType> cursor1 = fftImage1.createCursor();
		final Cursor<ComplexFloatType> cursor2 = fftImage2.createCursor();
		
		while ( cursor1.hasNext() )
		{
			cursor1.fwd();
			cursor2.fwd();
			
			cursor1.getType().mul( cursor2.getType() );
		}
				
		cursor1.close();
		cursor2.close();
	}
	
	protected void normalizeAndConjugate( final Image<ComplexFloatType> fftImage1, final Image<ComplexFloatType> fftImage2 )
	{
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( Math.min( 2, numThreads ) );
		final int numThreads = threads.length;
			
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement(); 
					
					if ( numThreads == 1 )
					{
						normalizeComplexImage( fftImage1, normalizationThreshold );
						normalizeAndConjugateComplexImage( fftImage2, normalizationThreshold );
					}
					else
					{
						if ( myNumber == 0 )
							normalizeComplexImage( fftImage1, normalizationThreshold );
						else
							normalizeAndConjugateComplexImage( fftImage2, normalizationThreshold );
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );		
	}
	
	private static final void normalizeComplexImage( final Image<ComplexFloatType> fftImage, final float normalizationThreshold )
	{
		final Cursor<ComplexFloatType> cursor = fftImage.createCursor();

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.getType().normalizeLength( normalizationThreshold );
		}
				
		cursor.close();		
	}
	
	private static final void normalizeAndConjugateComplexImage( final Image<ComplexFloatType> fftImage, final float normalizationThreshold )
	{
		final Cursor<ComplexFloatType> cursor = fftImage.createCursor();
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			
			cursor.getType().normalizeLength( normalizationThreshold );
			cursor.getType().complexConjugate();
		}
				
		cursor.close();		
	}
		
	protected boolean computeFFT( final FourierTransform<T> fft1, final FourierTransform<S> fft2 )
	{
		// use two threads in paralell if wanted
		final int minThreads = computeFFTinParalell ? 2 : 1;
		
		final AtomicInteger ai = new AtomicInteger(0);
		Thread[] threads = SimpleMultiThreading.newThreads( Math.min( minThreads, numThreads ) );
		final int numThreads = threads.length;
		
		final boolean[] sucess = new boolean[ 2 ];
		
		for (int ithread = 0; ithread < threads.length; ++ithread)
			threads[ithread] = new Thread(new Runnable()
			{
				public void run()
				{
					final int myNumber = ai.getAndIncrement(); 
					
					if ( numThreads == 1 )
					{
						fft1.setNumThreads( getNumThreads() );
						fft2.setNumThreads( getNumThreads() );
						sucess[ 0 ] = fft1.process();
						sucess[ 1 ] = fft2.process();
					}
					else
					{
						if ( myNumber == 0 )
						{
							fft1.setNumThreads( getNumThreads() / 2 );
							sucess[ 0 ] = fft1.process();							
						}
						else
						{
							fft2.setNumThreads( getNumThreads() / 2 );
							sucess[ 1 ] = fft2.process();														
						}
					}
				}
			});
		
		SimpleMultiThreading.startAndJoin( threads );
		
		return sucess[ 0 ] && sucess[ 1 ]; 
	}
	
	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }

	@Override
	public int getNumThreads() { return numThreads; }	

	@Override
	public boolean checkInput() 
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}
		
		if ( image1 == null || image2 == null)
		{
			errorMessage = "One of the input images is null";
			return false;
		}
		
		if ( image1.getNumDimensions() != image2.getNumDimensions() )
		{
			errorMessage = "Dimensionality of images is not the same";
			return false;
		}
		
		return true;
	}

	@Override
	public String getErrorMessage()  { return errorMessage; }
	
}
