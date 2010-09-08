/**
 * Copyright (c) 2009--2010, Cardona, Preibisch & Saalfeld
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
package mpicbg.imglib.sampler.shapelist;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import mpicbg.imglib.container.shapelist.ShapeListCached;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Cardona, Preibisch and Saalfeld
 */
public class ShapeListCacheFIFO<T extends Type< T > > extends ShapeListCache<T>
{
	final Map<Integer, T> cache;
	final LinkedList< Integer > queue;

	public ShapeListCacheFIFO( final int cacheSize, final ShapeListCached<T> container )
	{		
		super( cacheSize, container );
		
		cache = new HashMap<Integer, T>( cacheSize );
		queue = new LinkedList<Integer>( );
		
		for ( int i = 0; i < cacheSize; ++i )
			queue.add( Integer.MIN_VALUE );		
	}

	@Override
	public T lookUp( final int[] position )
	{
		final int index = fakeArray.getPos( position );

		final T value = cache.get( index );
		
		if ( value == null )
		{
			final T t = container.getShapeType( position );
			queue.add( index );
			cache.put( index , t );
			
			cache.remove( queue.removeFirst() );				
			
			return t;
		}
		else
		{
			return value;
		}		
	}

	@Override
	public ShapeListCache< T > createInstance() { return new ShapeListCacheFIFO< T >( cacheSize, container );	}
}
