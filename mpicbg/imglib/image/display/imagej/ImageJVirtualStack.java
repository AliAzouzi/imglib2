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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.image.display.imagej;

import mpicbg.imglib.cursor.LocalizablePlaneCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.Type;
import ij.ImagePlus;
import ij.ImageStack;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

public class ImageJVirtualStack<T extends Type<T>> extends ImageStack
{
	final Display<T> display;
	final Image<T> img;
	final int type, dimX, dimY, dimZ, sizeX, sizeY, sizeZ;
	final int[] dimensionPositions;
	
	/**
	 * Constructs a virtual stack of up to 3 arbitrary dimensions
	 * 
	 *   Image<T> img - the image
	 *   int type - the type of the Virtual Stack (ImageJFunctions.GRAY8, ImageJFunctions.GRAY32 or ImageJFunctions.COLOR_RGB)
	 *   int[] dim - which dimensions to display, can be up to three, but at least one. However
	 *   the array has to always have a size of 3. 
	 *   int[] dimensionPositions - the positions inside all dimensions that might be untouched
	 */
	public ImageJVirtualStack( final Image<T> img, final int type, final int[] dim, final int[] dimensionPositions )
	{
		super( img.getDimension( dim[ 0 ] ), img.getDimension( dim[ 1 ] ), img.getDimension( dim[ 2 ] ) );
		
		this.img = img;
		this.type = type;
		this.display = img.getDisplay();
		
		this.dimX = dim[ 0 ];
		this.dimY = dim[ 1 ];
		this.dimZ = dim[ 2 ];
		
		this.dimensionPositions = dimensionPositions;
		
		sizeX = img.getDimension( dim[ 0 ] );
		sizeY = img.getDimension( dim[ 1 ] );
		sizeZ = img.getDimension( dim[ 2 ] );
	}

	/**
	 * Constructs a virtual stack of type ImageJFunctions.GRAY32 of up to 3 arbitrary dimensions
	 * 
	 *   Image<T> img - the image
	 *   int[] dim - which dimensions to display, can be up to three, but at least one. However
	 *   the array has to always have a size of 3. 
	 *   int[] dimensionPositions - the positions inside all dimensions that might be untouched
	 */
	public ImageJVirtualStack( final Image<T> img, final int[] dim, final int[] dimensionPositions )
	{
		this( img, ImagePlus.GRAY32, dim, dimensionPositions );
	}
		
    
    /** Returns an ImageProcessor for the specified slice,
        were 1<=n<=nslices. Returns null if the stack is empty.
    */
    public ImageProcessor getProcessor( final int n ) 
    {
        final ImageProcessor ip;
        
        if ( sizeZ == 0 )
            return null;
        
        if ( n<1 || n>sizeZ )
            throw new IllegalArgumentException("no slice " + n);

        final int[] dimPos = dimensionPositions.clone();
        
        if ( dimZ < img.getNumDimensions() )
        	dimPos[ dimZ ] = n - 1;
                
        switch(type) 
        {
        	case ImagePlus.GRAY8:
        		ip = new ByteProcessor( sizeX, sizeY, extractSliceByte( img, display, dimX, dimY, dimPos ), null); break;
         	case ImagePlus.COLOR_RGB:
        		ip = new ColorProcessor( sizeX, sizeY, extractSliceRGB( img, display, dimX, dimY, dimPos )); break;
        	default:
        		ip = new FloatProcessor( sizeX, sizeY, extractSliceFloat( img, display, dimX, dimY, dimPos ), null); 
        		ip.setMinAndMax( display.getMin(), display.getMax() );
        		break;
        }
 
        return ip;
    }   
 
