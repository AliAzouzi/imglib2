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

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.basictypecontainer.IntAccess;

public class UnsignedIntType extends GenericIntType<UnsignedIntType>
{
	// this is the constructor if you want it to read from an array
	public UnsignedIntType( DirectAccessContainer<UnsignedIntType, IntAccess> intStorage ) { super( intStorage ); }

	// this is the constructor if you want it to be a variable
	public UnsignedIntType( final long value ) { super( getCodedSignedIntChecked(value) ); }

	// this is the constructor if you want it to be a variable
	public UnsignedIntType() { this( 0 ); }
	
	public static int getCodedSignedIntChecked( long unsignedInt )
	{
		if ( unsignedInt < 0 )
			unsignedInt = 0;
		else if ( unsignedInt > 4294967295l )
			unsignedInt = 4294967295l;
		
		return getCodedSignedInt( unsignedInt );
	}
	public static int getCodedSignedInt( final long unsignedInt ) { return (int)( unsignedInt & 0xffffffff ); }
	public static long getUnsignedInt( final int signedInt ) { return signedInt & 0xffffffff; }

	@Override
	public void mul( final float c )
	{
		final long a = getUnsignedInt( getValue() );
		setValue( getCodedSignedInt( MathLib.round( a * c ) ) );
	}

	@Override
	public void mul( final double c )
	{
		final long a = getUnsignedInt( getValue() );
		setValue( getCodedSignedInt( ( int )MathLib.round( a * c ) ) );
	}

	public long get(){ return getUnsignedInt( getValue() ); }
	public void set( final long f ){ setValue( getCodedSignedInt( f ) ); }

	@Override
	public int getInteger(){ return (int)get(); }
	@Override
	public long getIntegerLong() { return get(); }
	@Override
	public void setInteger( final int f ){ set( f ); }
	@Override
	public void setInteger( final long f ){ set( f ); }

	@Override
	public void div( final UnsignedIntType c )
	{
		set( get() / c.get() );
	}

	@Override
	public int compareTo( final UnsignedIntType c ) 
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
	public UnsignedIntType[] createArray1D( final int size1 ){ return new UnsignedIntType[ size1 ]; }

	@Override
	public UnsignedIntType[][] createArray2D( final int size1, final int size2 ){ return new UnsignedIntType[ size1 ][ size2 ]; }

	@Override
	public UnsignedIntType[][][] createArray3D( final int size1, final int size2, final int size3 ) { return new UnsignedIntType[ size1 ][ size2 ][ size3 ]; }

	@Override
	public UnsignedIntType createDirectAccessType( final DirectAccessContainer<UnsignedIntType, ?> DirectAccessContainer ) 
	{ 
		return new UnsignedIntType( (DirectAccessContainer<UnsignedIntType, IntAccess>)DirectAccessContainer ); 
	}
	
	@Override
	public UnsignedIntType createVariable(){ return new UnsignedIntType( 0 ); }

	@Override
	public UnsignedIntType clone(){ return new UnsignedIntType( get() ); }
}
