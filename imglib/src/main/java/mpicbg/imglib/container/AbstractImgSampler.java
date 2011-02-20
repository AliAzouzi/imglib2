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
 */
package mpicbg.imglib.container;

import mpicbg.imglib.type.Type;

/**
 * 
 * @author Stephan Preibisch and Stephan Saalfeld
 *
 * @param < T > the {@link Type} to be returned by {@link #get()}
 */
public abstract class AbstractImgSampler< T > implements ImgSampler< T >
{
	final protected int n;
	
	public AbstractImgSampler( final int n )
	{
		this.n = n;
	}

	@Override
	@Deprecated
	final public T getType() { return get(); }
	
	@Override
	public int numDimensions(){ return n; }

	@Override
	public long max( final int d )
	{
		return getImg().max( d );
	}

	@Override
	public void max( long[] max )
	{
		getImg().max( max );		
	}

	@Override
	public long min( int d )
	{
		return getImg().min( d );
	}

	@Override
	public void min( long[] min )
	{
		getImg().min( min );
	}

	@Override
	public void dimensions( long[] size )
	{
		getImg().dimensions( size );
	}

	@Override
	public long dimension( int d )
	{
		return getImg().dimension( d );
	}

	@Override
	public double realMax( int d )
	{
		return getImg().realMax( d );
	}

	@Override
	public void realMax( double[] max )
	{
		getImg().realMax( max );
	}

	@Override
	public double realMin( int d )
	{
		return getImg().realMin( d );
	}

	@Override
	public void realMin( double[] min )
	{
		getImg().realMin( min );	
	}
}
