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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.cursor.array;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.FakeAccess;
import mpicbg.imglib.container.basictypecontainer.array.FakeArray;
import mpicbg.imglib.cursor.PositionableCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursor;
import mpicbg.imglib.cursor.special.LocalNeighborhoodCursorFactory;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.location.RasterPositionable;
import mpicbg.imglib.location.VoidRasterPositionable;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;

public class ArrayPositionableCursor<T extends Type<T>> extends ArrayLocalizableCursor<T> implements PositionableCursor<T>
{
	//final CursorLink link;
	final protected int[] step;
	final int tmp[];
	
	int numNeighborhoodCursors = 0;
	
	protected RasterPositionable linkedRasterPositionable = VoidRasterPositionable.getInstance();
	
	public ArrayPositionableCursor( final Array<T,?> container, final Image<T> image, final T type ) 
	{
		super( container, image, type );
		
		step = Array.createAllocationSteps( container.getDimensions() );
		tmp = new int[ numDimensions ];
		//link = new NullLink();
	}	
	
	public static ArrayPositionableCursor<FakeType> createLinearByDimCursor( final int[] dim )
	{
		final Array<FakeType, FakeAccess> array = new Array<FakeType, FakeAccess>( null, new FakeArray(), dim, 1 );
		return new ArrayPositionableCursor<FakeType>( array, null, new FakeType() );
	}
	
	@Override
	public synchronized LocalNeighborhoodCursor<T> createLocalNeighborhoodCursor()
	{
		if ( numNeighborhoodCursors == 0)
		{
			++numNeighborhoodCursors;
			return LocalNeighborhoodCursorFactory.createLocalNeighborhoodCursor( this );
		}
		else
		{
			System.out.println("ArrayLocalizableByDimCursor.createLocalNeighborhoodCursor(): There is only one one special cursor per cursor allowed.");
			return null;
		}
	}

	@Override
	public synchronized RegionOfInterestCursor<T> createRegionOfInterestCursor( final int[] offset, final int[] size )
	{
		if ( numNeighborhoodCursors == 0)
		{
			++numNeighborhoodCursors;
			return new RegionOfInterestCursor<T>( this, offset, size );
		}
		else
		{
			System.out.println("ArrayLocalizableByDimCursor.createRegionOfInterestCursor(): There is only one special cursor per cursor allowed.");
			return null;
		}
	}
	
	@Override
	public void fwd( final int dim )
	{
		type.incIndex( step[ dim ] );
		++position[ dim ];
		
		linkedRasterPositionable.fwd( dim );
	}

	@Override
	public void move( final int steps, final int dim )
	{
		type.incIndex( step[ dim ] * steps );
		position[ dim ] += steps;	
		//link.move(steps, dim);
	}
	
	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int )distance, dim );		
	}
	
	@Override
	public void bck( final int dim )
	{
		type.decIndex( step[ dim ] );
		--position[ dim ];
		//link.bck(dim);
	}
		
	@Override
	public void moveTo( final int[] position )
	{		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final int dist = position[ d ] - getIntPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}
	
	@Override
	public void moveTo( final long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
		{
			final long dist = position[ d ] - getLongPosition( d );
			
			if ( dist != 0 )				
				move( dist, d );
		}
	}

	@Override
	public void moveTo( final RasterLocalizable localizable )
	{
		localizable.localize( tmp );
		moveTo( tmp );
	}

	@Override
	public void setPosition( final RasterLocalizable localizable )
	{
		localizable.localize( tmp );
		setPosition( tmp );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		type.updateIndex( container.getPos( position ) );
		
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = position[ d ];
		
		//link.setPosition( position );
	}
	
	@Override
	public void setPosition( long[] position )
	{
		for ( int d = 0; d < numDimensions; ++d )
			this.position[ d ] = ( int )position[ d ];
		
		type.updateIndex( container.getPos( this.position ) );
		
		//link.setPosition( position );
		
	}

	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;
		type.updateIndex( container.getPos( this.position ) );
		//link.setPosition( position, dim );
	}
	
	@Override
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
		
	}

	@Override
	public void linkRasterPositionable( final RasterPositionable rasterPositionable )
	{
		linkedRasterPositionable = rasterPositionable;
	}

	@Override
	public RasterPositionable unlinkRasterPositionable()
	{
		final RasterPositionable rasterPositionable = linkedRasterPositionable;
		linkedRasterPositionable = VoidRasterPositionable.getInstance();
		return rasterPositionable;
	}
}
