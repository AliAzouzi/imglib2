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

package net.imglib2.ops.operation.unary.real;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.ComplexType;

/**
 * Sets the real component of an output complex number to the inversion of
 * the real component of an input complex number about a range. The range is
 * specifed in the constructor.
 * 
 * @author Barry DeZonia
 * 
 */
public final class RealInvert
		implements UnaryOperation<ComplexType<?>, ComplexType<?>>
{
	private double actualMin;
	private double actualMax;

	/**
	 * Constructor.
	 * @param actualMin - minimum value of the range to invert about
	 * @param actualMax - maximum value of the range to invert about
	 */
	public RealInvert(final double actualMin, final double actualMax)
	{
		this.actualMax = actualMax;
		this.actualMin = actualMin;
	}

	@Override
	public ComplexType<?> compute(ComplexType<?> x, ComplexType<?> output) {
		double value = actualMax - (x.getRealDouble() - actualMin);
		output.setReal(value);
		return output;
	}
	
	@Override
	public RealInvert copy() {
		return new RealInvert(actualMin, actualMax);
	}
}
