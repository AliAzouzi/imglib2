package mpicbg.imglib.type.numeric.real;

import mpicbg.imglib.container.NativeContainer;
import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.type.NativeType;

public class FloatTypeNative extends FloatType implements NativeType
{
	// the DirectAccessContainer
	final NativeContainer<FloatType, ? extends FloatAccess > storage;
	
	// the (sub)DirectAccessContainer that holds the information 
	FloatAccess b;
	
	// this is the constructor if you want it to read from an array
	public FloatTypeNative( NativeContainer<FloatType, ? extends FloatAccess > floatStorage )
	{
		storage = floatStorage;
	}

	final public float get(){ return b.getValue( i ); }
	final public void set( final float f ){ b.setValue( i, f ); }
	
	@Override
	public void updateContainer( final Object c ) 
	{ 
		b = storage.update( c ); 
	}	
}
