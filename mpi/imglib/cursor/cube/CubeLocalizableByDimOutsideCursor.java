/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpi.imglib.cursor.cube;

import mpi.imglib.container.cube.Cube;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.image.Image;
import mpi.imglib.outside.OutsideStrategy;
import mpi.imglib.outside.OutsideStrategyFactory;
import mpi.imglib.type.Type;

public class CubeLocalizableByDimOutsideCursor<T extends Type<T>> extends CubeLocalizableByDimCursor<T> implements LocalizableByDimCursor<T>
{
	final OutsideStrategyFactory<T> outsideStrategyFactory;
	final OutsideStrategy<T> outsideStrategy;
	
	boolean isOutside = false;
	
	public CubeLocalizableByDimOutsideCursor( final Cube<?,?,T> container, final Image<T> image, final T type, final OutsideStrategyFactory<T> outsideStrategyFactory ) 
	{
		super( container, image, type );
		
		this.outsideStrategyFactory = outsideStrategyFactory;
		this.outsideStrategy = outsideStrategyFactory.createStrategy( this );
		
		reset();
	}	

	@Override
	public boolean hasNext()
	{			
		if ( !isOutside && cube < numCubes - 1 )
			return true;
		else if ( type.getIndex() < cubeMaxI - 1 )
			return true;
		else
			return false;
	}	

	@Override
	public void reset()
	{
		if ( outsideStrategy == null )
			return;
		
		type.updateIndex( -1 );
		cube = 0;
		getCubeData( cube );
		isClosed = false;
		isOutside = false;
		
		position[ 0 ] = -1;
		
		for ( int d = 1; d < numDimensions; d++ )
		{
			position[ d ] = 0;
			cubePosition[ d ] = 0;
		}
		
		type.updateDataArray( this );
	}

	@Override
	public void fwd()
	{
		if ( !isOutside )
		{
			if ( type.getIndex() < cubeMaxI - 1 )
			{
				type.incIndex();
				
				for ( int d = 0; d < numDimensions; d++ )
				{
					if ( position[ d ] < cubeDimensions[ d ] + cubeOffset[ d ] - 1 )
					{
						position[ d ]++;
						
						for ( int e = 0; e < d; e++ )
							position[ e ] = cubeOffset[ e ];
						
						return;
					}
				}				
			}
			else if (cube < numCubes - 1)
			{
				cube++;
				type.updateIndex( 0 );			
				getCubeData(cube);
				for ( int d = 0; d < numDimensions; d++ )
					position[ d ] = cubeOffset[ d ];
			}
			else
			{
				// we moved outside the image
				isOutside = true;
				lastCube = -1;						
				cube = numCubes;
				position[0]++;
				outsideStrategy.initOutside( type );
			}
		}
	}
	
	@Override
	public void move( final int steps, final int dim )
	{
		if ( isOutside )
		{
			position[ dim ] += steps;	

			// reenter the image?
			if ( position[ dim ] >= 0 && position[ dim ] <  dimensions[ dim ] ) 
				setPosition( position );
			else // moved outside of the image
				outsideStrategy.notifyOutside( type );
			
		}
		else
		{
			position[ dim ] += steps;	
	
			if ( position[ dim ] < cubeEnd[ dim ] && position[ dim ] >= cubeOffset[ dim ] )
			{
				// still inside the cube
				type.incIndex( step[ dim ] * steps );
			}
			else
			{
				setPosition( position[ dim ], dim );
			}
		}
	}

