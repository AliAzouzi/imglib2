/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.script.filter.fn;

import java.util.Iterator;

import net.imglib2.AbstractCursor;
import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.script.algorithm.fn.ImgProxy;
import net.imglib2.type.numeric.RealType;

/**
 * A read-only image that converts a source {@link Img} to the target {@link Type} on the fly.
 *
 * @param <R> The {@link Type} of the source image.
 * @param <T> The {@link Type} to convert into.
 *
 * @author Albert Cardona
 */
public class ConverterImgProxy<R extends RealType<R>, T extends RealType<T>> extends ImgProxy<T>
{
	private final Img<R> source;
	private final T type;

	/**
	 * Uses the {@param source} image as a read-only {@link Img}.
	 * The {@link Cursor} and {@link RandomAccess} generated by this proxy {@link Img}
	 * iterate the {@param source} image and convert the {@link Type} on the fly.
	 *
	 * @param source
	 * @param type
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ConverterImgProxy(final Img<R> source, final T type) {
		super((Img)source);
		this.source = source;
		this.type = type;
	}

	protected void convert(final R r, final T tmp) {
		tmp.setComplexNumber(r.getRealDouble(), r.getImaginaryDouble());
	}

	/** Returns null! */
	public Img<R> sourceImage() {
		return source;
	}

	/** Returns the target {@link Type}. */
	public T targetType() {
		return type;
	}

	/** Returns null! */
	@Override
	public Img<T> image() {
		return null;
	}

	@Override
	public T firstElement() {
		final R r = source.firstElement();
		final T t = type.createVariable();
		convert(r, t);
		return t;
	}


	/** Returns a read-only {@link Iterator} that transforms the type on the fly from the source image. */
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>()
		{
			final Iterator<R> ir = source.iterator();
			final T t = type.createVariable();

			@Override
			public boolean hasNext() {
				return ir.hasNext();
			}

			@Override
			public T next() {
				final R r = ir.next();
				convert(r, t);
				return t;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/** Returns a read-only {@link Cursor} that transforms the type on the fly from the source image. */
	@Override
	public Cursor<T> cursor() {
		return new ConverterCursor();
	}

	/** Returns a read-only {@link Cursor} that transforms the type on the fly from the source image. */
	@Override
	public Cursor<T> localizingCursor() {
		return cursor();
	}

	private final class ConverterCursor extends AbstractCursor<T>
	{
		final Cursor<R> rc = source.cursor();
		final T t = type.createVariable();

		private ConverterCursor() {
			super(source.numDimensions());
		}

		@Override
		public T get() {
			final R r = rc.get();
			convert(r, t);
			return t;
		}

		@Override
		public final void fwd() {
			rc.fwd();
		}

		@Override
		public final void reset() {
			rc.reset();
		}

		@Override
		public final boolean hasNext() {
			return rc.hasNext();
		}

		@Override
		public final void localize(final long[] position) {
			rc.localize(position);
		}

		@Override
		public final long getLongPosition(final int d) {
			return rc.getLongPosition(d);
		}

		@Override
		public final ConverterCursor copy() {
			return new ConverterCursor();
		}

		@Override
		public ConverterCursor copyCursor() {
			return new ConverterCursor();
		}
	}

	@Override
	public ImgFactory<T> factory() {
		try {
			return source.factory().imgFactory(type.createVariable());
		} catch (final IncompatibleTypeException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Returns a read-only {@link RandomAccess} that transforms the type on the fly from the source image. */
	@Override
	public RandomAccess<T> randomAccess() {
		return new ConverterRandomAccess();
	}

	/** Returns a read-only {@link RandomAccess} that transforms the type on the fly from the source image. */
	@Override
	public RandomAccess<T> randomAccess(final Interval interval) {
		return new ConverterRandomAccess(interval);
	}

	private final class ConverterRandomAccess implements RandomAccess<T>
	{
		private final RandomAccess<R> ra;
		private final T t = type.createVariable();
		private final Interval interval;

		public ConverterRandomAccess() {
			this.ra = source.randomAccess();
			this.interval = null;
		}

		public ConverterRandomAccess(final Interval interval) {
			this.ra = source.randomAccess(interval);
			this.interval = interval;
		}

		@Override
		public void localize( final int[] position )
		{
			ra.localize( position );
		}

		@Override
		public void localize( final long[] position )
		{
			ra.localize( position );
		}

		@Override
		public int getIntPosition( final int d )
		{
			return ra.getIntPosition( d );
		}

		@Override
		public long getLongPosition( final int d )
		{
			return ra.getLongPosition( d );
		}

		@Override
		public void localize( final float[] position )
		{
			ra.localize( position );
		}

		@Override
		public void localize( final double[] position )
		{
			ra.localize( position );
		}

		@Override
		public float getFloatPosition( final int d )
		{
			return ra.getFloatPosition( d );
		}

		@Override
		public double getDoublePosition( final int d )
		{
			return ra.getDoublePosition( d );
		}

		@Override
		public int numDimensions()
		{
			return ra.numDimensions();
		}

		@Override
		public void fwd( final int d )
		{
			ra.fwd( d );
		}

		@Override
		public void bck( final int d )
		{
			ra.bck( d );
		}

		@Override
		public void move( final int distance, final int d )
		{
			ra.move( distance, d );
		}

		@Override
		public void move( final long distance, final int d )
		{
			ra.move( distance, d );
		}

		@Override
		public void move( final Localizable localizable )
		{
			ra.move( localizable );
		}

		@Override
		public void move( final int[] distance )
		{
			ra.move( distance );
		}

		@Override
		public void move( final long[] distance )
		{
			ra.move( distance );
		}

		@Override
		public void setPosition( final Localizable localizable )
		{
			ra.setPosition( localizable );
		}

		@Override
		public void setPosition( final int[] position )
		{
			ra.setPosition( position );
		}

		@Override
		public void setPosition( final long[] position )
		{
			ra.setPosition( position );
		}

		@Override
		public void setPosition( final int position, final int d )
		{
			ra.setPosition( position, d );
		}

		@Override
		public void setPosition( final long position, final int d )
		{
			ra.setPosition( position, d );
		}

		@Override
		public final T get() {
			final R r = ra.get();
			convert(r, t);
			return t;
		}

		@Override
		public final ConverterRandomAccess copy() {
			return null == interval ? new ConverterRandomAccess() : new ConverterRandomAccess(interval);
		}

		@Override
		public final ConverterRandomAccess copyRandomAccess() {
			return copy();
		}
	}

	/** Returns a new {@link ConverterImgProxy} for the same source. */
	@Override
	public Img<T> copy() {
		return new ConverterImgProxy<R, T>(source, type.createVariable());
	}
}
