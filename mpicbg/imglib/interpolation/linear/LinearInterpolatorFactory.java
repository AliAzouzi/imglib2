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
package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.RealType;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class LinearInterpolatorFactory< T extends NumericType< T > > extends InterpolatorFactory< T, Interpolator< T > >
{
	public LinearInterpolatorFactory( final OutOfBoundsStrategyFactory< T > outOfBoundsStrategyFactory )
	{
		super( outOfBoundsStrategyFactory );
	}
	
	@Override
	public Interpolator< T > createSampler( final Image< T > img )
	{
		if ( img.numDimensions() == 1 )
		{
			return new LinearInterpolator1D< T >( img, outOfBoundsStrategyFactory );
		}
		else if ( img.numDimensions() == 2 )
		{
			return new LinearInterpolator2D< T >( img, outOfBoundsStrategyFactory );
		}
		else if ( img.numDimensions() == 3 )
		{
			if ( RealType.class.isInstance( img.createType() ) )
			{
				return new LinearInterpolator3D< T >( img, outOfBoundsStrategyFactory ); 
				//LinearInterpolator3DRealType( img, outOfBoundsStrategyFactory );
			}
			else
				return new LinearInterpolator3D< T >( img, outOfBoundsStrategyFactory );
		}
		else
		{
			return new LinearInterpolator< T >( img, outOfBoundsStrategyFactory );
		}
	}
}
