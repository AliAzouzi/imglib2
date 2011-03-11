/**
 * Copyright (c) 2011, Stephan Saalfeld
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

package mpicbg.imglib;

import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsRandomAccess;
import mpicbg.imglib.util.Util;
import mpicbg.imglib.view.IntervalView;
import mpicbg.imglib.view.View;
import mpicbg.imglib.util.Pair;
import mpicbg.imglib.view.ViewTransform;

/**
 * Implements {@link RandomAccessible} for a {@link RandomAccessibleInterval}
 * through an {@link OutOfBoundsFactory}.
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
final public class ExtendedRandomAccessibleInterval< T, F extends RandomAccessibleInterval< T > > implements IntervalView< T > 
{
	final protected F target;
	final protected OutOfBoundsFactory< T, F > factory;

	public ExtendedRandomAccessibleInterval( final F interval, final OutOfBoundsFactory< T, F > factory )
	{
		this.target = interval;
		this.factory = factory;
	}
	
	@Override
	final public long dimension( final int d )
	{
		return target.dimension( d );
	}

	@Override
	final public void dimensions( final long[] dimensions )
	{
		target.dimensions( dimensions );
	}

	@Override
	final public long max( final int d )
	{
		return target.max( d );
	}

	@Override
	final public void max( final long[] max )
	{
		target.max( max );
	}

	@Override
	final public long min( final int d )
	{
		return target.min( d );
	}

	@Override
	final public void min( final long[] min )
	{
		target.min( min );
	}

	@Override
	final public double realMax( final int d )
	{
		return target.realMax( d );
	}

	@Override
	final public void realMax( final double[] max )
	{
		realMax( max );
	}

	@Override
	final public double realMin( final int d )
	{
		return target.realMin( d );
	}

	@Override
	final public void realMin( final double[] min )
	{
		realMin( min );
	}

	@Override
	final public int numDimensions()
	{
		return target.numDimensions();
	}

	@Override
	final public RandomAccess< T > randomAccess()
	{
		return new OutOfBoundsRandomAccess< T >( target.numDimensions(), factory.create( target ) );
	}

	@Override
	final public RandomAccess< T > randomAccess( Interval interval )
	{
		assert target.numDimensions() == interval.numDimensions();
		
		if ( Util.contains( target, interval ) ) {
			return target.randomAccess( interval );
		} else {
			return randomAccess();
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Pair< RandomAccess< T >, ViewTransform > untransformedRandomAccess( Interval interval )
	{
		System.out.println( "ExtendedRandomAccessibleInterval.untransformedRandomAccess in " + toString() );
		if ( Util.contains( target, interval ) )
		{
			if ( View.class.isInstance( target ) )
			{
				return ( ( View< T > ) target ).untransformedRandomAccess( interval );
			}
			else
			{
				return new Pair< RandomAccess< T >, ViewTransform >( target.randomAccess( interval ), null );
			}
		}
		else
		{
			return new Pair< RandomAccess< T >, ViewTransform >( randomAccess(), null );
		}
	}

	@Override
	public RandomAccessible< T > getTargetRandomAccessible()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
