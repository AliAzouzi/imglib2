/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package tobias;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;

import net.imglib2.converter.Converter;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

abstract public class AbstractInteractive2DViewer< T extends RealType< T > & NativeType< T > > extends AbstractInteractiveExample< T > implements TransformEventHandler2D.TransformListener
{
	final static protected double step = Math.PI /180;
	protected double theta = 0.0;
	protected double scale = 1.0;
	protected double oTheta = 0;


	@Override
	final protected synchronized void copyState()
	{
		reducedAffineCopy.set( reducedAffine );
	}

	@Override
	final protected void visualize()
	{
		final Image image = imp.getImage();
		final Graphics2D graphics = ( Graphics2D )image.getGraphics();
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setPaint( Color.WHITE );
		graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
		graphics.drawString("theta = " + String.format( "%.3f", ( theta / Math.PI * 180 ) ), 10, 10 );
		graphics.drawString("scale = " + String.format( "%.3f", ( scale ) ), 10, 20 );
	}


	final protected ArrayList< AffineTransform2D > list = new ArrayList< AffineTransform2D >();
	final protected AffineTransform2D affine = new AffineTransform2D();
	final protected AffineTransform2D reducedAffine = new AffineTransform2D();
	final protected AffineTransform2D reducedAffineCopy = new AffineTransform2D();

	final protected Converter< T, ARGBType > converter;

	@Override
	public void setTransform( final AffineTransform2D transform )
	{
		synchronized ( reducedAffine )
		{
			affine.set( transform );
		}
		update();
	}

	@Override
	public void quit()
	{
		painter.interrupt();
	}

	public AbstractInteractive2DViewer( final Converter< T, ARGBType > converter )
	{
		this.converter = converter;
	}

	final protected void update()
	{
		synchronized ( reducedAffine )
		{
			TransformEventHandler2D.reduceAffineTransformList( list, reducedAffine );
		}

		painter.repaint();
	}
}
