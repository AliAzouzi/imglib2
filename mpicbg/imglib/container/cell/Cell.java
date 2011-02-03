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
package mpicbg.imglib.container.cell;

import mpicbg.imglib.container.AbstractImg;
import mpicbg.imglib.container.AbstractPixelGridContainer;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 * @param <A>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class Cell< T extends Type< T >, A extends ArrayDataAccess< A > > // extends Array< T, A >
{
	final protected int[] offset, step, dim;

	final protected int cellId, numDimensions, numPixels, numEntities;

	// the ArrayDataAccess containing the data
	final protected A data;

	public Cell( final A creator, final int cellId, final int[] dim, final int offset[], final int entitiesPerPixel )
	{
		this.offset = offset;
		this.cellId = cellId;
		this.numDimensions = dim.length;
		this.dim = dim;
		this.numPixels = ( int ) AbstractImg.numElements( dim );
		this.numEntities = ( int ) AbstractPixelGridContainer.getNumEntities( dim, entitiesPerPixel );

		step = new int[ numDimensions ];

		this.data = creator.createArray( numEntities );

		// the steps when moving inside a cell
		Array.createAllocationSteps( dim, step );
	}

	protected A getData()
	{
		return data;
	}

	protected void close()
	{
		data.close();
	}

	public int getNumPixels()
	{
		return numPixels;
	}

	public int getNumEntities()
	{
		return numEntities;
	}

	public void dimensions( final int[] dim )
	{
		for ( int d = 0; d < numDimensions; ++d )
			dim[ d ] = this.dim[ d ];
	}

	public void getSteps( final int[] step )
	{
		for ( int d = 0; d < numDimensions; d++ )
			step[ d ] = this.step[ d ];
	}

	public int getCellId()
	{
		return cellId;
	}

	public long getLongOffset( final int dim )
	{
		return offset[ dim ];
	}

	/**
	 * Read the {@link Cell} offset coordinates into an int[]
	 * 
	 * @param offset
	 */
	public void offset( final int[] offset )
	{
		for ( int i = 0; i < numDimensions; ++i )
			offset[ i ] = this.offset[ i ];
	}

	/**
	 * Read the {@link Cell} offset coordinates into a long[]
	 * 
	 * @param offset
	 */
	public void offset( final long[] offset )
	{
		for ( int i = 0; i < numDimensions; ++i )
			offset[ i ] = this.offset[ i ];
	}

	/**
	 * Calculate the {@link Cell} index for a global position. Note that this
	 * method does not check if the global position is actually contained in the
	 * {@link Cell}.
	 * 
	 * @param position
	 * @return
	 */
	public final int globalPositionToIndex( final int[] position )
	{
		int i = position[ 0 ] - offset[ 0 ];
		for ( int d = 1; d < dim.length; ++d )
			i += ( position[ d ] - offset[ d ] ) * step[ d ];

		return i;
	}

	final public void indexToGlobalPosition( int i, final long[] position )
	{
		for ( int d = numDimensions - 1; d >= 0; --d )
		{
			final int ld = i / step[ d ];
			i -= ld * step[ d ];
			// i %= step[ d ];

			position[ d ] = ld + offset[ d ];
		}
	}

	final public long indexToGlobalPosition( int i, final int dim )
	{
		for ( int d = numDimensions - 1; d > dim; --d )
			i %= step[ d ];

		return i / step[ dim ] + offset[ dim ];
	}
}
