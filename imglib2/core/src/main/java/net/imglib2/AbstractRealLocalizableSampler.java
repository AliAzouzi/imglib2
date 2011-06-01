/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package net.imglib2;

/**
 * @author Lee Kamentsky
 *An abstract class that implements the RealLocalizable interface using
 *an array of doubles to maintain position
 *
 * @param <T> the type of the sampler
 */
public abstract class AbstractRealLocalizableSampler<T> extends AbstractSampler<T> implements
		RealLocalizable {

	protected final double [] position;
	protected AbstractRealLocalizableSampler(int nDimensions) {
		super(nDimensions);
		position = new double [nDimensions];
	}
	@Override
	public void localize(float[] position) {
		for (int i=0; i<numDimensions(); i++) {
			this.position[i] = position[i];
		}
	}

	@Override
	public void localize(double[] position) {
		System.arraycopy(position, 0, this.position, 0, numDimensions());
	}

	@Override
	public float getFloatPosition(int d) {
		return (float)position[d];
	}

	@Override
	public double getDoublePosition(int d) {
		return position[d];
	}

}
