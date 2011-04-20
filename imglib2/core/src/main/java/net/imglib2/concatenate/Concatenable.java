package net.imglib2.concatenate;

public interface Concatenable< A >
{
	public Concatenable< A > concatenate( A a );

	public Class< A > getConcatenableClass();
}
