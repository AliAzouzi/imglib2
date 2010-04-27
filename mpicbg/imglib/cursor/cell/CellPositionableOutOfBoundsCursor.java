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
package mpicbg.imglib.cursor.cell;

import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategy;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class CellPositionableOutOfBoundsCursor<T extends Type<T>> extends CellPositionableCursor<T>
{
	final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory;
	final OutOfBoundsStrategy<T> outOfBoundsStrategy;
	
	boolean isOutOfBounds = false;
	
	public CellPositionableOutOfBoundsCursor( final CellContainer<T,?> container, final Image<T> image, final T type, final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory ) 
	{
		super( container, image, type );
		
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.outOfBoundsStrategy = outOfBoundsStrategyFactory.createStrategy( this );
		
		reset();
	}	

	@Override
	public boolean hasNext()
	{			
		if ( !isOutOfBounds && cell < numCells - 1 )
			return true;
		else if ( type.getIndex() < cellMaxI - 1 )
			return true;
		else
			return false;
	}	

	@Override
	public T type() 
	{ 
		if ( isOutOfBounds )
			return outOfBoundsStrategy.getType();
		else
			return type; 
	}
	
	@Override
	public void reset()
	{
		if ( outOfBoundsStrategy != null )
		{
			type.updateIndex( -1 );
			cell = 0;
			getCellData( cell );
			isOutOfBounds = false;
			
			position[ 0 ] = -1;
			
			for ( int d = 1; d < numDimensions; d++ )
			{
				position[ d ] = 0;
				cellPosition[ d ] = 0;
			}
			
			type.updateContainer( this );
		}
		
		linkedIterator.reset();
	}

	@Override
	public void fwd()
	{
		if ( !isOutOfBounds )
		{
			if ( type.getIndex() < cellMaxI - 1 )
			{
				type.incIndex();
				
				for ( int d = 0; d < numDimensions; d++ )
				{
					if ( position[ d ] < cellDimensions[ d ] + cellOffset[ d ] - 1 )
					{
						position[ d ]++;
						
						for ( int e = 0; e < d; e++ )
							position[ e ] = cellOffset[ e ];
						
						break;
					}
				}				
			}
			else if (cell < numCells - 1)
			{
				cell++;
				type.updateIndex( 0 );			
				getCellData(cell);
				for ( int d = 0; d < numDimensions; d++ )
					position[ d ] = cellOffset[ d ];
			}
			else
			{
				// we moved out of image bounds
				isOutOfBounds = true;
				lastCell = -1;						
				cell = numCells;
				position[0]++;
				outOfBoundsStrategy.initOutOfBOunds(  );
			}
		}
		
		linkedIterator.fwd();
	}
	
	@Override
	public void move( final int steps, final int dim )
	{
		if ( isOutOfBounds )
		{
			position[ dim ] += steps;	

			// reenter the image?
			if ( position[ dim ] >= 0 && position[ dim ] <  dimensions[ dim ] ) 
			{
				isOutOfBounds = false;
				
				for ( int d = 0; d < numDimensions && !isOutOfBounds; d++ )
					if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
						isOutOfBounds = true;
				
				if ( !isOutOfBounds )
				{
					type.updateContainer( this );			
					
					// the cell position in "cell space" from the image coordinates 
					container.getCellPosition( position, cellPosition );
					
					// get the cell index
					cell = container.getCellIndex( cursor, cellPosition );

					getCellData(cell);
					type.updateIndex( cellInstance.getPosGlobal( position ) );
				}
				else
				{
					outOfBoundsStrategy.notifyOutOfBOunds( steps, dim );
				}
			}
			else // moved out of image bounds
			{
				outOfBoundsStrategy.notifyOutOfBOunds( steps, dim );
			}
			
			linkedRasterPositionable.move( steps, dim );
		}
		else
		{
			position[ dim ] += steps;	
	
			if ( position[ dim ] < cellEnd[ dim ] && position[ dim ] >= cellOffset[ dim ] )
			{
				// still inside the cell
				type.incIndex( step[ dim ] * steps );
				
				linkedRasterPositionable.move( steps, dim );
			}
			else
				setPosition( position[ dim ], dim );
		}
	}

	@Override
	public void fwd( final int dim )
	{
		if ( isOutOfBounds )
		{
			position[ dim ]++;

			// reenter the image?
			if ( position[ dim ] == 0 )
				setPosition( position );
			else // moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOundsFwd( dim );
		}
		else if ( position[ dim ] + 1 < cellEnd[ dim ])
		{
			// still inside the cell
			type.incIndex( step[ dim ] );
			position[ dim ]++;	
		}
		else if ( cellPosition[ dim ] < numCellsDim[ dim ] - 2 )
		{
			// next cell in dim direction is not the last one
			cellPosition[ dim ]++;
			cell += cellStep[ dim ];
			
			// we can directly compute the array index i in the next cell
			type.decIndex( ( position[ dim ] - cellOffset[ dim ] ) * step[ dim ] );
			getCellData(cell);
			
			position[ dim ]++;	
		} 
		else if ( cellPosition[ dim ] == numCellsDim[ dim ] - 2 ) 
		{
			// next cell in dim direction is the last one, we cannot propagte array index i					
			cellPosition[ dim ]++;
			cell += cellStep[ dim ];

			getCellData(cell);					
			position[ dim ]++;	
			type.updateIndex( cellInstance.getPosGlobal( position ) );
		}
		else
		{
			// left the image
			isOutOfBounds = true;
			lastCell = -1;						
			cell = numCells;
			position[0]++;
			outOfBoundsStrategy.initOutOfBOunds(  );
		}
		
		linkedRasterPositionable.fwd( dim );
	}

	@Override
	public void bck( final int dim )
	{
		if ( isOutOfBounds )
		{
			position[ dim ]--;	

			// reenter the image?
			if ( position[ dim ] == dimensions[ dim ] - 1 )
				setPosition( position );
			else // moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBoundsBck( dim );
		}
		else if ( position[ dim ] - 1 >= cellOffset[ dim ])
		{
			// still inside the cell
			type.decIndex( step[ dim ] );
			position[ dim ]--;	
		}
		else if ( cellPosition[ dim ] == numCellsDim[ dim ] - 1 && numCells != 1)
		{
			// current cell is the last one, so we cannot propagate the i
			cellPosition[ dim ]--;
			cell -= cellStep[ dim ];

			getCellData(cell);					
			
			position[ dim ]--;
			type.updateIndex( cellInstance.getPosGlobal( position ) );
		}
		else if ( cellPosition[ dim ] > 0 )
		{
			// current cell in dim direction is not the last one
			cellPosition[ dim ]--;
			cell -= cellStep[ dim ];
			
			type.decIndex( ( position[ dim ] - cellOffset[ dim ]) * step[ dim ] );
			getCellData(cell);
			type.incIndex( ( cellDimensions[ dim ] - 1 ) * step[ dim ] );
			
			position[ dim ]--;	
		}
		else
		{
			// left the image
			isOutOfBounds = true;
			lastCell = -1;						
			cell = numCells;
			position[0]++;
			outOfBoundsStrategy.initOutOfBOunds(  );			
		}

		linkedRasterPositionable.bck( dim );
	}

	@Override
	public void setPosition( final int[] position )
	{
		// save current state
		final boolean wasOutOfBounds = isOutOfBounds;
		isOutOfBounds = false;

		// update positions and check if we are inside the image
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[ d ];
			
			if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
			{
				// we are out of image bounds
				isOutOfBounds = true;
			}
		}

		if ( isOutOfBounds )
		{
			// new location is out of image bounds
		
			if ( wasOutOfBounds ) // just moved out of image bounds
				outOfBoundsStrategy.notifyOutOfBOunds(  );
			else // we left the image with this setPosition() call
				outOfBoundsStrategy.initOutOfBOunds(  );
		}
		else
		{
			// new location is inside the image
			if ( wasOutOfBounds ) // we reenter the image with this setPosition() call
				type.updateContainer( this );			
						
			// the cell position in "cell space" from the image coordinates 
			container.getCellPosition( position, cellPosition );
			
			// get the cell index
			cell = container.getCellIndex( cursor, cellPosition );

			getCellData(cell);
			type.updateIndex( cellInstance.getPosGlobal( position ) );			
		}

		linkedRasterPositionable.setPosition( position );
	}
	
	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// we are out of image bounds or in the initial starting position
		if ( isOutOfBounds || type.getIndex() == -1 )
		{
			// if just this dimensions moves inside does not necessarily mean that
			// the other ones do as well, so we have to do a full check here
			setPosition( this.position );
		}
		else
		{
			// we can just check in this dimension if it is still inside

			if ( position < 0 || position >= dimensions[ dim ])
			{
				// cursor has left the image
				isOutOfBounds = true;
				outOfBoundsStrategy.initOutOfBOunds(  );
			}
			else
			{
				// jumped around inside the image
				
				// the cell position in "cell space" from the image coordinates 
				cellPosition[ dim ] = container.getCellPosition( position, dim );

				// get the cell index
				cell = container.getCellIndex( cursor, cellPosition[ dim ], dim );
				
				getCellData(cell);
				type.updateIndex( cellInstance.getPosGlobal( this.position ) );				
			}

			linkedRasterPositionable.setPosition( position, dim );
		}
	}	
}
