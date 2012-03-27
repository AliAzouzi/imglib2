/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.display;

import net.imglib2.type.numeric.ARGBType;

/**
 * Abstract superclass for color lookup tables.
 * 
 * @author Stephan Saalfeld
 * @author Curtis Rueden
 */
public abstract class ColorTable<T> {

	/** Actual color table values. */
	protected final T[] values;

	/** Initializes a color table with the given table values. */
	public ColorTable(final T... values) {
		this.values = values;
	}

	/** Gets a copy of the entire color table. */
	public T[] getValues() {
		return values.clone();
	}

	/** Converts the tuple at the given position into a packed ARGB value. */
	public int argb(final int i) {
		final int r = values.length > 0 ? get(0, i) : 0;
		final int g = values.length > 1 ? get(1, i) : 0;
		final int b = values.length > 2 ? get(2, i) : 0;
		final int a = values.length > 3 ? get(3, i) : 0xff;
		return ARGBType.rgba(r, g, b, a);
	}

	/**
	 * Gets the number of color components in the table (typically 3 for RGB or 4
	 * for RGBA).
	 */
	public int getComponentCount() {
		return values.length;
	}

	/** Gets the number of elements for each color component in the table. */
	public abstract int getLength();

	/**
	 * Gets an individual value from the color table.
	 * 
	 * @param c The color component to query.
	 * @param i The index into the color table.
	 * @return The value of the table at the specified position.
	 */
	public abstract int get(final int c, final int i);

}
