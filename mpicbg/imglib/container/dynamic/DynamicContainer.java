/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
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
package mpicbg.imglib.container.dynamic;

import mpicbg.imglib.container.AbstractImg;
import mpicbg.imglib.container.AbstractNativeContainer;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.sampler.dynamic.DynamicBasicRasterIterator;
import mpicbg.imglib.sampler.dynamic.DynamicLocalizingRasterIterator;
import mpicbg.imglib.sampler.dynamic.DynamicPositionableRasterSampler;
import mpicbg.imglib.sampler.dynamic.DynamicOutOfBoundsPositionableRasterSampler;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 * @param <A>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class DynamicContainer< T > extends AbstractImg< T >
{
	final protected int[] step;
	final protected int[] dim;
	
	// we have to overwrite those as this can change during the processing
	protected int numPixels, numEntities;

	public DynamicContainer( final long[] dim, final T type )
	{
		super( dim );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int )dim[ d ];

		this.step = Array.createAllocationSteps( this.dim );
		this.numPixels = ( int ) super.numPixels;
	}

	public int[] getSteps() { return step.clone(); }

	public int getStep( final int dim ) { return step[ dim ]; }

	public final int getPos( final int[] l )
	{
		int i = l[ 0 ];
		for ( int d = 1; d < n; ++d )
			i += l[ d ] * step[ d ];

		return i;
	}

	@Override
	public long numPixels() { return numPixels; }

	@Override
	public ImgCursor< T > cursor()
	{
		return new DynamicBasicRasterIterator< T >( this, image );
	}

	@Override
	public ImgCursor< T > localizingCursor()
	{
		return new DynamicLocalizingRasterIterator< T >( this, image );
	}

	@Override
	public ImgRandomAccess< T > integerRandomAccess()
	{
		return new DynamicPositionableRasterSampler< T >( this, image );
	}

	@Override
	public ImgRandomAccess<T> integerRandomAccess(OutOfBoundsFactory<T, Img<T>> factory)
{
		return new DynamicOutOfBoundsPositionableRasterSampler< T >( this, image, outOfBoundsFactory );
	}


	final public void indexToPosition( int i, final int[] l )
	{
		for ( int d = n - 1; d >= 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
	}

	final public void indexToPosition( int i, final long[] l )
	{
		for ( int d = n - 1; d >= 0; --d )
		{
			final int ld = i / step[ d ];
			l[ d ] = ld;
			i -= ld * step[ d ];
			// i %= step[ d ];
		}
	}

	final public int indexToPosition( int i, final int dim )
	{
		for ( int d = n - 1; d > dim; --d )
			i %= step[ d ];

		return i / step[ dim ];
	}
}
