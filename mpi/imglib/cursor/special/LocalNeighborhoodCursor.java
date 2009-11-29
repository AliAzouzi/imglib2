package mpi.imglib.cursor.special;

import mpi.imglib.container.array.FakeArray;
import mpi.imglib.cursor.CursorImpl;
import mpi.imglib.cursor.LocalizableByDimCursor;
import mpi.imglib.cursor.LocalizableCursor;
import mpi.imglib.cursor.array.ArrayLocalizableByDimCursor;
import mpi.imglib.type.Type;
import mpi.imglib.type.label.FakeType;

public class LocalNeighborhoodCursor<T extends Type<T>> extends CursorImpl<T> 
{
	/**
	 * Here we "misuse" a ArrayLocalizableCursor to iterate through the cubes,
	 * he always gives us the location of the current cube we are instantiating 
	 */
	final LocalizableCursor<FakeType> neigborhoodCursor;

	final LocalizableByDimCursor<T> cursor;
	final int[] position, tmp;
	final int numDimensions, centralPositionIndex;
	boolean isActive;
	
	public LocalNeighborhoodCursor( final LocalizableByDimCursor<T> cursor )
	{
		super( cursor.getStorageContainer(), cursor.getImage(), cursor.getType() );
		
		this.cursor = cursor;
		this.position = cursor.getPosition();
		
		this.numDimensions = cursor.getImage().getNumDimensions();
		this.tmp = new int[ numDimensions ];
				
		int[] dim = new int[ numDimensions ];
		for ( int d = 0; d < numDimensions; ++d )
			dim[ d ] = 3;

		this.neigborhoodCursor = new ArrayLocalizableByDimCursor<FakeType>( new FakeArray<FakeType>( dim ), null, new FakeType() );
		this.isActive = true;

		for ( int d = 0; d < numDimensions; ++d )
			dim[ d ] = 1;

		this.centralPositionIndex = ((FakeArray<FakeType>)neigborhoodCursor.getStorageContainer()).getPos( dim );
	}
	
	@Override
	public boolean hasNext() { return neigborhoodCursor.hasNext(); }
	
	@Override
	public void close() 
	{
		neigborhoodCursor.close();
		isActive = false;
	}

	public void update()
	{
		cursor.getPosition( position );
		this.neigborhoodCursor.reset();		
	}
	
	public void reset()
	{
		cursor.setPosition( position );
		this.neigborhoodCursor.reset();		
	}

	@Override
	public void fwd()
	{
		neigborhoodCursor.fwd();
		
		if ( neigborhoodCursor.getType().getIndex() == centralPositionIndex )
			neigborhoodCursor.fwd();
		
		neigborhoodCursor.getPosition( tmp );

		for ( int d = 0; d < numDimensions; ++d )
			tmp[ d ] = position[ d ] + ( tmp[d] - 1 );
		
		cursor.moveTo( tmp );		
	}
	
	@Override
	public int getArrayIndex() { return cursor.getArrayIndex(); }

	@Override
	public int getStorageIndex() { return cursor.getStorageIndex();	}

	@Override
	public boolean isActive() { return cursor.isActive() && isActive; }

	@Override
	public void setDebug( boolean debug ) { cursor.setDebug( debug ); }
}
