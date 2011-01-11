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

import mpicbg.imglib.RasterInterval;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.outofbounds.RasterOutOfBoundsFactory;
import mpicbg.imglib.sampler.PositionableRasterSampler;
import mpicbg.imglib.sampler.RasterIterator;
import mpicbg.imglib.sampler.special.OrthoSliceIterator;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public interface Container< T extends Type< T > > extends RasterInterval< T, Container< T >, PositionableRasterSampler< T >, RasterIterator< T > >
{
	public RasterIterator< T > createRasterIterator( final Image< T > image );
	public RasterIterator< T > createLocalizingRasterIterator( final Image< T > image );
	public PositionableRasterSampler< T > createPositionableRasterSampler( final Image< T > image );
	public PositionableRasterSampler< T > createPositionableRasterSampler( final Image< T > image, final RasterOutOfBoundsFactory< T > outOfBoundsFactory );
	public OrthoSliceIterator< T > createOrthoSliceIterator( final Image< T > image, final int x, final int y, final int[] position );
	
	public void close();

	public ContainerFactory getFactory();

	public long getId();

	public long numPixels();

	public boolean compareStorageContainerDimensions( final Container< ? > img );
	public boolean compareStorageContainerCompatibility( final Container< ? > img );

}
