package net.imglib2.type.numeric;

import net.imglib2.type.Type;

public interface NumericType<T extends NumericType<T>> extends Type<T>
{
	public void add( T c );
	public void sub( T c );
	public void mul( T c );
	public void div( T c );

	public void setZero();
	public void setOne();	
	
	public void mul( float c );
	public void mul( double c );	
}
