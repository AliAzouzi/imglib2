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


// NOTE - this class would not support a ConditionalFunction as that
// implementation uses neigh info to calc one value or another. This could
// take a Bool to a Real but without neighborhood info. Might want another
// kind of operation/function to generalize a ConditionalFunction.

/**
 * 
 * @author Barry DeZonia
 *
 */
public class ConverterFunction<INDEX,INTERMEDIATE_TYPE,FINAL_TYPE>
	implements Function<INDEX,FINAL_TYPE>
{
	private final Function<INDEX,INTERMEDIATE_TYPE> intermediateFunc;
	private final UnaryOperation<INTERMEDIATE_TYPE,FINAL_TYPE> operation;
	private final INTERMEDIATE_TYPE variable;

	public ConverterFunction(Function<INDEX,INTERMEDIATE_TYPE> func,
		UnaryOperation<INTERMEDIATE_TYPE,FINAL_TYPE> operation)
	{
		this.intermediateFunc = func;
		this.operation = operation;
		this.variable = func.createOutput();
	}

	@Override
	public void evaluate(Neighborhood<INDEX> region, INDEX point, FINAL_TYPE output)
	{
		intermediateFunc.evaluate(region,point,variable);
		operation.compute(variable, output);
	}
	
	@Override
	public FINAL_TYPE createOutput() {
		return operation.createOutput(variable);
	}
	
	@Override
	public ConverterFunction<INDEX,INTERMEDIATE_TYPE,FINAL_TYPE> duplicate() {
		return new ConverterFunction<INDEX, INTERMEDIATE_TYPE, FINAL_TYPE>(intermediateFunc.duplicate(), operation.copy());
	}
}
