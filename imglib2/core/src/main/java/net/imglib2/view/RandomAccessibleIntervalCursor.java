/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package net.imglib2.view;

import net.imglib2.AbstractInterval;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.util.IntervalIndexer;

/**
 * A {@link Cursor} that iterates any {@link RandomAccessibleInterval} by moving
 * a {@link RandomAccess} in flat iteration order. {@link Localizable} calls are
 * forwarded to the {@link RandomAccess}.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>, Stephan Saalfeld
 */
public final class RandomAccessibleIntervalCursor< T > extends AbstractInterval implements Cursor< T >
{
	private final RandomAccess< T > randomAccess;

	private final long[] dimensions;

	private final long[] tmp;

	private final long lastIndex;

	private long index;

	public < I extends RandomAccessible< T > & Interval > RandomAccessibleIntervalCursor( final I interval )
	{
		super( interval );
		randomAccess = interval.randomAccess();
		dimensions = new long[ n ];
		dimensions( dimensions );
		tmp = new long[ n ];
		long size = 1;
		for ( int d = 0; d < n; ++d )
			size *= dimensions[ d ];
		lastIndex = size - 1;
		reset();
	}

	protected RandomAccessibleIntervalCursor( final RandomAccessibleIntervalCursor< T > cursor )
	{
		super( cursor );
		this.randomAccess = cursor.randomAccess.copyRandomAccess();
		dimensions = cursor.dimensions.clone();
		tmp = new long[ n ];
		lastIndex = cursor.lastIndex;
		index = cursor.index;
	}

	@Override
	public T get()
	{
		return randomAccess.get();
	}

	@Override
	public void jumpFwd( final long steps )
	{
		index += steps;
		IntervalIndexer.indexToPosition( steps, dimensions, tmp );
		randomAccess.move( tmp );
	}

	@Override
	public void fwd()
	{
		++index;
		for ( int d = 0; d < n; ++d )
		{
			randomAccess.fwd( d );
			if ( randomAccess.getLongPosition( d ) > max[ d ] )
				randomAccess.setPosition( min[ d ], d );
			else
				break;
		}
	}

	@Override
	public void reset()
	{
		index = -1;
		randomAccess.setPosition( min );
		randomAccess.bck( 0 );
	}

	@Override
	public boolean hasNext()
	{
		return index < lastIndex;
	}

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{}

	@Override
	public RandomAccessibleIntervalCursor< T > copy()
	{
		return new RandomAccessibleIntervalCursor< T >( this );
	}

	@Override
	public RandomAccessibleIntervalCursor< T > copyCursor()
	{
		return copy();
	}

	@Override
	public void localize( final float[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return randomAccess.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return randomAccess.getDoublePosition( d );
	}

	@Override
	public void localize( final int[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public void localize( final long[] position )
	{
		randomAccess.localize( position );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return randomAccess.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return randomAccess.getLongPosition( d );
	}
}
