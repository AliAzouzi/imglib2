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
 *
 */
package mpicbg.imglib.location.transform;

import mpicbg.imglib.location.Localizable;
import mpicbg.imglib.location.Positionable;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.location.RasterPositionable;

/**
 * A {@link Positionable} that drives a {@link RasterPositionable} to its
 * round discrete coordinates:
 * 
 * f = r < 0 ? (long)( r - 0.5 ) : (long)( r + 0.5 )
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RoundRasterPositionable< LocalizableRasterPositionable extends RasterLocalizable & RasterPositionable > implements Positionable, Localizable
{
	final protected LocalizableRasterPositionable target;
	
	final private int numDimensions;
	
	/* current position, required for relative movement */
	final private float[] position;
	
	public RoundRasterPositionable( final LocalizableRasterPositionable target )
	{
		this.target = target;
		
		numDimensions = target.numDimensions();
		
		position = new float[ numDimensions ];
	}
	
	public RoundRasterPositionable( final Localizable origin, final LocalizableRasterPositionable target )
	{
		this( target );
		
		origin.localize( position );
		for ( int d = 0; d < numDimensions; ++d )
			target.setPosition( round( position[ d ] ), d );
	}
	
	final static private int round( final double r )
	{
		return r < 0 ? ( int )( r - 0.5 ) : ( int )( r + 0.5 );
	}
	
	final static private int round( final float r )
	{
		return r < 0 ? ( int )( r - 0.5f ) : ( int )( r + 0.5f );
	}
	
	
	/* Dimensionality */
	
	@Override
	public int numDimensions(){ return target.numDimensions(); }

	
	/* Localizable */

	@Override
	public double getDoublePosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public float getFloatPosition( final int dim )
	{
		return position[ dim ];
	}

	@Override
	public String getLocationAsString()
	{
		final StringBuffer pos = new StringBuffer( "(" );
		pos.append( position[ 0 ] );

		for ( int d = 1; d < numDimensions; d++ )
			pos.append( ", " ).append( position[ d ] );

		pos.append( ")" );

		return pos.toString();
	}

	@Override
	public void localize( final float[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = this.position[ d ];
	}
	

	/* Positionable */
	
	@Override
	public void move( final float distance, final int dim )
	{
		final float realPosition = position[ dim ] + distance;
		final int floorPosition = round( realPosition );
		position[ dim ] = realPosition;
		final int floorDistance = floorPosition - target.getIntPosition( dim );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, dim );
	}

	@Override
	public void move( final double distance, final int dim )
	{
		final float realPosition = position[ dim ] + ( float )distance;
		final int floorPosition = round( realPosition );
		position[ dim ] = realPosition;
		final int floorDistance = floorPosition - target.getIntPosition( dim );
		if ( floorDistance == 0 )
			return;
		else
			target.move( floorDistance, dim );
	}

	@Override
	public void moveTo( final Localizable localizable )
	{
		localizable.localize( position );
		moveTo( position );
	}

	@Override
	public void moveTo( final float[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			final float realPosition = position[ d ];
			this.position[ d ] = realPosition;
			final int floorPosition = round( realPosition );
			final int floorDistance = floorPosition - target.getIntPosition( d );
			if ( floorDistance == 0 )
				return;
			else
				target.move( floorDistance, d );
		}
	}

	@Override
	public void moveTo( final double[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			final float realPosition = ( float )position[ d ];
			this.position[ d ] = realPosition;
			final int floorPosition = round( realPosition );
			final int floorDistance = floorPosition - target.getIntPosition( d );
			if ( floorDistance == 0 )
				return;
			else
				target.move( floorDistance, d );
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		localizable.localize( position );
		setPosition( position );
	}

	@Override
	public void setPosition( final float[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			final float realPosition = position[ d ];
			this.position[ d ] = realPosition;
			target.setPosition( round( realPosition ), d );
		}
	}

	@Override
	public void setPosition( final double[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			final float realPosition = ( float )position[ d ];
			this.position[ d ] = realPosition;
			target.setPosition( round( realPosition ), d );
		}
	}

	@Override
	public void setPosition( final float position, final int dim )
	{
		this.position[ dim ] = position;
		target.setPosition( round( position ), dim );
	}

	@Override
	public void setPosition( final double position, final int dim )
	{
		this.position[ dim ] = ( float )position;
		target.setPosition( round( position ), dim );
	}

	
	/* RasterPositionable */
	
	@Override
	public void bck( final int dim )
	{
		position[ dim ] -= 1;
		target.bck( dim );
	}

	@Override
	public void fwd( final int dim )
	{
		position[ dim ] += 1;
		target.fwd( dim );
	}

	@Override
	public void move( final int distance, final int dim )
	{
		position[ dim ] += distance;
		target.move( distance, dim );
	}

	@Override
	public void move( final long distance, final int dim )
	{
		position[ dim ] += distance;
		target.move( distance, dim );
	}

	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		localizable.localize( position );
		target.moveTo( localizable );
	}

	@Override
	public void moveTo( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];
		target.moveTo( position );
	}

	@Override
	public void moveTo( final long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];
		target.moveTo( position );
	}
	
	@Override
	public void setPosition( RasterLocalizable localizable )
	{
		localizable.localize( position );
		target.setPosition( localizable );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];
		target.setPosition( position );
	}
	
	@Override
	public void setPosition( long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];
		target.setPosition( position );
	}

	@Override
	public void setPosition( int position, int dim )
	{
		this.position[ dim ] = position;
		target.setPosition( position, dim );
	}

	@Override
	public void setPosition( long position, int dim )
	{
		this.position[ dim ] = position;
		target.setPosition( position, dim );
	}
}
