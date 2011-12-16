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
import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.type.numeric.ComplexType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * 
 * @author Barry DeZonia
 * 
 */
public final class ComplexTan<T extends ComplexType<T>, U extends ComplexType<U>>
		implements UnaryOperation<T, U> {

	private final ComplexCopy<T, U> copyFunc;
	private final ComplexCos<U, U> cosFunc;
	private final ComplexSin<U, U> sinFunc;
	private final ComplexDivide<U, U, U> divFunc;

	private final U z;
	private final U sin;
	private final U cos;

	private final U type;

	public ComplexTan(U type) {
		this.type = type;

		copyFunc = new ComplexCopy<T, U>();
		cosFunc = new ComplexCos<U, U>(type);
		sinFunc = new ComplexSin<U, U>(type);
		divFunc = new ComplexDivide<U, U, U>();

		z = type.createVariable();
		sin = type.createVariable();
		cos = type.createVariable();
	}

	@Override
	public U compute(T in, U output) {
		copyFunc.compute(in, z);
		sinFunc.compute(z, sin);
		cosFunc.compute(z, cos);
		divFunc.compute(sin, cos, output);
		return output;
	}

	@Override
	public ComplexTan<T, U> copy() {
		return new ComplexTan<T, U>(type);
	}

}
