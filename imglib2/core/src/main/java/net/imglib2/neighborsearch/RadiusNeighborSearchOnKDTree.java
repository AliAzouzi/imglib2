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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.collection.KDTree;
import net.imglib2.collection.KDTreeNode;
import net.imglib2.util.Pair;

/**
 * Implementation of {@link RadiusNeighborSearch} search for kd-trees.
 * 
 * @author Tobias Pietzsch
 */
public class RadiusNeighborSearchOnKDTree< T > implements RadiusNeighborSearch< T >
{
	protected KDTree< T > tree;

	protected final int n;

	protected final double[] pos;

	protected ArrayList< Pair< KDTreeNode< T >, Double > > resultPoints;

	public RadiusNeighborSearchOnKDTree( KDTree< T > tree )
	{
		this.tree = tree;
		this.n = tree.numDimensions();
		this.pos = new double[ n ];
		this.resultPoints = new ArrayList< Pair< KDTreeNode< T >, Double > >();
	}

	@Override
	public void search( final RealLocalizable reference, final double radius, final boolean sortResults )
	{
		assert radius >= 0;
		reference.localize( pos );
		resultPoints.clear();
		searchNode( tree.getRoot(), radius * radius );
		if ( sortResults )
		{
			Collections.sort( resultPoints, new Comparator< Pair< KDTreeNode< T >, Double > >()
			{
				@Override
				public int compare( Pair< KDTreeNode< T >, Double > o1, Pair< KDTreeNode< T >, Double > o2 )
				{
					return Double.compare( o1.b, o2.b );
				}
			} );
		}
	}

	protected void searchNode( KDTreeNode< T > current, final double squRadius )
	{
		// consider the current node
		final double squDistance = current.squDistanceTo( pos );
		if ( squDistance <= squRadius )
		{
			resultPoints.add( new Pair< KDTreeNode< T >, Double >( current, squDistance ) );
		}

		final double axisDiff = pos[ current.getSplitDimension() ] - current.getSplitCoordinate();
		final double axisSquDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		final KDTreeNode< T > nearChild = leftIsNearBranch ? current.left : current.right;
		final KDTreeNode< T > awayChild = leftIsNearBranch ? current.right : current.left;
		if ( nearChild != null )
			searchNode( nearChild, squRadius );

		// search the away branch - maybe
		if ( ( axisSquDistance <= squRadius ) && ( awayChild != null ) )
			searchNode( awayChild, squRadius );
	}

	@Override
	public int numNeighbors()
	{
		return resultPoints.size();
	}

	@Override
	public Sampler< T > getSampler( int i )
	{
		return resultPoints.get( i ).a;
	}

	@Override
	public RealLocalizable getPosition( int i )
	{
		return resultPoints.get( i ).a;
	}

	@Override
	public double getSquareDistance( int i )
	{
		return resultPoints.get( i ).b;
	}

	@Override
	public double getDistance( int i )
	{
		return Math.sqrt( resultPoints.get( i ).b );
	}
}
