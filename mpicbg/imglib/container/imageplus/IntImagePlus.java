/**
 * Copyright (c) 2009--2010, Johannes Schindelin
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
 * @author Stephan Preibisch & Johannes Schindelin
 */
package mpicbg.imglib.container.imageplus;

import ij.IJ;
import ij.ImagePlus;


import mpicbg.imglib.container.basictypecontainer.IntContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class IntImagePlus<T extends Type<T>> extends ImagePlusContainer<T> implements IntContainer<T> 
{
	final ImagePlus image;
	final int[][] mirror;
	int[] cache = null;
	
	public IntImagePlus( final ImagePlusContainerFactory factory, final int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );

		image = IJ.createImage( "image", "RGB Black", width * entitiesPerPixel, height, depth );
		mirror = new int[ depth ][];
		
		for ( int i = 0; i < depth; ++i )
			mirror[ i ] = (int[])image.getStack().getProcessor( i+1 ).getPixels();
	}

	public IntImagePlus( final ImagePlus image, final ImagePlusContainerFactory factory ) 
	{
		super( factory, ImagePlusContainer.getCorrectDimensionality(image), 1 );
		
		this.image = image;
		mirror = new int[ depth ][];
		
		for ( int i = 0; i < depth; ++i )
			mirror[ i ] = (int[])image.getStack().getProcessor( i+1 ).getPixels();
	}
	
	@Override
	public int getValue( final int index )  { return cache[ index ]; }

	@Override
	public void setValue( final int index, final int value ) { cache[ index ] = value; }
	
	@Override
	public void update( final Cursor<?> c ) { cache = mirror[ c.getStorageIndex() ]; }

	public int[] getCurrentStorageArray( Cursor<?> c ) 
	{
		return mirror[ c.getStorageIndex() ];
	}

	@Override
	public void close() { image.close(); }

	@Override
	public ImagePlus getImagePlus() { return image;	}
}

