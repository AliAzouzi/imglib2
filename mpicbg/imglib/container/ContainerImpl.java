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
package mpicbg.imglib.container;

import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.type.Type;

public abstract class ContainerImpl<T extends Type<T>> implements Container<T>
{
	final static AtomicInteger idGen = new AtomicInteger(0);
	final protected int numPixels, numDimensions, entitiesPerPixel, numEntities, id;
	protected final int[] dim;
	
	final ContainerFactory factory;

	public ContainerImpl( final ContainerFactory factory, int[] dim, final int entitiesPerPixel )
	{
		this.numDimensions = dim.length;
		
		int numPixels = 1;		
		for (int i = 0; i < numDimensions; i++)
			numPixels *= dim[i];
		this.numPixels = numPixels;
		this.entitiesPerPixel = entitiesPerPixel;
		this.numEntities = numPixels * entitiesPerPixel;

		this.dim = dim.clone();
		this.factory = factory;
		this.id = idGen.getAndIncrement();
	}
		
	@Override
	public ContainerFactory getFactory() { return factory; }
	
	@Override
	public int getId(){ return id; }
	@Override
	public int getNumEntities() { return numEntities; }
	@Override
	public int getNumDimensions() { return dim.length; }
	@Override
	public int getNumEntitiesPerPixel(){ return entitiesPerPixel; }
	@Override
	public int[] getDimensions() { return dim.clone(); }
	
	@Override
	public void getDimensions( final int[] dimensions )
	{
		for (int i = 0; i < numDimensions; i++)
			dimensions[i] = this.dim[i];
	}

	@Override
	public int getDimension( final int dim )
	{
		if ( dim < numDimensions && dim > -1 )
			return this.dim[ dim ];
		else
			return 1;		
	}
	
	@Override
	public int getNumPixels() { return numPixels; }

	@Override
	public String toString()
	{
		String className = this.getClass().getName();
		className = className.substring( className.lastIndexOf(".") + 1, className.length());
		
		String description = className + ", id '" + getId() + "' [" + dim[ 0 ];
		
		for ( int i = 1; i < numDimensions; i++ )
			description += "x" + dim[ i ];
		
		description += "], " + entitiesPerPixel + " entities per pixel.";
		
		return description;
	}
	
	@Override
	public boolean compareStorageContainerDimensions( final Container<?> container )
	{
		if ( container.getNumDimensions() != this.getNumDimensions() )
			return false;
		
		for ( int i = 0; i < numDimensions; i++ )
			if ( this.dim[i] != container.getDimensions()[i])
				return false;
		
		return true;
	}		

	@Override
	public boolean compareStorageContainerCompatibility( final Container<?> container )
	{
		if ( compareStorageContainerDimensions( container ))
		{			
			if ( getFactory().getClass().isInstance( container.getFactory() ))
				return true;
			else
				return false;
		}
		else
		{
			return false;
		}
	}		
	
}
