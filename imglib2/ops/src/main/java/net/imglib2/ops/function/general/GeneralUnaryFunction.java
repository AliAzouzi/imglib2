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

package net.imglib2.ops.function.general;

import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.type.numeric.RealType;

/**
 * 
 * @author Barry DeZonia
 * 
 */
public class GeneralUnaryFunction<INDEX, INPUT_TYPE extends RealType<INPUT_TYPE>,
		OUTPUT_TYPE extends RealType<OUTPUT_TYPE>>
	implements Function<INDEX, OUTPUT_TYPE>
{
	private final Function<INDEX, INPUT_TYPE> f1;
	private final INPUT_TYPE temp;
	private final UnaryOperation<RealType<?>, RealType<?>> operation;
	private final OUTPUT_TYPE type;

	public GeneralUnaryFunction(Function<INDEX, INPUT_TYPE> f1,
			UnaryOperation<RealType<?>, RealType<?>> operation, OUTPUT_TYPE type) {
		this.type = type;
		this.f1 = f1;
		this.temp = f1.createOutput();
		this.operation = operation;
	}

	@Override
	public void evaluate(Neighborhood<INDEX> region, INDEX point,
			OUTPUT_TYPE output) {
		f1.evaluate(region, point, temp);
		operation.compute(temp, output);
	}

	@Override
	public GeneralUnaryFunction<INDEX, INPUT_TYPE, OUTPUT_TYPE> copy() {
		return new GeneralUnaryFunction<INDEX, INPUT_TYPE, OUTPUT_TYPE>(
			f1.copy(), operation.copy(), type);
	}

	@Override
	public OUTPUT_TYPE createOutput() {
		return type.createVariable();
	}
}
