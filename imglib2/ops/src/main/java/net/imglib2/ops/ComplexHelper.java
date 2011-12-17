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

package net.imglib2.ops;

import net.imglib2.type.numeric.ComplexType;

/**
 * Static utility class for implementing methods common to complex types.
 * These should make their way into Imglib classes when possible.
 * 
 * @author Barry DeZonia
 *
 */
public class ComplexHelper {
	
	private ComplexHelper() {
		// utility class - do not instantiate
	}
	
	/**
	 * Gets the modulus (magnitude, radius, r, etc.) of a given complex number
	 */
	public static double getModulus(ComplexType<?> z) {
		return Math.sqrt(getModulus2(z));
	}

	/**
	 * Gets the square of the modulus (magnitude, radius, r, etc.) of a given
	 * complex number
	 */
	public static double getModulus2(ComplexType<?> z) {
		return
			z.getRealDouble()*z.getRealDouble() +
			z.getImaginaryDouble()*z.getImaginaryDouble();
	}
	
	/**
	 * Gets the argument (angle, theta, etc.) of a given complex number
	 */
	public static double getArgument(ComplexType<?> z) {
		double x = z.getRealDouble();
		double y = z.getImaginaryDouble();
		double theta;
		if (x == 0) {
			if (y > 0)
				theta = Math.PI / 2;  // looks fine
			else if (y < 0)
				theta = -Math.PI / 2;  // looks fine
			else // y == 0 : theta indeterminate
				theta = 0;  // sensible default
		}
		else if (y == 0) {
			if (x > 0)
				theta = 0;  // looks fine
			else // (x < 0)
				theta = Math.PI;  // looks fine
		}
		else // x && y both != 0
			theta = Math.atan2(y,x);
		
		return theta;
	}

	/**
	 * Normalizes an angle in radians to -Math.PI < angle <= Math.PI
	 */
	public static double getPrincipleArgument(double angle) {
		double arg = angle;
		while (arg <= -Math.PI) arg += 2*Math.PI;
		while (arg > Math.PI) arg -= 2*Math.PI;
		return arg;
	}

	/**
	 * Sets the value of a complex number to a given (r,theta) combination
	 */
	public static void setPolar(ComplexType<?> z, double r, double theta) {
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		z.setComplexNumber(x, y);
	}
}
