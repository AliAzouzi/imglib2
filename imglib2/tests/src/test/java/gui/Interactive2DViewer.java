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

package gui;

import ij.ImagePlus;
import ij.process.ColorProcessor;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.util.ArrayList;

import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.converter.Converter;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.Interpolant;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.realtransform.AffineGet;
import net.imglib2.realtransform.AffineRandomAccessible;
import net.imglib2.realtransform.AffineTransform2D;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.RealType;

public class Interactive2DViewer< T extends RealType< T > & NativeType< T > > extends AbstractInteractiveViewer implements TransformEventHandler2D.TransformListener
{
	/**
	 * the {@link RandomAccessible} to display
	 */
	final protected RandomAccessible< T > source;

	/**
	 * converts {@link #source} type T to ARGBType for display
	 */
	final protected Converter< T, ARGBType > converter;

	/**
	 * Display.
	 */
	final protected ImagePlus imp;

	/**
	 * Used to render into {@link #imp}.
	 */
	final protected ARGBScreenImage screenImage;

	/**
	 * Currently active projector, used to re-paint the display. It maps the
	 * {@link #source} data to {@link #screenImage}.
	 */
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/**
	 * ImgLib2 logo overlay painter.
	 */
	final protected LogoPainter logo = new LogoPainter();

	/**
	 * Key and mouse handler, that maintains the current transformation.
	 * It triggers {@link #setTransform(AffineTransform2D),
	 * {@link #toggleInterpolation()}, and {@link #quit()}.
	 */
	protected TransformEventHandler2D transformEventHandler;

	/**
	 * Register and restore key and mouse handlers.
	 */
	protected GUI< TransformEventHandler2D > gui;



	final protected NearestNeighborInterpolatorFactory< T > nnFactory = new NearestNeighborInterpolatorFactory< T >();

	final protected NLinearInterpolatorFactory< T > nlFactory = new NLinearInterpolatorFactory< T >();


	final protected ArrayList< AffineTransform2D > list = new ArrayList< AffineTransform2D >();

	final protected AffineTransform2D affine = new AffineTransform2D();

	final protected AffineTransform2D reducedAffine = new AffineTransform2D();

	final protected AffineTransform2D reducedAffineCopy = new AffineTransform2D();

	/**
	 *
	 * @param width
	 *            width of the display window
	 * @param height
	 *            height of the display window
	 * @param source
	 *            the {@link RandomAccessible} to display
	 * @param converter
	 *            converts {@link #source} type T to ARGBType for display
	 * @param initialTransform
	 *            initial transformation to apply to the {@link #source}
	 */
	public Interactive2DViewer( final int width, final int height, final RandomAccessible< T > source, final Converter< T, ARGBType > converter, final AffineTransform2D initialTransform )
	{
		this.converter = converter;
		this.source = source;

		final ColorProcessor cp = new ColorProcessor( width, height );
		screenImage = new ARGBScreenImage( cp.getWidth(), cp.getHeight(), ( int[] ) cp.getPixels() );
		projector = createProjector( nnFactory );

		if ( initialTransform != null )
			list.add( initialTransform );
		list.add( affine );
		TransformEventHandler2D.reduceAffineTransformList( list, reducedAffine );

		imp = new ImagePlus( "argbScreenProjection", cp );
		imp.show();
		imp.getCanvas().setMagnification( 1.0 );
		imp.updateAndDraw();

		// create and register key and mouse handler
		transformEventHandler = new TransformEventHandler2D( new FinalInterval( new long[] { imp.getWidth(), imp.getHeight() } ), this );
		gui = new GUI< TransformEventHandler2D >( imp );
		gui.takeOverGui( transformEventHandler );

		requestRepaint();
		startPainter();
	}

	public Interactive2DViewer( final int width, final int height, final RandomAccessible< T > source, final Converter< T, ARGBType > converter )
	{
		this( width, height, source, converter, null );
	}

	// -- TransformEventHandler2D.TransformListener --

	@Override
	public void setTransform( final AffineTransform2D transform )
	{
		synchronized ( reducedAffine )
		{
			affine.set( transform );
			TransformEventHandler2D.reduceAffineTransformList( list, reducedAffine );
		}
		requestRepaint();
	}

	@Override
	public void quit()
	{
		stopPainter();
		if ( imp != null )
		{
			gui.restoreGui();
		}
	}

	protected int interpolation = 0;

	@Override
	public void toggleInterpolation()
	{
		++interpolation;
		interpolation %= 2;
		switch ( interpolation )
		{
		case 0:
			projector = createProjector( nnFactory );
			break;
		case 1:
			projector = createProjector( nlFactory );
			break;
		}
		requestRepaint();
	}

	protected XYRandomAccessibleProjector< T, ARGBType > createProjector( final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory )
	{
		final Interpolant< T, RandomAccessible< T > > interpolant = new Interpolant< T, RandomAccessible< T > >( source, interpolatorFactory );
		final AffineRandomAccessible< T, AffineGet > mapping = new AffineRandomAccessible< T, AffineGet >( interpolant, reducedAffineCopy.inverse() );
		return new XYRandomAccessibleProjector< T, ARGBType >( mapping, screenImage, converter );
	}


	// -- AbstractInteractiveExample --
	long drawtime;

	@Override
	public void paint()
	{
		synchronized ( reducedAffine )
		{
			reducedAffineCopy.set( reducedAffine );
		}
		final long start = System.currentTimeMillis();
		projector.map();
		drawtime = System.currentTimeMillis() - start;
		logo.paint( screenImage );
		visualize();
		imp.updateAndDraw();
	}

	final protected void visualize()
	{
		final Image image = imp.getImage();
		final Graphics2D graphics = ( Graphics2D ) image.getGraphics();
		graphics.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		graphics.setPaint( Color.WHITE );
		graphics.setFont( new Font( "SansSerif", Font.PLAIN, 8 ) );
		graphics.drawString( "theta = " + String.format( "%.3f", ( transformEventHandler.getTheta() / Math.PI * 180 ) ), 10, 10 );
		graphics.drawString( "scale = " + String.format( "%.3f", ( transformEventHandler.getScale() ) ), 10, 20 );
		graphics.drawString( "rendered in " + drawtime + "ms", 10, 30 );
	}
}
