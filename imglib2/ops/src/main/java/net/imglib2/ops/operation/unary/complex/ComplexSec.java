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

package net.imglib2.ops.operation.unary.complex;

import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.Complex;
import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.ops.sandbox.ComplexOutput;
import net.imglib2.type.numeric.ComplexType;

//Formula taken from MATLAB documentation

/**
 * 
 * @author Barry DeZonia
 *
 */
public final class ComplexSec<T extends ComplexType<T>>
	implements UnaryOperation<T,T> {

	private static final Complex ONE = Complex.createCartesian(1, 0);

	private static final ComplexCos cosFunc = new ComplexCos();
	private static final ComplexDivide divFunc = new ComplexDivide();
	
	private final Complex cos = new Complex();
	
	// TODO - is it the same but quicker to calculate reciprocal(cos(z))?
	//   Later - it is the same but tests showed it very slightly slower
	
	@Override
	public void compute(Complex z, Complex output) {
		cosFunc.compute(z, cos);
		divFunc.compute(ONE, cos, output);
	}
	
	@Override
	public ComplexSec copy() {
		return new ComplexSec();
	}

	@Override
	public T createOutput(T dataHint) {
		return dataHint.createVariable();
	}
}
