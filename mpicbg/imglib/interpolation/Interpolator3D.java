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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.interpolation;

import mpicbg.imglib.type.Type;

public interface Interpolator3D<T extends Type<T>> extends Interpolator<T>
{
	/**
	 * Moves the interpolator to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param float x - the float position in x
	 * @param float y - the float position in y
	 * @param float z - the float position in z
	 */
	public void moveTo( float x, float y, float z );

	/**
	 * Moves the interpolator a certain distance given by the vector to a random position inside or outside the image.
	 * This method is typically more efficient than setting the position
	 * 
	 * @param float x - the float vector in x
	 * @param float y - the float vector in y
	 * @param float z - the float vector in z
	 */
	public void moveRel( float x, float y, float z );
	
	/**
	 * Sets the interpolator to a random position inside or outside the image.
	 * This method is typically less efficient than moving the position
	 * 
	 * @param float x - the float position in x
	 * @param float y - the float position in y
	 * @param float z - the float position in z
	 */
	public void setPosition( float x, float y, float z );

	/**
	 * Returns the current x coordinate of the interpolator
	 * 
	 * @return float - x coordinate
	 */
	public float getX();

	/**
	 * Returns the current y coordinate of the interpolator
	 * 
	 * @return float - y coordinate
	 */
	public float getY();

	/**
	 * Returns the current z coordinate of the interpolator
	 * 
	 * @return float - z coordinate
	 */
	public float getZ();
	
}
