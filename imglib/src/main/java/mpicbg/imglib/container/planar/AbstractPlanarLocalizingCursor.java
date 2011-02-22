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
package mpicbg.imglib.container.planar;

import mpicbg.imglib.container.AbstractImgLocalizingCursorInt;
import mpicbg.imglib.type.NativeType;

/**
 * Localizing Iterator for a {@link PlanarContainer PlanarContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
abstract public class AbstractPlanarLocalizingCursor< T extends NativeType< T > > extends AbstractImgLocalizingCursorInt< T > implements PlanarContainer.PlanarContainerSampler
{
	protected final T type;

	protected final int lastIndex, lastSliceIndex;
	protected int sliceIndex;
	
	public AbstractPlanarLocalizingCursor( final PlanarContainer<T,?> container ) 
	{
		super( container );
		
		this.type = container.createLinkedType();

		lastIndex = ( ( n > 1 ) ? container.dimensions[ 1 ] : 1 )  *  container.dimensions[ 0 ] - 1;
		lastSliceIndex = container.numSlices() - 1;
		
		reset();
	}

	@Override
	public int getCurrentSliceIndex() { return sliceIndex; }

	@Override
	public T get() { return type; }
	
	/**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 * 
	 * @return false for the last element 
	 */
	@Override
	public boolean hasNext()
	{
		return ( type.getIndex() < lastIndex ) || ( sliceIndex < lastSliceIndex );
	}
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() == lastIndex )
		{
			++sliceIndex;
			type.updateIndex( 0 );
			type.updateContainer( this );
		}
		else
		{
			type.incIndex();
		}

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] >= size[ d ] ) position[ d ] = 0;
			else break;
		}
	}

	@Override
	public void reset()
	{
		position[ 0 ] = -1;
		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;
		
		sliceIndex = 0;
		
		type.updateIndex( -1 );		
		type.updateContainer( this );
	}
}
