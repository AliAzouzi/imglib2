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

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.ImageWindow;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import net.imglib2.RandomAccessible;
import net.imglib2.display.ARGBScreenImage;
import net.imglib2.display.XYRandomAccessibleProjector;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NLinearInterpolatorFactory;
import net.imglib2.interpolation.randomaccess.NearestNeighborInterpolatorFactory;
import net.imglib2.type.numeric.ARGBType;
import net.imglib2.type.numeric.NumericType;

/**
 * Base class for interactive ImgLib2 examples.
 *
 * @author Stephan Saalfeld
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public abstract class AbstractInteractiveExample< T extends NumericType< T > > implements KeyListener, MouseWheelListener, MouseListener, MouseMotionListener
{
	/**
	 * Register mouse and key listeners. Backup and restore old listeners.
	 */
	final protected class GUI
	{
		final private ImageWindow window;

		final private Canvas canvas;

		final private ImageJ ij;

		/* backup */
		private KeyListener[] windowKeyListeners;

		private KeyListener[] canvasKeyListeners;

		private KeyListener[] ijKeyListeners;

		private MouseListener[] canvasMouseListeners;

		private MouseMotionListener[] canvasMouseMotionListeners;

		private MouseWheelListener[] windowMouseWheelListeners;

		GUI( final ImagePlus imp )
		{
			window = imp.getWindow();
			canvas = imp.getCanvas();

			ij = IJ.getInstance();
		}

		/**
		 * Add new event handlers.
		 */
		final void takeOverGui()
		{
			backupGui();
			clearGui();

			canvas.addKeyListener( AbstractInteractiveExample.this );
			window.addKeyListener( AbstractInteractiveExample.this );

			if ( ij != null )
				ij.addKeyListener( AbstractInteractiveExample.this );

			canvas.addMouseMotionListener( AbstractInteractiveExample.this );
			canvas.addMouseListener( AbstractInteractiveExample.this );

			window.addMouseWheelListener( AbstractInteractiveExample.this );
		}

		/**
		 * Restore the previously active Event handlers.
		 */
		final void restoreGui()
		{
			clearGui();
			for ( final KeyListener l : canvasKeyListeners )
				canvas.addKeyListener( l );
			for ( final KeyListener l : windowKeyListeners )
				window.addKeyListener( l );
			if ( ij != null )
				for ( final KeyListener l : ijKeyListeners )
					ij.addKeyListener( l );
			for ( final MouseListener l : canvasMouseListeners )
				canvas.addMouseListener( l );
			for ( final MouseMotionListener l : canvasMouseMotionListeners )
				canvas.addMouseMotionListener( l );
			for ( final MouseWheelListener l : windowMouseWheelListeners )
				window.addMouseWheelListener( l );
		}

		/**
		 * Backup active event handlers for restore.
		 */
		private final void backupGui()
		{
			canvasKeyListeners = canvas.getKeyListeners();
			windowKeyListeners = window.getKeyListeners();
			if ( ij != null )
				ijKeyListeners = ij.getKeyListeners();
			canvasMouseListeners = canvas.getMouseListeners();
			canvasMouseMotionListeners = canvas.getMouseMotionListeners();
			windowMouseWheelListeners = window.getMouseWheelListeners();
		}

		/**
		 * Remove both ours and the backed up event handlers.
		 */
		private final void clearGui()
		{
			for ( final KeyListener l : canvasKeyListeners )
				canvas.removeKeyListener( l );
			for ( final KeyListener l : windowKeyListeners )
				window.removeKeyListener( l );
			if ( ij != null )
				for ( final KeyListener l : ijKeyListeners )
					ij.removeKeyListener( l );
			for ( final MouseListener l : canvasMouseListeners )
				canvas.removeMouseListener( l );
			for ( final MouseMotionListener l : canvasMouseMotionListeners )
				canvas.removeMouseMotionListener( l );
			for ( final MouseWheelListener l : windowMouseWheelListeners )
				window.removeMouseWheelListener( l );

			canvas.removeKeyListener( AbstractInteractiveExample.this );
			window.removeKeyListener( AbstractInteractiveExample.this );
			if ( ij != null )
				ij.removeKeyListener( AbstractInteractiveExample.this );
			canvas.removeMouseListener( AbstractInteractiveExample.this );
			canvas.removeMouseMotionListener( AbstractInteractiveExample.this );
			window.removeMouseWheelListener( AbstractInteractiveExample.this );
		}
	}

	/**
	 * Display.
	 */
	protected ImagePlus imp;

	/**
	 * Wrapped by {@link #imp}.
	 */
	protected ARGBScreenImage screenImage;

	/**
	 * Currently active projector, used by {@link MappingThread} to re-paint the
	 * display. It maps the source data to {@link #screenImage}.
	 */
	protected XYRandomAccessibleProjector< T, ARGBType > projector;

	/**
	 * ImgLib2 logo overlay painter.
	 */
	final protected LogoPainter logo;

	/**
	 * Repaint display.
	 *
	 * @see AbstractInteractiveExample#projector
	 * @see AbstractInteractiveExample#screenImage
	 * @see AbstractInteractiveExample#logo
	 * @see AbstractInteractiveExample#imp
	 */
	final public class MappingThread extends Thread
	{
		private boolean pleaseRepaint;

		public MappingThread()
		{
			this.setName( "MappingThread" );
		}

		@Override
		public void run()
		{
			while ( !isInterrupted() )
			{
				final boolean b;
				synchronized ( this )
				{
					b = pleaseRepaint;
					pleaseRepaint = false;
				}
				if ( b )
				{
					copyState();
					projector.map();
					logo.paint( screenImage );
					// imp.setImage( screenImage.image() );
					visualize();
					imp.updateAndDraw();
				}
				synchronized ( this )
				{
					try
					{
						if ( !pleaseRepaint )
							wait();
					}
					catch ( final InterruptedException e )
					{}
				}
			}
		}

		/**
		 * request repaint.
		 */
		public void repaint()
		{
			synchronized ( this )
			{
				pleaseRepaint = true;
				notify();
			}
		}
	}

	public AbstractInteractiveExample()
	{
		this( "src/test/java/resources/imglib2-logo2.png" );
	}

	public AbstractInteractiveExample( final String overlayFilename )
	{
		logo = new LogoPainter();
	}

	abstract protected void copyState();

	abstract protected void visualize();

	final static protected String NL = System.getProperty( "line.separator" );

	protected GUI gui;

	final protected NearestNeighborInterpolatorFactory< T > nnFactory = new NearestNeighborInterpolatorFactory< T >();

	final protected NLinearInterpolatorFactory< T > nlFactory = new NLinearInterpolatorFactory< T >();

	protected int interpolation = 0;

	protected MappingThread painter;

	abstract protected XYRandomAccessibleProjector< T, ARGBType > createProjector( final InterpolatorFactory< T, RandomAccessible< T > > interpolatorFactory );

	abstract protected void update();

	protected void toggleInterpolation()
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
	}

	final protected float keyModfiedSpeed( final int modifiers )
	{
		if ( ( modifiers & KeyEvent.SHIFT_DOWN_MASK ) != 0 )
			return 10;
		else if ( ( modifiers & KeyEvent.CTRL_DOWN_MASK ) != 0 )
			return 0.1f;
		else
			return 1;
	}

	@Override
	public void keyReleased( final KeyEvent e )
	{}

	@Override
	public void keyTyped( final KeyEvent e )
	{}

	@Override
	public void mouseMoved( final MouseEvent e )
	{}

	@Override
	public void mouseClicked( final MouseEvent e )
	{}

	@Override
	public void mouseEntered( final MouseEvent e )
	{}

	@Override
	public void mouseExited( final MouseEvent e )
	{}

	@Override
	public void mouseReleased( final MouseEvent e )
	{}
}
