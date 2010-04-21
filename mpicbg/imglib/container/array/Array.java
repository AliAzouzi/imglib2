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
package mpicbg.imglib.container.array;

import mpicbg.imglib.container.AbstractDirectAccessContainer;
import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.array.ArrayCursor;
import mpicbg.imglib.cursor.array.ArrayPositionableCursor;
import mpicbg.imglib.cursor.array.ArrayPositionableOutOfBoundsCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizableCursor;
import mpicbg.imglib.cursor.array.ArrayLocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class Array<T extends Type<T>, A extends DataAccess> extends AbstractDirectAccessContainer<T, A>
{
	final protected int[] step;
	final ArrayContainerFactory factory;
	
	// the DataAccess created by the ArrayContainerFactory
	final A data;

	public Array( final ArrayContainerFactory factory, final A data, final int[] dim, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );
		
		step = Array.createAllocationSteps( dim );
		this.factory = factory;
		this.data = data;
	}
	
	@Override
	public A update( final Cursor<?> c ) { return data; }

	@Override
	public ArrayContainerFactory getFactory() { return factory; }
	
	@Override
	public ArrayCursor<T> createCursor( final Image<T> image ) 
	{
		// create a Cursor using a Type that is linked to the container
		ArrayCursor<T> c = new ArrayCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
		return c;
	}

	@Override
	public ArrayLocalizableCursor<T> createLocalizableCursor( final Image<T> image ) 
	{ 
		// create a Cursor using a Type that is linked to the container
		ArrayLocalizableCursor<T> c = new ArrayLocalizableCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
		return c;
	}

	@Override
	public ArrayLocalizablePlaneCursor<T> createLocalizablePlaneCursor( final Image<T> image ) 
	{ 
		// create a Cursor using a Type that is linked to the container
		ArrayLocalizablePlaneCursor<T> c = new ArrayLocalizablePlaneCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
		return c;
	}
	
	@Override
	public ArrayPositionableCursor<T> createPositionableCursor( final Image<T> image ) 
	{ 
		// create a Cursor using a Type that is linked to the container
		ArrayPositionableCursor<T> c = new ArrayPositionableCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
		return c;
	}
	
	@Override
	public ArrayPositionableOutOfBoundsCursor<T> createPositionableCursor( final Image<T> image, final OutOfBoundsStrategyFactory<T> outOfBoundsFactory ) 
	{ 
		// create a Cursor using a Type that is linked to the container
		ArrayPositionableOutOfBoundsCursor<T> c = new ArrayPositionableOutOfBoundsCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer(), outOfBoundsFactory );
		return c;
	}
	
	public static int[] createAllocationSteps( final int[] dim )
	{
		int[] steps = new int[ dim.length ];
		createAllocationSteps( dim, steps );
		return steps;		
	}

	public static void createAllocationSteps( final int[] dim, final int[] steps )
	{
		steps[ 0 ] = 1;
		for ( int d = 1; d < dim.length; ++d )
			  steps[ d ] = steps[ d - 1 ] * dim[ d - 1 ];
	}
	
	public final int getPos( final int[] l )
	{ 
		int i = l[ 0 ];
		for ( int d = 1; d < numDimensions; ++d )
			i += l[ d ] * step[ d ];
		
		return i;
	}

	@Override
	public void close() { data.close();	}	
}
