/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.outofbounds;

import net.imglib2.AbstractEuclideanSpace;
import net.imglib2.Bounded;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;

/**
 *
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public final class OutOfBoundsRandomAccess< T > extends AbstractEuclideanSpace implements RandomAccess< T >, Bounded
{
	/**
	 * performs the actual moves and generates/queries a Type
	 */
	final protected OutOfBounds< T > outOfBounds;

	private OutOfBoundsRandomAccess( final OutOfBoundsRandomAccess< T > outOfBoundsRandomAccess )
	{
		super( outOfBoundsRandomAccess.n );
		this.outOfBounds = outOfBoundsRandomAccess.outOfBounds.copy();
	}

	/**
	 * @param n number of dimensions in the {@link RandomAccessible}.
	 * @param outOfBounds
	 */
	public OutOfBoundsRandomAccess( final int n, final OutOfBounds< T > outOfBounds )
	{
		super( n );
		this.outOfBounds = outOfBounds;
	}


	/* Bounded */

	@Override
	final public boolean isOutOfBounds()
	{
		return outOfBounds.isOutOfBounds();
	}


	/* Sampler */

	@Override
	final public T get(){ return outOfBounds.get(); }


	/* Localizable */

	@Override
	final public void localize( final int[] position ){ outOfBounds.localize( position ); }

	@Override
	final public void localize( final long[] position ){ outOfBounds.localize( position ); }

	@Override
	final public int getIntPosition( final int dim ){ return outOfBounds.getIntPosition( dim ); }

	@Override
	final public long getLongPosition( final int dim ){ return outOfBounds.getLongPosition( dim ); }


	/* RealLocalizable */

	@Override
	final public void localize( final float[] position ){ outOfBounds.localize( position ); }

	@Override
	final public void localize( final double[] position ){ outOfBounds.localize( position ); }

	@Override
	final public double getDoublePosition( final int dim ){ return outOfBounds.getDoublePosition( dim ); }

	@Override
	final public float  getFloatPosition( final int dim ){ return outOfBounds.getFloatPosition( dim ); }


	/* Positionable */

	@Override
	final public void fwd( final int dim )
	{
		outOfBounds.fwd( dim );
	}

	@Override
	final public void bck( final int dim )
	{
		outOfBounds.bck( dim );
	}

	@Override
	final public void move( final int distance, final int dim )
	{
		outOfBounds.move( distance, dim );
	}

	@Override
	final public void move( final long distance, final int dim )
	{
		outOfBounds.move( distance, dim );
	}

	@Override
	final public void move( final Localizable localizable )
	{
		outOfBounds.move( localizable );
	}

	@Override
	final public void move( final int[] distance )
	{
		outOfBounds.move( distance );
	}

	@Override
	final public void move( final long[] distance )
	{
		outOfBounds.move( distance );
	}

	@Override
	final public void setPosition( final int distance, final int dim )
	{
		outOfBounds.setPosition( distance, dim );
	}

	@Override
	final public void setPosition( final long distance, final int dim )
	{
		outOfBounds.setPosition( distance, dim );
	}

	@Override
	final public void setPosition( final Localizable localizable )
	{
		outOfBounds.setPosition( localizable );
	}

	@Override
	final public void setPosition( final int[] position )
	{
		outOfBounds.setPosition( position );
	}

	@Override
	final public void setPosition( final long[] position )
	{
		outOfBounds.setPosition( position );
	}

	@Override
	public String toString() { return outOfBounds.toString() + " = " + get(); }

	@Override
	public OutOfBoundsRandomAccess< T > copy()
	{
		return new OutOfBoundsRandomAccess< T >( this );
	}

	@Override
	public OutOfBoundsRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}
}
