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

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.DoubleContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.DoubleTypeDisplay;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.TypeImpl;

public class DoubleType extends TypeImpl<DoubleType> implements NumericType<DoubleType>
{
	final DoubleContainer<DoubleType> doubleStorage;
	double[] v;
	
	// this is the constructor if you want it to read from an array
	public DoubleType( DoubleContainer<DoubleType> doubleStorage )
	{
		this.doubleStorage = doubleStorage;
	}
	
	// this is the constructor if you want it to be a variable
	public DoubleType( final double value )
	{
		doubleStorage = null;
		v = new double[ 1 ];
		v[ 0 ] = value;
		i = 0;
	}

	// this is the constructor if you want it to be a variable
	public DoubleType() { this( 0 ); }

	@Override
	public DoubleContainer<DoubleType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] )
	{
		return storageFactory.createDoubleInstance( dim, 1 );	
	}

	@Override
	public DoubleTypeDisplay getDefaultDisplay( Image<DoubleType> image )
	{
		return new DoubleTypeDisplay( image );
	}

	@Override
	public void updateDataArray( final Cursor<?> c ) 
	{ 
		v = doubleStorage.getCurrentStorageArray( c ); 
	}

	@Override
	public void mul( final float c ) { v[ i ] = v[ i ] * c; }

	@Override
	public void mul( final double c ) { v[ i ] = v[ i ] * c; }

	public double get() { return v[ i ]; }
	public void set( final double f ) { v[ i ] = f; }
	public float getReal() { return (float)v[ i ]; }
	public void setReal( final float f ) { v[ i ] = f; }

	@Override
	public void add( final DoubleType c ) { v[ i ] += c.get(); }

	@Override
	public void div( final DoubleType c ) { v[ i ] /= c.get(); }

	@Override
	public void mul( final DoubleType c ) { v[ i ] *= c.get(); }

	@Override
	public void sub( final DoubleType c ) { v[ i ] -= c.get(); }

	@Override
	public void set( final DoubleType c ) { v[ i ] = c.get(); }

	@Override
	public int compareTo( final DoubleType c ) 
	{ 
		if ( v[ i ] > c.get() )
			return 1;
		else if ( v[ i ] < c.get() )
			return -1;
		else 
			return 0;
	}

	@Override
	public void setOne() { v[ i ] = 1; }

	@Override
	public void setZero() { v[ i ] = 0; }

	@Override
	public void inc() { v[ i ]++; }

	@Override
	public void dec() { v[ i ]--; }
	
	@Override
	public DoubleType[] createArray1D(int size1){ return new DoubleType[ size1 ]; }

	@Override
	public DoubleType[][] createArray2D(int size1, int size2){ return new DoubleType[ size1 ][ size2 ]; }

	@Override
	public DoubleType[][][] createArray3D(int size1, int size2, int size3) { return new DoubleType[ size1 ][ size2 ][ size3 ]; }

	//@Override
	//public DoubleType getType() { return this; }
	
	@Override
	public DoubleType createType( Container<DoubleType> container )
	{
		return new DoubleType( (DoubleContainer<DoubleType>)container );
	}

	@Override
	public DoubleType createVariable(){ return new DoubleType( 0 ); }

	@Override
	public DoubleType clone(){ return new DoubleType( v[ i ] ); }

	@Override
	public String toString() { return "" + v[i]; }
}
