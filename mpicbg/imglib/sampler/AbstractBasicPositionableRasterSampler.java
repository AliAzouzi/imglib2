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
package mpicbg.imglib.sampler;

import mpicbg.imglib.IntegerInterval;
import mpicbg.imglib.IntegerLocalizable;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public abstract class AbstractBasicPositionableRasterSampler< T extends Type< T > > extends AbstractLocalizableRasterSampler< T > implements PositionableRasterIntervalSampler< T >
{
	/* internal register for position calculation */
	final protected int[] tmp;

	public AbstractBasicPositionableRasterSampler( final IntegerInterval f )
	{
		super( f );

		this.tmp = new int[ n ];
	}

	@Override
	public boolean isOutOfBounds()
	{
		for ( int d = 0; d < n; ++d )
		{
			final int x = position[ d ];
			if ( x < 0 || x >= dimensions[ d ] )
				return true;
		}
		return false;
	}

	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int ) distance, dim );
	}

	@Override
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int ) position, dim );
	}

	@Override
	public void moveTo( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final int dist = pos[ d ] - getIntPosition( d );

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void moveTo( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long dist = pos[ d ] - getLongPosition( d );

			if ( dist != 0 )
				move( dist, d );
		}
	}

	@Override
	public void moveTo( final IntegerLocalizable localizable )
	{
		localizable.localize( tmp );
		moveTo( tmp );
	}

	@Override
	public void setPosition( final IntegerLocalizable localizable )
	{
		localizable.localize( tmp );
		setPosition( tmp );
	}
}