    public static <T extends Type<T>> float[] extractSliceFloat( final Image<T> img, final Display<T> display, final int dimX, final int dimY, final int[] dimensionPositions )
    {
		final int sizeX = img.getDimension( dimX );
		final int sizeY = img.getDimension( dimY );
    	
    	final LocalizablePlaneCursor<T> cursor = img.createLocalizablePlaneCursor();		
		cursor.reset( dimX, dimY, dimensionPositions );   	
		
		final T type = cursor.getType();
	    	
    	// store the slice image
    	float[] sliceImg = new float[ sizeX * sizeY ];
    	
    	if ( dimY < img.getNumDimensions() )
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();
	    		sliceImg[ cursor.getPosition( dimX ) + cursor.getPosition( dimY ) * sizeX ] = display.get32Bit(type);    		
	    	}
    	}
    	else // only a 1D image
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();
	    		sliceImg[ cursor.getPosition( dimX ) ] = display.get32Bit(type);    		
	    	}    		
    	}
    	
    	cursor.close();

    	return sliceImg;
    }

    public static <T extends Type<T>> int[] extractSliceRGB( final Image<T> img, final Display<T> display, final int dimX, final int dimY, final int[] dimensionPositions )
    {
		final int sizeX = img.getDimension( dimX );
		final int sizeY = img.getDimension( dimY );
    	
    	final LocalizablePlaneCursor<T> cursor = img.createLocalizablePlaneCursor();		
		cursor.reset( dimX, dimY, dimensionPositions );   	
		
		final T type = cursor.getType();
	    	
    	// store the slice image
    	int[] sliceImg = new int[ sizeX * sizeY ];
    	
    	if ( dimY < img.getNumDimensions() )
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();
	    		sliceImg[ cursor.getPosition( dimX ) + cursor.getPosition( dimY ) * sizeX ] = display.get8BitARGB(type);    		
	    	}
    	}
    	else // only a 1D image
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();
	    		sliceImg[ cursor.getPosition( dimX ) ] = display.get8BitARGB(type);    		
	    	}
    	}

    	return sliceImg;
    }

    public static <T extends Type<T>> byte[] extractSliceByte( final Image<T> img, final Display<T> display, final int dimX, final int dimY, final int[] dimensionPositions )
    {
		final int sizeX = img.getDimension( dimX );
		final int sizeY = img.getDimension( dimY );
    	
    	final LocalizablePlaneCursor<T> cursor = img.createLocalizablePlaneCursor();		
		cursor.reset( dimX, dimY, dimensionPositions );   	
		
		final T type = cursor.getType();
	    	
    	// store the slice image
    	byte[] sliceImg = new byte[ sizeX * sizeY ];
    	
    	if ( dimY < img.getNumDimensions() )
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();
	    		sliceImg[ cursor.getPosition( dimX ) + cursor.getPosition( dimY ) * sizeX ] = display.get8BitSigned(type);    		
	    	}
    	}
    	else // only a 1D image
    	{
	    	while ( cursor.hasNext() )
	    	{
	    		cursor.fwd();
	    		sliceImg[ cursor.getPosition( dimX ) ] = display.get8BitSigned(type);    		
	    	}    		
    	}

    	return sliceImg;
    }
    
	 /** Obsolete. Short images are always unsigned. */
    public void addUnsignedShortSlice(String sliceLabel, Object pixels) {}
    
    /** Adds the image in 'ip' to the end of the stack. */
    public void addSlice(String sliceLabel, ImageProcessor ip) {}
    
    /** Adds the image in 'ip' to the stack following slice 'n'. Adds
        the slice to the beginning of the stack if 'n' is zero. */
    public void addSlice(String sliceLabel, ImageProcessor ip, int n) {}
    
    /** Deletes the specified slice, were 1<=n<=nslices. */
    public void deleteSlice(int n) {}
    
    /** Deletes the last slice in the stack. */
    public void deleteLastSlice() {}
        
    /** Updates this stack so its attributes, such as min, max,
        calibration table and color model, are the same as 'ip'. */
    public void update(ImageProcessor ip) {}
    
    /** Returns the pixel array for the specified slice, were 1<=n<=nslices. */
    public Object getPixels(int n) { return getProcessor(n).getPixels(); }
    
    /** Assigns a pixel array to the specified slice,
        were 1<=n<=nslices. */
    public void setPixels(Object pixels, int n) {}
    
    /** Returns the stack as an array of 1D pixel arrays. Note
        that the size of the returned array may be greater than
        the number of slices currently in the stack, with
        unused elements set to null. */
    public Object[] getImageArray() { return null; }
    
    /** Returns the slice labels as an array of Strings. Note
        that the size of the returned array may be greater than
        the number of slices currently in the stack. Returns null
        if the stack is empty or the label of the first slice is null.  */
    public String[] getSliceLabels() { return null; }
    
    /** Returns the label of the specified slice, were 1<=n<=nslices.
        Returns null if the slice does not have a label. For DICOM
        and FITS stacks, labels may contain header information. */
    public String getSliceLabel(int n) { return "" + n; }
    
    /** Returns a shortened version (up to the first 60 characters or first newline and 
        suffix removed) of the label of the specified slice.
        Returns null if the slice does not have a label. */
    public String getShortSliceLabel(int n) { return getSliceLabel(n); }

    /** Sets the label of the specified slice, were 1<=n<=nslices. */
    public void setSliceLabel(String label, int n) {}

    /** Returns true if this is a 3-slice RGB stack. */
    public boolean isRGB() { return false; }
    
    /** Returns true if this is a 3-slice HSB stack. */
    public boolean isHSB() { return false; }

    /** Returns true if this is a virtual (disk resident) stack. 
        This method is overridden by the VirtualStack subclass. */
    public boolean isVirtual() { return true; }

    /** Frees memory by deleting a few slices from the end of the stack. */
    public void trim() {}

    public String toString() { return ("Virtual Stack of " + img); }
}
