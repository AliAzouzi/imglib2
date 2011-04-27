package net.imglib2.neighborsearch;

import net.imglib2.RealLocalizable;
import net.imglib2.Sampler;
import net.imglib2.collection.KDTree;
import net.imglib2.collection.KDTreeNode;


public class NearestNeighborSearchOnKDTree< T > implements NearestNeighborSearch< T >
{
	protected KDTree< T > tree;
	
	protected final int n;
	protected final double[] pos;

	protected KDTreeNode< T > bestPoint;
	protected double bestSquDistance;
	
	public NearestNeighborSearchOnKDTree( KDTree< T > tree )
	{
		n = tree.numDimensions();
		pos = new double[ n ];
		this.tree = tree;
	}
	
	@Override
	public void search( RealLocalizable p )
	{
		p.localize( pos );
		bestSquDistance = Double.MAX_VALUE;
		searchNode( tree.getRoot() );
	}
	
	protected void searchNode( KDTreeNode< T > current )
	{
		// consider the current node
		final double distance = current.squDistanceTo( pos );
		if ( distance < bestSquDistance )
		{
			bestSquDistance = distance;
			bestPoint = current;
		}
		
		final double axisDiff = pos[ current.getSplitDimension() ] - current.getSplitCoordinate();
		final double axisSquDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		final KDTreeNode< T > nearChild = leftIsNearBranch ? current.left : current.right;
		final KDTreeNode< T > awayChild = leftIsNearBranch ? current.right : current.left;
		if ( nearChild != null )
			searchNode( nearChild );

	    // search the away branch - maybe
		if ( ( axisSquDistance <= bestSquDistance ) && ( awayChild != null ) )
			searchNode( awayChild );
	}

	@Override
	public Sampler< T > getSampler()
	{
		return bestPoint;
	}

	@Override
	public RealLocalizable getPosition()
	{
		return bestPoint;
	}

	@Override
	public double getSquareDistance()
	{
		return bestSquDistance;
	}

	@Override
	public double getDistance()
	{
		return Math.sqrt( bestSquDistance );
	}
}
