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
package mpicbg.imglib.type.numeric.integer;

import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.img.basictypeaccess.LongAccess;
import mpicbg.imglib.img.basictypeaccess.array.LongArray;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.util.Util;

final public class LongType extends AbstractIntegerType<LongType> implements NativeType<LongType>
{
	private int i = 0;
	
	// the NativeContainer
	final NativeImg<LongType, ? extends LongAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	LongAccess b;
	
	// this is the constructor if you want it to read from an array
	public LongType( NativeImg<LongType, ? extends LongAccess> longStorage )
	{
		storage = longStorage;
	}

	// this is the constructor if you want it to be a variable
	public LongType( final long value )
	{
		storage = null;
		b = new LongArray ( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public LongType() { this( 0 ); }

	@Override
	public NativeImg<LongType, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<LongType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<LongType, ? extends LongAccess> container = storageFactory.createLongInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final LongType linkedType = new LongType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}

	@Override
	public void updateContainer( final Object c ) { b = storage.update( c ); }

	@Override
	public LongType duplicateTypeOnSameNativeContainer() { return new LongType( storage ); }
	
	public long get(){ return b.getValue( i ); }
	public void set( final long f ){ b.setValue( i, f ); }

	@Override
	public int getInteger(){ return (int)get(); }
	@Override
	public long getIntegerLong() { return get(); }
	@Override
	public void setInteger( final int f ){ set( f ); }
	@Override
	public void setInteger( final long f ){ set( f ); }
	
	@Override
	public double getMaxValue() { return Long.MAX_VALUE; }
	@Override
	public double getMinValue()  { return Long.MIN_VALUE; }
	
	@Override
	public void mul( final float c )
	{
		set( Util.round( get() * c ) );
	}

	@Override
	public void mul( final double c )
	{
		set( Util.round( get() * c ) );
	}
	
	@Override
	public void add( final LongType c )
	{
		set( get() + c.get() );
	}

	@Override
	public void div( final LongType c )
	{
		set( get() / c.get() );
	}

	@Override
	public void mul( final LongType c )
	{
		set( get() * c.get() );
	}

	@Override
	public void sub( final LongType c )
	{
		set( get() - c.get() );
	}

	@Override
	public int compareTo( final LongType c ) 
	{ 
		final long a = get();
		final long b = c.get();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public void set( final LongType c ){ set( c.get() ); }

	@Override
	public void setOne() { set( 1 ); }

	@Override
	public void setZero() { set( 0 ); }

	@Override
	public void inc()
	{
		long a = get();
		set( ++a );
	}

	@Override
	public void dec()
	{
		long a = get();
		set( --a );
	}

	@Override
	public LongType createVariable(){ return new LongType( 0 ); }

	@Override
	public LongType copy(){ return new LongType( get() ); }
	
	@Override
	public int getEntitiesPerPixel() { return 1; }

	@Override
	public void updateIndex( final int i ) { this.i = i; }
	@Override
	public int getIndex() { return i; }
	
	@Override
	public void incIndex() { ++i; }
	@Override
	public void incIndex( final int increment ) { i += increment; }
	@Override
	public void decIndex() { --i; }
	@Override
	public void decIndex( final int decrement ) { i -= decrement; }		
}
