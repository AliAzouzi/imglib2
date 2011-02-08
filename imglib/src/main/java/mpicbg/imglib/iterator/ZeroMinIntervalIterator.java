/**
 * Copyright (c) 2009--2011, Stephan Preibisch & Stephan Saalfeld
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
package mpicbg.imglib.iterator;

import mpicbg.imglib.Interval;
import mpicbg.imglib.Sampler;
import mpicbg.imglib.location.FlatIterator;
import mpicbg.imglib.util.IntervalIndexer;
import mpicbg.imglib.util.Util;

/**
 * Use this class to iterate a virtual rectangular {@link Interval} whose min
 * coordinates are at 0<sup><em>n</em></sup> in flat
 * order, that is: row by row, plane by plane, cube by cube, ...  This is useful for
 * iterating an arbitrary interval in a defined order.  For that,
 * connect a {@link ZeroMinIntervalIterator} to a {@link Positionable}.
 * 
 * <pre>
 * ...
 * ZeroMinIntervalIterator i = new ZeroMinIntervalIterator(image);
 * RandomAccess<T> s = image.randomAccess();
 * while (i.hasNext()) {
 *   i.fwd();
 *   s.setPosition(i);
 *   s.type().performOperation(...);
 *   ...
 * }
 * ...
 * </pre>
 * 
 * Note that {@link ZeroMinIntervalIterator} is the right choice in situations where
 * <em>not</em> for each pixel you want to localize and/or set the
 * {@link Positionable} [{@link Sampler}], that is in a sparse sampling situation.
 * For localizing at each iteration step (as in the simplified example above),
 * use {@link FlatIterator} instead.
 *  
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class ZeroMinIntervalIterator extends IntervalIterator
{
	public ZeroMinIntervalIterator( final long[] dimensions )
	{
		super( dimensions );
	}

	public ZeroMinIntervalIterator( final Interval interval )
	{
		super( interval );
	}
	

	/* Iterator */

	@Override
	final public void jumpFwd( final long i ) { index += i; }

	@Override
	final public void fwd() { ++index; }

	@Override
	final public void reset() { index = -1; }
	
	
	/* IntegerLocalizable */

	@Override
	final public long getLongPosition( final int dim )
	{
		return IntervalIndexer.indexToPosition( index, dimensions, steps, dim );
	}
	
	@Override
	final public void localize( final long[] position ) { IntervalIndexer.indexToPosition( index, dimensions, position ); }

	@Override
	final public int getIntPosition( final int dim ) { return ( int )IntervalIndexer.indexToPosition( index, dimensions, steps, dim ); }

	@Override
	final public void localize( final int[] position ) { IntervalIndexer.indexToPosition( index, dimensions, position ); }

	@Override
	final public double getDoublePosition( final int dim ) { return IntervalIndexer.indexToPosition( index, dimensions, steps, dim ); }
	
	
	/* RealLocalizable */

	@Override
	final public float getFloatPosition( final int dim ) { return IntervalIndexer.indexToPosition( index, dimensions, steps, dim ); }

	@Override
	final public void localize( final float[] position ) { IntervalIndexer.indexToPosition( index, dimensions, position ); }

	@Override
	final public void localize( final double[] position ) { IntervalIndexer.indexToPosition( index, dimensions, position ); }

	
	/* EuclideanSpace */
	
	@Override
	final public int numDimensions() { return n; }
	
	
	/* Object */
	
	@Override
	final public String toString()
	{
		final int[] l = new int[ dimensions.length ];
		localize( l );
		return Util.printCoordinates( l );
	}
}