	@Override
	public void fwd( final int dim )
	{
		if ( isOutside )
		{
			position[ dim ]++;

			// reenter the image?
			if ( position[ dim ] == 0 )
				setPosition( position );
			else // moved outside of the image
				outsideStrategy.notifyOutside( type );
		}
		else if ( position[ dim ] + 1 < cubeEnd[ dim ])
		{
			// still inside the cube
			type.incIndex( step[ dim ] );
			position[ dim ]++;	
		}
		else if ( cubePosition[ dim ] < numCubesDim[ dim ] - 2 )
		{
			// next cube in dim direction is not the last one
			cubePosition[ dim ]++;
			cube += cubeStep[ dim ];
			
			// we can directly compute the array index i in the next cube
			type.decIndex( ( position[ dim ] - cubeOffset[ dim ] ) * step[ dim ] );
			getCubeData(cube);
			
			position[ dim ]++;	
		} 
		else if ( cubePosition[ dim ] == numCubesDim[ dim ] - 2 ) 
		{
			// next cube in dim direction is the last one, we cannot propagte array index i					
			cubePosition[ dim ]++;
			cube += cubeStep[ dim ];

			getCubeData(cube);					
			position[ dim ]++;	
			type.updateIndex( cubeInstance.getPosGlobal( position ) );
		}
		else
		{
			// left the image
			isOutside = true;
			lastCube = -1;						
			cube = numCubes;
			position[0]++;
			outsideStrategy.initOutside( type );
		}
	}

	@Override
	public void bck( final int dim )
	{
		if ( isOutside )
		{
			position[ dim ]--;	

			// reenter the image?
			if ( position[ dim ] == dimensions[ dim ] - 1 )
				setPosition( position );
			else // moved outside of the image
				outsideStrategy.notifyOutside( type );
		}
		else if ( position[ dim ] - 1 >= cubeOffset[ dim ])
		{
			// still inside the cube
			type.decIndex( step[ dim ] );
			position[ dim ]--;	
		}
		else if ( cubePosition[ dim ] == numCubesDim[ dim ] - 1 && numCubes != 1)
		{
			// current cube is the last one, so we cannot propagate the i
			cubePosition[ dim ]--;
			cube -= cubeStep[ dim ];

			getCubeData(cube);					
			
			position[ dim ]--;
			type.updateIndex( cubeInstance.getPosGlobal( position ) );
		}
		else if ( cubePosition[ dim ] > 0 )
		{
			// current cube in dim direction is not the last one
			cubePosition[ dim ]--;
			cube -= cubeStep[ dim ];
			
			type.decIndex( ( position[ dim ] - cubeOffset[ dim ]) * step[ dim ] );
			getCubeData(cube);
			type.incIndex( ( cubeDimensions[ dim ] - 1 ) * step[ dim ] );
			
			position[ dim ]--;	
		}
		else
		{
			// left the image
			isOutside = true;
			lastCube = -1;						
			cube = numCubes;
			position[0]++;
			outsideStrategy.initOutside( type );			
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		// save current state
		final boolean wasOutside = isOutside;
		isOutside = false;

		// update positions and check if we are inside the image
		for ( int d = 0; d < numDimensions; d++ )
		{
			this.position[ d ] = position[ d ];
			
			if ( position[ d ] < 0 || position[ d ] >= dimensions[ d ])
			{
				// we are outside of the image
				isOutside = true;
			}
		}

		if ( isOutside )
		{
			// new location is outside the image
		
			if ( wasOutside ) // just moved outside of the image
				outsideStrategy.notifyOutside( type );
			else // we left the image with this setPosition() call
				outsideStrategy.initOutside( type );
		}
		else
		{
			// new location is inside the image
			if ( wasOutside ) // we reenter the image with this setPosition() call
				type.updateDataArray( this );			
						
			// the cube position in "cube space" from the image coordinates 
			container.getCubeElementPosition( position, cubePosition );
			
			// get the cube index
			cube = container.getCubeElementIndex( cursor, cubePosition );

			getCubeData(cube);
			type.updateIndex( cubeInstance.getPosGlobal( position ) );			
		}	
	}
	
	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;

		// we are outside the image or in the initial starting position
		if ( isOutside || type.getIndex() == -1 )
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
				isOutside = true;
				outsideStrategy.initOutside( type );
				return;
			}
			else
			{
				// jumped around inside the image
				
				// the cube position in "cube space" from the image coordinates 
				cubePosition[ dim ] = container.getCubeElementPosition( position, dim );

				// get the cube index
				cube = container.getCubeElementIndex( cursor, cubePosition[ dim ], dim );
				
				getCubeData(cube);
				type.updateIndex( cubeInstance.getPosGlobal( this.position ) );				
			}
		}
	}	
}
