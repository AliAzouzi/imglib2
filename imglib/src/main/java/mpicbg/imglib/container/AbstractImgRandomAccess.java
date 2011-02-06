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
package mpicbg.imglib.container;

import mpicbg.imglib.Interval;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public abstract class AbstractImgRandomAccess< T extends Type< T > > extends AbstractLocalizableImgSampler< T > implements ImgRandomAccess< T >
{
	/* internal register for position calculation */
	final protected int[] tmp;

	public AbstractImgRandomAccess( final Interval f )
	{
		super( f );

		this.tmp = new int[ n ];
	}

	@Override
	public boolean isOutOfBounds()
	{
		for ( int d = 0; d < n; ++d )
		{
			final long x = position[ d ];
			if ( x < 0 || x >= size[ d ] )
				return true;
		}
		return false;
	}

	@Override
	public void move( final int distance, final int dim )
	{
		move( ( long )distance, dim );
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		setPosition( ( long )position, dim );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final int dist = distance[ d ];

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long dist = distance[ d ];

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long dist = localizable.getLongPosition( d );

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( tmp );
		setPosition( tmp );
	}
}
