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
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator1D;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.Type;

public class NearestNeighborInterpolator1D<T extends Type<T>> extends NearestNeighborInterpolator<T> implements Interpolator1D<T>
{
	float x;
	
	protected NearestNeighborInterpolator1D( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		super( img, interpolatorFactory, outsideStrategyFactory );
		
		x = 0;
	}

	/**
	 * Returns the typed image the interpolator is working on
	 * 
	 * @return - the image
	 */
	@Override
	public Image<T> getImage() { return img; }		
	
	@Override
	public void getPosition( final float[] position ) { position[ 0 ] = x; }

	@Override
	public float[] getPosition() { return new float[]{ x }; }	
	
	@Override
	public void close() { cursor.close(); }

	@Override
	public T getType() { return type; }
	
	@Override
	public void moveTo( final float x )
	{		
		this.x = x;
		
		final int ix = MathLib.round( x ); 
		
		cursor.move( ix - cursor.getPosition( 0 ), 0 );
	}

	@Override
	public void moveTo( final float[] position ) { moveTo( position[0] ); }

	@Override
	public void moveRel( final float x )
	{
		this.x += x;
		
		cursor.move( MathLib.round( this.x ) - cursor.getPosition( 0 ), 0 );
	}
	
	@Override
	public void moveRel( final float[] vector ) { moveRel( vector[0] ); }
	
	@Override
	public void setPosition( final float x )
	{
		this.x = x;

		cursor.setPosition( MathLib.round( x ), 0 );
	}
	
	@Override
	public void setPosition( final float[] position ) { setPosition( position[0] ); }

	@Override
	public float getX() { return x;	}
}
