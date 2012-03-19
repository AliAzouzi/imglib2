package net.imglib2.img.cell;

import net.imglib2.AbstractLocalizable;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.type.NativeType;

/**
 * This {@link ImgRandomAccess} assumes that successive accesses fall
 * within different cells more often than not.
 * No checks are performed to determine whether we stay in the same cell.
 * Instead, the cell position is computed and set on every access.
 */
public class CellRandomAccess< T extends NativeType< T >, A extends ArrayDataAccess< A >, C extends AbstractCell< A > > extends AbstractLocalizable implements RandomAccess< T >, CellImg.CellContainerSampler< T, A, C >
{
	protected final CellImg< T, A, C > img;

	protected final T type;

	protected final RandomAccess< C > randomAccessOnCells;

	protected final long[] tmp;

	protected int[] currentCellSteps;
	protected long[] currentCellMin;
	protected long[] currentCellMax;

	protected boolean isOutOfBounds;
	protected final long[] oobCellMin;
	protected final long[] oobCellMax;

	/**
	 * The current index of the type.
	 * It is faster to duplicate this here than to access it through type.getIndex().
	 */
	protected int index;

	protected CellRandomAccess( final CellRandomAccess< T, A, C > randomAccess )
	{
		super( randomAccess.numDimensions() );

		img = randomAccess.img;
		type = randomAccess.type.duplicateTypeOnSameNativeImg();
		randomAccessOnCells = randomAccess.randomAccessOnCells.copyRandomAccess();
		tmp = new long[ n ];

		for ( int d = 0; d < n; ++d )
			position[ d ] = randomAccess.position[ d ];

		currentCellSteps = randomAccess.currentCellSteps;
		currentCellMin = randomAccess.currentCellMin;
		currentCellMax = randomAccess.currentCellMax;

		isOutOfBounds = randomAccess.isOutOfBounds;
		oobCellMin = randomAccess.oobCellMin;
		oobCellMax = randomAccess.oobCellMax;

		index = randomAccess.index;
		type.updateContainer( this );
		type.updateIndex( index );
	}

	public CellRandomAccess( final CellImg< T, A, C > img )
	{
		super( img.numDimensions() );

		this.img = img;
		type = img.createLinkedType();
		randomAccessOnCells = img.cells.randomAccess();

		tmp = new long[ n ];

		isOutOfBounds = false;
		oobCellMin = new long[ n ];
		oobCellMax = new long[ n ];
		for ( int d = 0; d < n; ++d )
		{
			oobCellMin[ d ] = Long.MAX_VALUE;
			oobCellMax[ d ] = Long.MIN_VALUE;
		}

		img.getCellPosition( position, tmp );
		randomAccessOnCells.setPosition( tmp );
		updatePosition( false );
	}

