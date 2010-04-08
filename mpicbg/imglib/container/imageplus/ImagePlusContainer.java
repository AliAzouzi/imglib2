/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Johannes Schindelin
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
 * @author Johannes Schindelin & Stephan Preibisch
 */
package mpicbg.imglib.container.imageplus;

import java.util.ArrayList;

import ij.ImagePlus;

import mpicbg.imglib.container.Container3D;
import mpicbg.imglib.container.DirectAccessContainerImpl;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableByDimCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableByDimOutOfBoundsCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizableCursor;
import mpicbg.imglib.cursor.imageplus.ImagePlusLocalizablePlaneCursor;
import mpicbg.imglib.exception.ImgLibException;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class ImagePlusContainer<T extends Type<T>, A extends ArrayDataAccess<A>> extends DirectAccessContainerImpl<T,A> implements Container3D<T>
{
	final ImagePlusContainerFactory factory;
	final int width, height, depth;

	final ArrayList<A> mirror;

	ImagePlusContainer( final ImagePlusContainerFactory factory, final int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );
		
		this.factory = factory;
		this.width = dim[ 0 ];
		
		if( dim.length < 2 )
			this.height = 1;
		else
			this.height = dim[ 1 ];
		
		if ( dim.length < 3 )
			this.depth = 1;
		else
			this.depth = dim[ 2 ];
		
		mirror = new ArrayList<A>( depth );
	}
	
	ImagePlusContainer( final ImagePlusContainerFactory factory, final A creator, final int[] dim, final int entitiesPerPixel ) 
	{
		this( factory, dim, entitiesPerPixel );				
		
		for ( int i = 0; i < depth; ++i )
			mirror.add( creator.createArray( width * height ));
	}

	public ImagePlus getImagePlus() throws ImgLibException 
	{ 
		throw new ImgLibException( this, "has no ImagePlus instance, it is not a standard type of ImagePlus" ); 
	}

	@Override
	public A update( final Cursor<?> c ) { return mirror.get( c.getStorageIndex() ); }
	
	protected static int[] getCorrectDimensionality( final ImagePlus imp )
	{
		int numDimensions = 3;
				
		if ( imp.getStackSize() == 1 )
			--numDimensions;
		
		if ( imp.getHeight() == 1 )
			--numDimensions;
		
		final int[] dim = new int[ numDimensions ];
		dim[ 0 ] = imp.getWidth();

		if ( numDimensions >= 2 )
			dim[ 1 ] = imp.getHeight();
		
		if ( numDimensions == 3 )
			dim[ 2 ] = imp.getStackSize();
		
		return dim;
	}

	@Override
	public int getWidth() { return width; }
	@Override
	public int getHeight() { return height; }
	@Override
	public int getDepth() { return depth; }

	public final int getPos( final int[] l ) 
	{
		if ( numDimensions > 1 )
			return l[ 1 ] * width + l[ 0 ];
		else
			return l[ 0 ];
	}	

	@Override
	public Cursor<T> createCursor( final Image<T> image ) 
	{
		return new ImagePlusCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableCursor<T> createLocalizableCursor( final Image<T> image ) 
	{
		return new ImagePlusLocalizableCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizablePlaneCursor<T> createLocalizablePlaneCursor( final Image<T> image ) 
	{
		return new ImagePlusLocalizablePlaneCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( final Image<T> image ) 
	{
		return new ImagePlusLocalizableByDimCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer() );
	}

	@Override
	public LocalizableByDimCursor<T> createLocalizableByDimCursor( final Image<T> image, OutOfBoundsStrategyFactory<T> outOfBoundsFactory ) 
	{
		return new ImagePlusLocalizableByDimOutOfBoundsCursor<T>( this, image, linkedType.duplicateTypeOnSameDirectAccessContainer(), outOfBoundsFactory );
	}
	
	public ImagePlusContainerFactory getFactory() { return factory; }

	@Override
	public void close()
	{
		for ( final A array : mirror )
			array.close();
	}
}
