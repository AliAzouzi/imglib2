/*
Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.io.img.virtual;

import loci.common.DataTools;
import loci.formats.FormatTools;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.io.ImgOpener;


/**
 * 
 * @author Barry DeZonia
 *
 */
public class VirtualPlaneLoader {

	// -- instance variables --
	
	private VirtualImg<?> virtImage;
	private PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg;
	private long[] planeDims;
	private long[] planePosLoaded;
	
	// -- constructor --
	
	public VirtualPlaneLoader(VirtualImg<?> image, PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg) {
		this.virtImage = image;
		this.planeImg = planeImg;
		this.planeDims = new long[image.numDimensions()-2];
		for (int i = 0; i < planeDims.length; i++)
			this.planeDims[i] = image.dimension(i+2);
		this.planePosLoaded = new long[planeDims.length];
	}
	
	// -- public interface --
	
	public boolean virtualSwap(long[] pos) {
		if (!planeLoaded(pos)) {
			loadPlane(pos);
			return true;
		}
		return false;
	}

	@SuppressWarnings({"rawtypes","unchecked"})
	public void loadPlane(long[] pos) {
		for (int i = 0; i < planePosLoaded.length; i++)
			planePosLoaded[i] = pos[i+2];
		int planeNum = planeIndex(planeDims, planePosLoaded);
		byte[] planeBytes = null;
		try {
			planeBytes = virtImage.getReader().openBytes(planeNum);
		} catch (Exception e) {
			throw new IllegalArgumentException("cannot load plane "+planeNum);
		}
		Object primitivePlane = typeConvert(planeBytes);
		ArrayDataAccess<?> wrappedPlane = ImgOpener.makeArray(primitivePlane);
		((PlanarImg)planeImg).setPlane(0, wrappedPlane);
	}

	// -- private helpers --
	
	private boolean planeLoaded(long[] pos) {
		for (int i = 2; i < pos.length; i++)
			if (pos[i] != planePosLoaded[i-2])
				return false;
		return true;
	}

	private static int planeIndex(long[] planeDimensions, long[] planePos) {
		int index = 0;
		for (int i = 0; i < planePos.length; i++) {
			int delta = 1;
			for (int j = 1; j <= i; j++)
				delta *= planeDimensions[j-1];
			index += delta * planePos[i];
		}
		return index;
	}
	
	private Object typeConvert(byte[] bytes) {
		
		int bytesPerPix;
		boolean floating;
		boolean little = false; // TODO - how should this be set in general?

		switch (virtImage.getReader().getPixelType()) {
			case FormatTools.UINT8:
			case FormatTools.INT8:
				bytesPerPix = 1;
				floating = false;
				break;
			case FormatTools.UINT16:
			case FormatTools.INT16:
				bytesPerPix = 2;
				floating = false;
				break;
			case FormatTools.UINT32:
			case FormatTools.INT32:
				bytesPerPix = 4;
				floating = false;
				break;
			case FormatTools.FLOAT:
				bytesPerPix = 4;
				floating = true;
				break;
			case FormatTools.DOUBLE:
				bytesPerPix = 8;
				floating = true;
				break;
			// TODO - BF does not support long[] i.e. FormatTools.INT64
			default:
				throw new
					IllegalArgumentException("Cannot convert byte[] to unknown pixel type "+
						virtImage.getReader().getPixelType());
		}
		
		return DataTools.makeDataArray(bytes, bytesPerPix, floating, little);
	}
}
