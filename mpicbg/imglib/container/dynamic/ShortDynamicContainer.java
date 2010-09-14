/**
 * Copyright (c) 2009--2010, Stephan Preibisch and Stephan Saalfeld
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
package mpicbg.imglib.container.dynamic;

import java.util.ArrayList;

import mpicbg.imglib.sampler.dynamic.DynamicStorageAccess;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ShortDynamicContainer< T extends Type< T >> extends DynamicContainer< T, ShortDynamicContainerAccessor >
{
	final ArrayList< Short > data;

	public ShortDynamicContainer( final DynamicContainerFactory factory, final int[] dim, final int entitiesPerPixel )
	{
		super( factory, dim, entitiesPerPixel );

		data = new ArrayList< Short >();

		for ( int i = 0; i < numPixels * entitiesPerPixel; ++i )
			data.add( ( short ) 0 );
	}

	@Override
	public ShortDynamicContainerAccessor update( final Object access )
	{
		final DynamicStorageAccess c = ( DynamicStorageAccess )access;
		final ShortDynamicContainerAccessor accessor = ( ShortDynamicContainerAccessor )c.getAccessor();
		accessor.updateIndex( c.getInternalIndex() );

		return accessor;
	}

	@Override
	public ShortDynamicContainerAccessor createAccessor()
	{
		return new ShortDynamicContainerAccessor( this, entitiesPerPixel );
	}

	@Override
	public void close()
	{
		data.clear();
	}

}
