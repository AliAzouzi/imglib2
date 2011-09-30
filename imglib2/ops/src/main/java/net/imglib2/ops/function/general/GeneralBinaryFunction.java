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

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Function;
import net.imglib2.ops.Neighborhood;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class GeneralBinaryFunction<INDEX, INPUT1_TYPE, INPUT2_TYPE, OUTPUT_TYPE>
	implements Function<INDEX,OUTPUT_TYPE>
{
	private final Function<INDEX,INPUT1_TYPE> f1;
	private final Function<INDEX,INPUT2_TYPE> f2;
	private final INPUT1_TYPE input1;
	private final INPUT2_TYPE input2;
	private final BinaryOperation<INPUT1_TYPE,INPUT2_TYPE,OUTPUT_TYPE> operation;
	
	public GeneralBinaryFunction(
			Function<INDEX,INPUT1_TYPE> f1,
			Function<INDEX,INPUT2_TYPE> f2,
			BinaryOperation<INPUT1_TYPE,INPUT2_TYPE,OUTPUT_TYPE> operation)
	{
		this.f1 = f1;
		this.f2 = f2;
		this.input1 = f1.createOutput();
		this.input2 = f2.createOutput();
		this.operation = operation;
	}
	
	@Override
	public void evaluate(
			Neighborhood<INDEX> region, INDEX point, OUTPUT_TYPE output)
	{
		f1.evaluate(region, point, input1);
		f2.evaluate(region, point, input2);
		operation.compute(input1, input2, output);
	}
	
	@Override
	public OUTPUT_TYPE createOutput() {
		return operation.createOutput();
	}

	@Override
	public GeneralBinaryFunction<INDEX, INPUT1_TYPE, INPUT2_TYPE, OUTPUT_TYPE> duplicate() {
		return new GeneralBinaryFunction<INDEX, INPUT1_TYPE, INPUT2_TYPE, OUTPUT_TYPE>(
				f1.duplicate(), f2.duplicate(), operation.duplicate());
	}
}
