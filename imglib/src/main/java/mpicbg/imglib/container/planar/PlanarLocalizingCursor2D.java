package mpicbg.imglib.container.planar;

import mpicbg.imglib.type.NativeType;

public class PlanarLocalizingCursor2D< T extends NativeType< T > > extends PlanarLocalizingCursor1D< T > 
{
	public PlanarLocalizingCursor2D( final PlanarContainer<T, ?> container )
	{
		super( container );
	}

	@Override
	public boolean hasNext()
	{
		return type.getIndex() < lastIndex;
	}
	
	@Override
	public void fwd()
	{
		type.incIndex();

		if ( ++position[ 0 ] >= dimension[ 0 ] )
		{
			position[ 0 ] = 0;
			++position[ 1 ];
		}
	}
}
