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

package net.imglib2.ops.operation.binary.complex;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.type.numeric.ComplexType;

// TODO - unverified formula

/**
 * 
 * @author Barry DeZonia
 *
 */
public final class ComplexAvg<T extends ComplexType<T>, U extends ComplexType<U>,
	V extends ComplexType<V>> implements BinaryOperation<T,U,V> {

	private final V type;
	
	public ComplexAvg(V type) {
		this.type = type;
	}
	
	@Override
	public void compute(T z1, U z2, V output) {
		double x = (z1.getRealDouble() + z2.getRealDouble()) / 2;
		double y = (z1.getImaginaryDouble() + z2.getImaginaryDouble()) / 2;
		output.setComplexNumber(x,y);
	}

	@Override
	public ComplexAvg<T,U,V> copy() {
		return new ComplexAvg<T,U,V>(type);
	}

	@Override
	public V createOutput(T dataHint1, U dataHint2) {
		return type.createVariable();
	}
}
