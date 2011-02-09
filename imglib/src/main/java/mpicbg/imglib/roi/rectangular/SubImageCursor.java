/**
 * Copyright (c) 20011, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the ImgLib/Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.roi.rectangular;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.EuclideanSpace;
import mpicbg.imglib.InjectiveInterval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.Sampler;
import mpicbg.imglib.util.Util;

/**
 * 
 * @author Stephan Preibisch (stephan.preibisch@gmx.de)
 * 
 */
public class SubImageCursor< T > implements Sampler< T >, EuclideanSpace, InjectiveInterval, Cursor< T >, java.util.Iterator< T >
{
	final SubImage<T> subImage;
	final RandomAccess<T> access;
	
	// true means go forward, false go backward
	final boolean[] currentDirectionDim;
	
	final long[] max, size, position;
	final int n;
	final long numPixelsMinus1;
	
	long i;
	
	public SubImageCursor( final SubImage<T> subImage )
	{
		this.subImage = subImage;
		this.access = subImage.createRandomAccessForSource();
		
		this.n = subImage.numDimensions();
				
		this.size = Util.intervalDimensions( subImage );
		this.max = Util.intervalMax( subImage );
		this.position = new long[ n ];
		
		this.currentDirectionDim = new boolean[ n ]; 
		
		int count = 1;
		for ( int d = 0; d < n; ++d )
			count *= size[ d ];
		
		numPixelsMinus1 = count - 1;
		
		reset();
	}
	/*
	 * From Dr. Schindelin, not tested yet
	 * 
	indexToPosition(int index, int[] size, int[] pos) {
	 int j, cumulSize = 1, cumulPos = 0;
	
	 for (j = 0; j < size.length; j++)
	  cumulSize *= size[j];
	
	 while (--j >= 0) {
	  cumulSize /= size[j];
	  dimPos = (index / cumulSize) % size[j];
	  if ((cumulPos % 2) == 1)
	   pos[j] = size[j] - 1 - dimPos;
	  else
	   pos[j] = dimPos;
	  cumulPos += dimPos;
	 }
	}
	
	indexToDirection(int index, int[] size, int[] direction) {
	 int j, cumulSize = 1;
	
	 for (j = 0; j < size.length; j++) {
	  cumulSize *= size[j];
	  if ((index % cumulSize) != 0) {
	   direction[j] = +1;
	   break;
	  }
	  direction[j] = 0;
	 }
	 for (k = j + 1; k < size.length; k++) {
	  cumulSize *= size[k];
	  int dimPos = (index / cumulSize) % size[k];
	  if ((dimPos % 2) == 1)
	   direction[j] *= -1;
	  direction[k] = 0;
	 }
	}	 
	*/
	
	/* Iterator */
	
	@Override
	public T get()  { return access.get(); }

	@Override
	public T getType() { return get();	}

	@Override
	public void jumpFwd( final long steps ) 
	{ 
		// TODO: This has to be more efficient!
		for ( long f = 0; f < steps; ++f )
			fwd();
	}

	@Override
	public void fwd() 
	{
		++i;
		
		for ( int d = 0; d < n; ++d )
		{
			if ( currentDirectionDim[ d ] )
			{
				if ( position[ d ] < size[ d ] - 1 )
				{
					access.fwd( d );
					++position[ d ];
					
					// revert the direction of all lower dimensions
					for ( int e = 0; e < d; ++e )
						currentDirectionDim[ e ] = !currentDirectionDim[ e ];
					
					return;
				}				
			}
			else
			{
				if ( position[ d ] > 0 )
				{
					access.bck( d );
					--position[ d ];

					// revert the direction of all lower dimensions
					for ( int e = 0; e < d; ++e )
						currentDirectionDim[ e ] = !currentDirectionDim[ e ];
					
					return;
				}
			}
		}
	}

	@Override
	public void reset()
	{ 
		i = -1;
		access.setPosition( subImage );
		access.bck( 0 );
			
		for ( int d = 0; d < n; ++d )
		{
			// true means go forward
			currentDirectionDim[ d ] = true;
			position[ d ] = 0;
		}
		
		position[ 0 ] = -1;
	}

	@Override
	public boolean hasNext() { return i < numPixelsMinus1; }

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	/* Interval */
	
	@Override
	public long min( final int d ) { return 0; }

	@Override
	public void min( final long[] min )
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}

	@Override
	public long max( final int d ) { return max[ d ]; }

	@Override
	public void max( final long[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}

	@Override
	public void dimensions( final long[] dimensions )
	{
		for ( int d = 0; d < n; ++d )
			dimensions[ d ] = size[ d ];
	}

	@Override
	public long dimension( final int d ) { return size[ d ]; }

	@Override
	public double realMin( final int d ) { return 0; }

	@Override
	public void realMin( final double[] min ) 
	{
		for ( int d = 0; d < n; ++d )
			min[ d ] = 0;
	}

	@Override
	public double realMax( final int d ) { return max[ d ]; }

	@Override
	public void realMax( final double[] max )
	{
		for ( int d = 0; d < n; ++d )
			max[ d ] = this.max[ d ];
	}
	
	/* Localize */

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}

	@Override
	public float getFloatPosition( final int dim ) { return position[ dim ]; }

	@Override
	public double getDoublePosition( final int dim ) { return position[ dim ]; }

	@Override
	public void localize( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = (int)this.position[ d ];
	}

	@Override
	public void localize( final long[] position ) 
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}

	@Override
	public int getIntPosition( final int dim )  { return (int)position[ dim ]; }

	@Override
	public long getLongPosition( final int dim )  { return position[ dim ]; }

	/* Euclidean Space */
	
	@Override
	public int numDimensions() { return n; }
	
	/* Iterator */
	
	@Override
	public void remove() {}
	
	@Override
	public String toString() { return Util.printCoordinates( this ) + ": " + get(); }
}
