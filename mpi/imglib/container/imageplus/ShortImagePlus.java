/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Johannes Schindelin
 */
package mpi.imglib.container.imageplus;

import ij.IJ;
import ij.ImagePlus;

import mpi.imglib.container.basictypecontainer.ShortContainer;

import mpi.imglib.cursor.Cursor;
import mpi.imglib.type.Type;

public class ShortImagePlus<T extends Type<T>> extends ImagePlusContainer<T> implements ShortContainer<T> 
{
	final ImagePlus image;
	final short[][] mirror;
	
	public ShortImagePlus( ImagePlusContainerFactory factory, int[] dim, final int entitiesPerPixel ) 
	{
		super( factory, dim, entitiesPerPixel );
		
		image = IJ.createImage( "image", "16-Bit Black", dim[0], dim[1], dim[2]);
		mirror = new short[ dim[2] ][];
		
		for ( int i = 0; i < dim[ 2 ]; ++i )
			mirror[ i ] = (short[])image.getStack().getProcessor( i+1 ).getPixels();
}

	public ShortImagePlus( ImagePlus image, ImagePlusContainerFactory factory ) 
	{
		this( factory, new int[]{ image.getWidth(), image.getHeight(), image.getStackSize()}, 1 );
	}
	
	@Override
	public short[] getCurrentStorageArray( Cursor<?> c ) 
	{
		return mirror[ c.getStorageIndex() ];
	}

	@Override
	public void close() { image.close(); }

	@Override
	public ImagePlus getImagePlus() { return image;	}
}