	@Override
	public C getCell()
	{
		return randomAccessOnCells.get();
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public CellRandomAccess< T, A, C > copy()
	{
		return new CellRandomAccess< T, A, C >( this );
	}

	@Override
	public CellRandomAccess< T, A, C > copyRandomAccess()
	{
		return copy();
	}

	@Override
	public void fwd( final int d )
	{
		index += currentCellSteps[ d ];
		if ( ++position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.fwd( d );
			updatePosition( position[ d ] >= img.dimension( d ) );
		}
		type.updateIndex( index );
	}

	@Override
	public void bck( final int d )
	{
		index -= currentCellSteps[ d ];
		if ( --position[ d ] < currentCellMin[ d ] )
		{
			randomAccessOnCells.bck( d );
			updatePosition( position[ d ] < 0 );
		}
		type.updateIndex( index );
	}

	@Override
	public void move( final int distance, final int d )
	{
		index += distance * currentCellSteps[ d ];
		position[ d ] += distance;
		if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= img.dimension( d ) );
		}
		type.updateIndex( index );
	}

	@Override
	public void move( final long distance, final int d )
	{
		index += ( int ) distance * currentCellSteps[ d ];
		position[ d ] += distance;
		if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= img.dimension( d ) );
		}
		type.updateIndex( index );
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long pos = localizable.getLongPosition( d );
			if ( pos != 0 )
			{
				index += ( int ) pos * currentCellSteps[ d ];
				position[ d ] += pos;
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= img.dimension( d );

					for ( ++d; d < n; ++d )
					{
						final long pos2 = localizable.getLongPosition( d );
						if ( pos2 != 0 )
						{
							position[ d ] += pos2;
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= img.dimension( d );
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		type.updateIndex( index );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( distance[ d ] != 0 )
			{
				index += distance[ d ] * currentCellSteps[ d ];
				position[ d ] += distance[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= img.dimension( d );

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= img.dimension( d );
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		type.updateIndex( index );
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( distance[ d ] != 0 )
			{
				index += ( int ) distance[ d ] * currentCellSteps[ d ];
				position[ d ] += distance[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= img.dimension( d );

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= img.dimension( d );
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final int pos, final int d )
	{
		index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
		position[ d ] = pos;
		if ( pos < currentCellMin[ d ] || pos > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( pos / img.cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= img.dimension( d ) );
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
		position[ d ] = pos;
		if ( pos < currentCellMin[ d ] || pos > currentCellMax[ d ] )
		{
			randomAccessOnCells.setPosition( pos / img.cellDims[ d ], d );
			updatePosition( position[ d ] < 0 || position[ d ] >= img.dimension( d ) );
		}
		type.updateIndex( index );
	}


	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			final long pos = localizable.getLongPosition( d );
			if ( pos != position[ d ] )
			{
				index += ( int ) ( pos - position[ d ] ) * currentCellSteps[ d ];
				position[ d ] = pos;
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= img.dimension( d );

					for ( ++d; d < n; ++d )
					{
						final long posInner = localizable.getLongPosition( d );
						if ( posInner != position[ d ] )
						{
							position[ d ] = posInner;
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= img.dimension( d );
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				index += ( int ) ( pos[ d ] - position[ d ] ) * currentCellSteps[ d ];
				position[ d ] = pos[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= img.dimension( d );

					for ( ++d; d < n; ++d )
					{
						if ( pos[ d ] != position[ d ] )
						{
							position[ d ] = pos[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= img.dimension( d );
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		type.updateIndex( index );
	}

	@Override
	public void setPosition( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				index += ( int ) ( pos[ d ] - position[ d ] ) * currentCellSteps[ d ];
				position[ d ] = pos[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
					boolean movedOutOfBounds = position[ d ] < 0 || position[ d ] >= img.dimension( d );

					for ( ++d; d < n; ++d )
					{
						if ( pos[ d ] != position[ d ] )
						{
							position[ d ] = pos[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								randomAccessOnCells.setPosition( position[ d ] / img.cellDims[ d ], d );
								movedOutOfBounds |= position[ d ] < 0 || position[ d ] >= img.dimension( d );
							}
						}
					}

					updatePosition( movedOutOfBounds );
				}
			}
		}
		type.updateIndex( index );
	}

	/**
	 * Update type to currentCellSteps, currentCellMin, and type after
	 * switching cells. This is called after randomAccessOnCells and position
	 * fields have been set.
	 *
	 * @param updateD the (first) dimension that triggered the call by moving out of current cell range.
	 */
	private void updatePosition( final boolean movedOutOfBounds )
	{
		// are we out of the image?
		if ( movedOutOfBounds )
		{
			isOutOfBounds = true;
			currentCellMin = oobCellMin;
			currentCellMax = oobCellMax;
		}
		else
		{
			if ( isOutOfBounds )
			{
				// did we come back into the image?
				isOutOfBounds = false;
				for ( int d = 0; d < n; ++d )
					if ( position[ d ] < 0 || position[ d ] >= img.dimension( d ) )
					{
						isOutOfBounds = true;
						break;
					}

				if ( ! isOutOfBounds )
				{
					// yes. we came back into the image.
					// re-initialize randomAccessOnCells to the correct position.
					img.getCellPosition( position, tmp );
					randomAccessOnCells.setPosition( tmp );
				}
			}

			final C cell = getCell();

			currentCellSteps = cell.steps;
			currentCellMin = cell.min;
			currentCellMax = cell.max;

			for ( int d = 0; d < n; ++d )
				tmp[ d ] = position[ d ] - currentCellMin[ d ];
			index = cell.localPositionToIndex( tmp );

			type.updateContainer( this );
		}
	}
}
