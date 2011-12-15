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

import net.imglib2.ops.ComplexHelper;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.ComplexType;

//Handbook of Mathematics and Computational Science, Harris & Stocker, Springer, 2006

/**
 * 
 * @author Barry DeZonia
 *
 */
public final class ComplexIntegerPower<T extends ComplexType<T>,U extends ComplexType<U>>
	implements UnaryOperation<T,U>
{
	private final int power;
	
	private final U type;
	
	public ComplexIntegerPower(int power, U type) {
		this.type = type;
		this.power = power;
	}

	@Override
	public void compute(T z, U output) {
		// NB: valid for ALL integral powers: 0, +/-1, +/-2, +/-3, ...
		double modulus = ComplexHelper.getModulus(z);
		double argument = ComplexHelper.getArgument(z);
		double r = Math.pow(modulus, power);
		double theta = ComplexHelper.getPrincipleArgument(power * argument);
		ComplexHelper.setPolar(output, r, theta);
	}
	
	@Override
	public ComplexIntegerPower<T,U> copy() {
		return new ComplexIntegerPower<T,U>(power,type);
	}

	@Override
	public U createOutput(T dataHint) {
		return type.createVariable();
	}
}
