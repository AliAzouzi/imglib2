package net.imglib2.type.numeric.integer;

import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.real.AbstractRealType;
import net.imglib2.util.Util;

public abstract class AbstractIntegerType<T extends AbstractIntegerType<T>> extends AbstractRealType<T> implements IntegerType<T>
{
	@Override
	public double getMinIncrement() { return 1; }

	@Override
	public float getRealFloat() { return getInteger(); }
	@Override
	public double getRealDouble() { return getIntegerLong(); }
	
	@Override
	public void setReal( final float real ){ setInteger( Util.round( real ) ); }
	@Override
	public void setReal( final double real ){ setInteger( Util.round( real ) ); }	

	@Override
	public void inc() { setInteger( getIntegerLong() + 1 ); }
	@Override
	public void dec() { setInteger( getIntegerLong() - 1 ); }

	@Override
	public void setZero() { setInteger( 0 ); }
	@Override
	public void setOne() { setInteger( 1 ); }	

	@Override
	public int compareTo( final T c ) 
	{ 
		final long a = getIntegerLong();
		final long b = c.getIntegerLong();
		if ( a > b )
			return 1;
		else if ( a < b )
			return -1;
		else 
			return 0;
	}
	
	@Override
	public String toString() { return "" + getIntegerLong(); }	
}
