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
package mpicbg.imglib.type.numeric;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.IntContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.RGBALegacyTypeDisplay;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public class RGBALegacyType extends TypeImpl<RGBALegacyType> implements NumericType<RGBALegacyType>
{
	final IntContainer<RGBALegacyType> intStorage;
	int[] v;
	
	// this is the constructor if you want it to read from an array
	public RGBALegacyType( IntContainer<RGBALegacyType> intStorage )
	{
		this.intStorage = intStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public RGBALegacyType( final int value )
	{
		intStorage = null;
		v = new int[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public RGBALegacyType() { this( 0 ); }
	
	@Override
	public IntContainer<RGBALegacyType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createIntInstance( dim, 1 );	
	}

	@Override
	public RGBALegacyTypeDisplay getDefaultDisplay( Image<RGBALegacyType> image )
	{
		return new RGBALegacyTypeDisplay( image );
	}

	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		v = intStorage.getCurrentStorageArray( c ); 
	}

	final public static int rgba( final int r, final int g, final int b, final int a)
	{
		return (r & 0xff) | ((g & 0xff) << 8) | ((b & 0xff) << 16) | ((a & 0xff) << 24);
	}
	
	final public static int rgba( final float r, final float g, final float b, final float a)
	{
		return rgba( MathLib.round(r), MathLib.round(g), MathLib.round(b), MathLib.round(a) );
	}

	final public static int rgba( final double r, final double g, final double b, final double a)
	{
		return rgba( (int)MathLib.round(r), (int)MathLib.round(g), (int)MathLib.round(b), (int)MathLib.round(a) );
	}
	
	final public static int red( final int value )
	{
		return value & 0xff;
	}
	
	final public static int green( final int value )
	{
		return (value >> 8) & 0xff;
	}
	
	final public static int blue( final int value )
	{
		return (value >> 16) & 0xff;
	}
	
	final public static int alpha( final int value )
	{
		return (value >> 24) & 0xff;
	}
	
	@Override
	public void mul( final float c )
	{
		final int value = v[ i ];		
		v[ i ] = rgba( red(value) * c, green(value) * c, blue(value) * c, alpha(value) * c );
	}

	@Override
	public void mul( final double c ) 
	{ 
		final int value = v[ i ];		
		v[ i ] = rgba( red(value) * c, green(value) * c, blue(value) * c, alpha(value) * c );
	}

	public int get() { return v[ i ]; }
	public void set( final int f ) { v[ i ] = f; }
	public float getReal() 
	{
		final int value = v[ i ];
		return ( red( value ) + green( value ) + blue( value ) ) / 3; 
	}
	public void setReal( final float f ) 
	{
		final int value = MathLib.round( f );
		v[ i ] = rgba( value, value, value, 0 );
	}

	@Override
	public void add( final RGBALegacyType c ) 
	{ 
		final int value1 = v[ i ];		
		final int value2 = c.get();		
		
		v[ i ] = rgba( red(value1) + red(value2), green(value1) + green(value2), blue(value1) + blue(value2), alpha(value1) + alpha(value2) );		 
	}

	@Override
	public void div( final RGBALegacyType c ) 
	{ 
		final int value1 = v[ i ];		
		final int value2 = c.get();		
		
		v[ i ] = rgba( red(value1) / red(value2), green(value1) / green(value2), blue(value1) / blue(value2), alpha(value1) / alpha(value2) );		 
	}

	@Override
	public void mul( final RGBALegacyType c ) 
	{
		final int value1 = v[ i ];		
		final int value2 = c.get();		
		
		v[ i ] = rgba( red(value1) * red(value2), green(value1) * green(value2), blue(value1) * blue(value2), alpha(value1) * alpha(value2) );		 
	}

	@Override
	public void sub( final RGBALegacyType c ) 
	{
		final int value1 = v[ i ];		
		final int value2 = c.get();		
		
		v[ i ] = rgba( red(value1) - red(value2), green(value1) - green(value2), blue(value1) - blue(value2), alpha(value1) - alpha(value2) );		 
	}
	
	@Override
	public int compareTo( final RGBALegacyType c ) 
	{ 
		final int value1 = v[ i ];		
		final int value2 = c.get();

		if ( red(value1) + green(value1) + blue(value1) + alpha(value1) > red(value2) + green(value2) + blue(value2) + alpha(value2) )
			return 1;
		else if ( red(value1) + green(value1) + blue(value1) + alpha(value1) < red(value2) + green(value2) + blue(value2) + alpha(value2) )
			return -1;
		else 
			return 0;
	}

	@Override
	public void set( final RGBALegacyType c ) { v[ i ] = c.get(); }

	@Override
	public void setOne() { v[ i ] = rgba( 1, 1, 1, 1 ); }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ] += rgba( 1, 1, 1, 1 ); }

	@Override
	public void dec() { v[ i ] -= rgba( 1, 1, 1, 1 ); }
	
	@Override
	public RGBALegacyType[] createArray1D(int size1){ return new RGBALegacyType[ size1 ]; }

	@Override
	public RGBALegacyType[][] createArray2D(int size1, int size2){ return new RGBALegacyType[ size1 ][ size2 ]; }

	@Override
	public RGBALegacyType[][][] createArray3D(int size1, int size2, int size3) { return new RGBALegacyType[ size1 ][ size2 ][ size3 ]; }

	//@Override
	//public RGBALegacyType getType() { return this; }
	
	@Override
	public RGBALegacyType createType( Container<RGBALegacyType> container )
	{
		return new RGBALegacyType( (IntContainer<RGBALegacyType>)container );
	}

	@Override
	public RGBALegacyType createVariable() { return new RGBALegacyType( 0 ); }

	@Override
	public RGBALegacyType copyVariable() { return new RGBALegacyType( v[ i ] ); }

	@Override
	public String toString() 
	{
		return "(r=" + red( v[ i ] ) + ",g=" + green( v[ i ] ) + ",b=" + blue( v[ i ] ) + ",a=" + alpha( v[ i ] ) + ")";
	}
}
