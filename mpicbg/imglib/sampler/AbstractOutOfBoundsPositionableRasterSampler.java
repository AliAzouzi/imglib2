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
package mpicbg.imglib.sampler;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.location.RasterLocalizable;
import mpicbg.imglib.location.RasterPositionable;
import mpicbg.imglib.location.VoidPositionable;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategy;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public abstract class AbstractOutOfBoundsPositionableRasterSampler< T extends Type< T > > extends AbstractRasterSampler< T > implements PositionableRasterSampler< T >
{
	/* performs the actual moves and generates/queries a Type */
	final protected OutOfBoundsStrategy< T > outOfBounds;
	
	/* linked RasterPositionable following the raster moves */
	protected RasterPositionable linkedRasterPositionable = VoidPositionable.getInstance();	
	
	
	public AbstractOutOfBoundsPositionableRasterSampler( final Container< T > container, final Image< T > image, final OutOfBoundsStrategy< T > outOfBounds )
	{
		super( container, image );
		
		this.outOfBounds = outOfBounds;
	}
	
	public AbstractOutOfBoundsPositionableRasterSampler( final Container< T > container, final Image< T > image, final OutOfBoundsStrategyFactory< T > outOfBoundsFactory )
	{
		super( container, image );
		
		this.outOfBounds = outOfBoundsFactory.createStrategy( this );
	}
	
	final public boolean isOutOfBounds(){ return outOfBounds.isOutOfBounds(); }
	
	
	/* Sampler */
	
	@Override
	final public T type(){ return outOfBounds.type(); }
	
	
	/* RasterLocalizable */
	
	@Override
	final public void localize( final int[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public void localize( final long[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public int getIntPosition( final int dim ){ return outOfBounds.getIntPosition( dim ); }
	
	@Override
	final public long getLongPosition( final int dim ){ return outOfBounds.getLongPosition( dim ); }
	
	
	/* Localizable */
	
	@Override
	final public void localize( final float[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public void localize( final double[] position ){ outOfBounds.localize( position ); }
	
	@Override
	final public double getDoublePosition( final int dim ){ return outOfBounds.getDoublePosition( dim ); }
	
	@Override
	final public float  getFloatPosition( final int dim ){ return outOfBounds.getFloatPosition( dim ); }
	
	@Override
	public String getLocationAsString() { return outOfBounds.getLocationAsString(); }
	
	@Override
	public String toString() { return getLocationAsString() + " = " + type(); }
	
	
	/* RasterPositionable */
	
	@Override
	final public void fwd( final int dim )
	{
		outOfBounds.fwd( dim );		
		linkedRasterPositionable.fwd( dim );
	}
	
	@Override
	final public void bck( final int dim )
	{
		outOfBounds.bck( dim );		
		linkedRasterPositionable.bck( dim );
	}
	
	@Override
	final public void move( final int distance, final int dim )
	{
		outOfBounds.move( distance, dim );
		linkedRasterPositionable.move( distance, dim );
	}
	
	@Override
	final public void move( final long distance, final int dim )
	{
		outOfBounds.move( distance, dim );
		linkedRasterPositionable.move( distance, dim );
	}
	
	@Override
	final public void moveTo( final RasterLocalizable localizable )
	{
		outOfBounds.moveTo( localizable );
		linkedRasterPositionable.moveTo( localizable );
	}
	
	@Override
	final public void moveTo( final int[] position )
	{
		outOfBounds.moveTo( position );
		linkedRasterPositionable.moveTo( position );
	}
	
	@Override
	final public void moveTo( final long[] position )
	{
		outOfBounds.moveTo( position );
		linkedRasterPositionable.moveTo( position );
	}
	
	@Override
	final public void setPosition( final int distance, final int dim )
	{
		outOfBounds.setPosition( distance, dim );
		linkedRasterPositionable.setPosition( distance, dim );
	}
	
	@Override
	final public void setPosition( final long distance, final int dim )
	{
		outOfBounds.setPosition( distance, dim );
		linkedRasterPositionable.setPosition( distance, dim );
	}
	
	@Override
	final public void setPosition( final RasterLocalizable localizable )
	{
		outOfBounds.setPosition( localizable );
		linkedRasterPositionable.setPosition( localizable );
	}
	
	@Override
	final public void setPosition( final int[] position )
	{
		outOfBounds.setPosition( position );
		linkedRasterPositionable.setPosition( position );
	}
	
	@Override
	final public void setPosition( final long[] position )
	{
		outOfBounds.setPosition( position );
		linkedRasterPositionable.setPosition( position );
	}
	
	
	/* RasterSampler */
	
	@Override
	public void close()
	{
		super.close();
		outOfBounds.close();
	}
}
