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
package mpicbg.imglib.type.label;

import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.img.basictypeaccess.CharAccess;
import mpicbg.imglib.img.basictypeaccess.array.CharArray;
import mpicbg.imglib.type.AbstractNativeType;
import mpicbg.imglib.type.BasePairType;
import mpicbg.imglib.type.label.BasePairBitType.Base;

public class BasePairCharType extends AbstractNativeType<BasePairCharType> implements BasePairType<BasePairCharType>
{
	@Override
	public int getEntitiesPerPixel() { return 1; } 

	// the NativeContainer
	final NativeImg<BasePairCharType, ? extends CharAccess> storage;
	
	// the (sub)NativeContainer that holds the information 
	CharAccess b;
	
	// this is the constructor if you want it to read from an array
	public BasePairCharType( NativeImg<BasePairCharType, ? extends CharAccess> charStorage )
	{
		storage = charStorage;
	}

	// this is the constructor if you want it to be a variable
	public BasePairCharType( final Base value )
	{
		storage = null;
		b = new CharArray( 1 );
		set( value );
	}

	// this is the constructor if you want it to be a variable
	public BasePairCharType( final char value )
	{	
		storage = null;
		b = new CharArray( 1 );
		setChar( value );
	}

	// this is the constructor if you want it to be a variable
	public BasePairCharType() { this( Base.N ); }

	@Override
	public NativeImg<BasePairCharType, ? extends CharAccess> createSuitableNativeImg( final NativeImgFactory<BasePairCharType> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<BasePairCharType, ? extends CharAccess> container = storageFactory.createCharInstance( dim, 1 );
		
		// create a Type that is linked to the container
		final BasePairCharType linkedType = new BasePairCharType( container );
		
		// pass it to the NativeContainer
		container.setLinkedType( linkedType );
		
		return container;
	}
	
	@Override
	public void updateContainer( final Object c ) { b = storage.update( c ); }
	
	@Override
	public BasePairCharType duplicateTypeOnSameNativeContainer() { return new BasePairCharType( storage ); }
	
	public char getChar() { return b.getValue( i ); }
	public void setChar( final char f ) { b.setValue( i, f ); }

	public void set( final Base base ) 
	{
		switch ( base )
		{
			case A: setChar('A'); return;
			case T: setChar('T'); return;
			case G: setChar('G'); return;
			case C: setChar('C'); return;
			case gap: setChar(' '); return;
			default: setChar('N'); return;
		}
	}
	
	public Base get() 
	{
		final char value = getChar();
		
		switch ( value )
		{
			case 'A': return Base.A;
			case 'T': return Base.T;
			case 'G': return Base.G;
			case 'C': return Base.C;
			case ' ': return Base.gap;
			default: return Base.N;
		}
	}
	
	@Override
	public void set( final BasePairCharType c ) { b.setValue( i, c.getChar() ); }

	@Override
	public int compareTo( final BasePairCharType c ) 
	{ 
		final char input = getChar();
		final char compare = c.getChar();
		
		if ( input == compare )
		{
			return 0;
		}
		else
		{
			switch ( input )
			{
				case ' ': return -1; 
				case 'N': if ( compare == ' ' ) return 1; else return -1;
				case 'A': if ( compare == ' ' || compare == 'N' ) return 1; else return -1;
				case 'T': if ( compare == 'G' || compare == 'C' ) return -1; else return 1;
				case 'G': if ( compare == 'C' ) return -1; else return 1;
				default: return 1;
			}
		}
	}

	@Override
	public void complement() 
	{
		final char base = getChar();
		switch ( base )
		{
			case 'A': setChar( 'T' ); break;
			case 'T': setChar( 'A' ); break;
			case 'G': setChar( 'C' ); break;
			case 'C': setChar( 'G' ); break;
		}
	}

	@Override
	public byte baseToValue()
	{
		final char base = getChar();
		
		switch ( base )
		{
			case 'N': return 1;
			case 'A': return 2;
			case 'T': return 3;
			case 'G': return 4;
			case 'C': return 5;
			default: return 0;
		}		
	}

	@Override
	public BasePairCharType createVariable(){ return new BasePairCharType( Base.N ); }
	
	@Override
	public BasePairCharType copy(){ return new BasePairCharType( get() ); }
	
	@Override
	public String toString() { return "" + get(); }
}
