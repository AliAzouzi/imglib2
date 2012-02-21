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

package net.imglib2.ops.function.real;

import net.imglib2.ExtendedRandomAccessibleInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.img.Img;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class RealImageFunction<T extends RealType<T>> implements Function<long[],T>
{
	// -- instance variables --
	
	private final RandomAccess<? extends RealType<?>> accessor;
	private final T type;
	
	// -- private constructor used by duplicate() --
	
	private RealImageFunction(RandomAccess<? extends RealType<?>> acc, T type)
	{
		this.accessor = acc;
		this.type = type;
	}
	
	// -- public constructors --
	
	public RealImageFunction(Img<? extends RealType<?>> img, T type) {
		this.accessor = img.randomAccess();
		this.type = type;
	}
	
	public <K extends RealType<K>> RealImageFunction(
		Img<K> img,
		OutOfBoundsFactory<K,Img<K>> factory,
		T type)
	{
		@SuppressWarnings({"rawtypes","unchecked"})
		RandomAccessible<T> extendedRandAcessible =
				new ExtendedRandomAccessibleInterval(img, factory);
		this.accessor =  extendedRandAcessible.randomAccess();
		this.type = type;
	}
	
	// -- public interface --
	
	@Override
	public void evaluate(Neighborhood<long[]> input, long[] point, T output)
	{
		accessor.setPosition(point);
		double r = accessor.get().getRealDouble();
		output.setReal(r);
		//output.setImaginary(0);
	}

	@Override
	public RealImageFunction<T> copy() {
		return new RealImageFunction<T>(accessor.copyRandomAccess(), type.copy());
	}

	@Override
	public T createOutput() {
		return type.createVariable();
	}
}
