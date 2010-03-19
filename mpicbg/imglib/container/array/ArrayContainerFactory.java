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
package mpicbg.imglib.container.array;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.PixelGridContainerImpl;
import mpicbg.imglib.container.basictypecontainer.*;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.CharArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.FloatArray;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.container.basictypecontainer.array.LongArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.type.Type;

public class ArrayContainerFactory extends ContainerFactory
{
	@Override
	public <T extends Type<T>> Container<T, BitAccess> createBitInstance( int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, BitAccess>( this, new BitArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, BitAccess>( this, new BitArray( numPixels ), dimensions, entitiesPerPixel );
	}
	
	@Override
	public <T extends Type<T>> Container<T, ByteAccess> createByteInstance( final int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, ByteAccess>( this, new ByteArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, ByteAccess>( this, new ByteArray( numPixels ), dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>> Container<T, CharAccess> createCharInstance(int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, CharAccess>( this, new CharArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, CharAccess>( this, new CharArray( numPixels ), dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>> Container<T, DoubleAccess> createDoubleInstance(int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, DoubleAccess>( this, new DoubleArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, DoubleAccess>( this, new DoubleArray( numPixels ), dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>> Container<T, FloatAccess> createFloatInstance(int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, FloatAccess>( this, new FloatArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, FloatAccess>( this, new FloatArray( numPixels ), dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>> Container<T, IntAccess> createIntInstance(int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, IntAccess>( this, new IntArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, IntAccess>( this, new IntArray( numPixels ), dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>> Container<T, LongAccess> createLongInstance(int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, LongAccess>( this, new LongArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, LongAccess>( this, new LongArray( numPixels ), dimensions, entitiesPerPixel );
	}

	@Override
	public <T extends Type<T>> Container<T, ShortAccess> createShortInstance(int[] dimensions, final int entitiesPerPixel)
	{
		final int numPixels = PixelGridContainerImpl.getNumEntities(dimensions, entitiesPerPixel);
		
		if ( dimensions.length == 3 && useOptimizedContainers )
			return new Array3D<T, ShortAccess>( this, new ShortArray( numPixels ), dimensions[0], dimensions[1], dimensions[2], entitiesPerPixel );
		else
			return new Array<T, ShortAccess>( this, new ShortArray( numPixels ), dimensions, entitiesPerPixel );
	}

	@Override
	public String getErrorMessage()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printProperties()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setParameters(String configuration)
	{
		// TODO Auto-generated method stub
		
	}

}
