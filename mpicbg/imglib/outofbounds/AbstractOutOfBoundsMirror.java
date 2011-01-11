/**
 * Copyright (c) 2009--2010, Stephan Saalfeld
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
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public abstract class AbstractOutOfBoundsMirror< T extends Type< T > > implements RasterOutOfBounds< T >
{
	final protected PositionableRasterSampler< T > outOfBoundsPositionable;
	
	final protected int numDimensions;
	
	final protected int[] dimension, position;
	
	/* true when increasing, false when decreasing */
	final protected boolean[] inc;
	
	final protected int[] p;
	
	final protected boolean[] dimIsOutOfBounds;
	
	protected boolean isOutOfBounds = false;
	
	AbstractOutOfBoundsMirror( final PositionableRasterSampler< T > source )
	{
		this( source, source.getImage().createPositionableRasterSampler() );
	}
	
	AbstractOutOfBoundsMirror(
			final PositionableRasterSampler< T > source,
			final PositionableRasterSampler< T > outOfBoundsPositionable )
	{
		this.outOfBoundsPositionable = outOfBoundsPositionable;
		numDimensions = source.getImage().numDimensions();
		dimension = source.getImage().getDimensions();
		position = new int[ numDimensions ];
		inc = new boolean[ numDimensions ];
		for ( int i = 0; i < dimension.length; ++i )
			inc[ i ] = true;
		
		p = new int[ numDimensions ];
		dimIsOutOfBounds = new boolean[ numDimensions ];
	}
	
	final protected void checkOutOfBounds()
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			if ( dimIsOutOfBounds[ d ] )
			{
				isOutOfBounds = true;
				return;
			}
		}
		isOutOfBounds = false;
	}
	
	
	/* Dimensionality */
	
	@Override
	public int numDimensions(){ return numDimensions; }
	
	
	/* OutOfBounds */
	
	@Override
	public boolean isOutOfBounds(){ return isOutOfBounds; }

	
	/* Sampler */
	
	@Override
	public T type(){ return outOfBoundsPositionable.type(); }
	
	@Override
	@Deprecated
	final public T getType(){ return type(); }
	
	
	/* RasterLocalizable */
	
	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final int[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}
	
	@Override
	public void localize( final long[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			pos[ d ] = this.position[ d ];
	}
	
	@Override
	public float getFloatPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public double getDoublePosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public int getIntPosition( final int dim ){ return position[ dim ]; }

	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public String toString() { return MathLib.printCoordinates( position ) + " = " + type(); }
	
	
	/* RasterPositionable */
	
	@Override
	public void move( final int distance, final int dim )
	{
		if ( distance > 0 )
		{
			for ( int i = 0; i < distance; ++i )
				fwd( dim );
		}
		else
		{
			for ( int i = -distance; i > 0; --i )
				bck( dim );
		}
	}
	
	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int )distance, dim );
	}
	
	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( localizable.getIntPosition( d ) - position[ d ], d );
	}
	
	@Override
	public void moveTo( final int[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( pos[ d ] - this.position[ d ], d );
	}
	
	@Override
	public void moveTo( final long[] pos )
	{
		for ( int d = 0; d < numDimensions; ++d )
			move( pos[ d ] - this.position[ d ], d );
	}
	
	@Override
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
	}
	
	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		for ( int d = 0; d < numDimensions; ++d )
			setPosition( localizable.getIntPosition( d ), d );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}
	
	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}
}
