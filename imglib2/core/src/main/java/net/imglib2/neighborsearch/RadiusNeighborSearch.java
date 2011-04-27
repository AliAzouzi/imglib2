/**
 * Copyright (c) 2011, Tobias Pietzsch
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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

package net.imglib2.neighborsearch;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;

/**
 * Radius neighbor search in an Euclidean space. The interface describes
 * implementations that perform the search for a specified reference location
 * and radius, and provide access to the data, location, and distance of the
 * found neighbors until the next search is performed. For every search, the
 * user can choose whether the found neighbors are returned in arbitrary order
 * or sorted by distance to the reference location.
 * 
 * In a multi-threaded application, each thread will need its own
 * {@link RadiusNeighborSearch}.
 * 
 * @author Tobias Pietzsch
 */
public interface RadiusNeighborSearch< T >
{
	/**
	 * Perform neighbor search within a radius about a reference coordinate. A
	 * point is considered within radius if its distance to the reference is
	 * smaller or equal the radius.
	 * 
	 * @param reference
	 *            the reference coordinate.
	 * @param radius
	 *            the radius about the reference coordinate that should be
	 *            searched for neighbors.
	 * @param sortResults
	 *            whether the results should be ordered by ascending distances
	 *            to reference.
	 */
	public void search( final RealLocalizable reference, final double radius, final boolean sortResults );

	/**
	 * Get the number of points found within radius after a
	 * {@link #search(RealLocalizable, double, boolean)}.
	 * 
	 * @return the number of points found within radius after a
	 *         {@link #search(RealLocalizable, double, boolean)}.
	 */
	public int numNeighbors();

	/**
	 * Access the data of the <em>i</em><sup>th</sup> neighbor within radius. If
	 * {@code sortResults} was set to true, neighbors are ordered by square
	 * Euclidean distance to the reference. Data is accessed through a
	 * {@link Sampler} that guarantees write access if the underlying data set
	 * is writable.
	 * 
	 * @param i
	 * @return
	 */
	public Sampler< T > getSampler( final int i );

	/**
	 * Access the position of the <em>i</em><sup>th</sup> neighbor within
	 * radius. If {@code sortResults} was set to true, neighbors are ordered by
	 * square Euclidean distance to the reference.
	 * 
	 * @param i
	 * @return
	 */
	public RealLocalizable getPosition( final int i );

	/**
	 * Access the square Euclidean distance between the reference location as
	 * used for the last search and the <em>i</em><sup>th</sup> neighbor. If
	 * {@code sortResults} was set to true, neighbors are ordered by square
	 * Euclidean distance to the reference.
	 * 
	 * @param i
	 * @return
	 */
	public double getSquareDistance( final int i );

	/**
	 * Access the Euclidean distance between the reference location as used for
	 * the last search and the <em>i</em><sup>th</sup> neighbor.
	 * 
	 * @param i
	 * @return
	 */
	public double getDistance( final int i );
}
