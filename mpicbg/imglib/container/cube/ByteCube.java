/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.container.cube;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.ByteContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class ByteCube<T extends Type<T>> extends Cube<ByteCubeElement<T>, ByteCube<T>, T> implements ByteContainer<T>
{
	public ByteCube(ContainerFactory factory, int[] dim, int[] cubeSize, int entitiesPerPixel)
	{
		super(factory, dim, cubeSize, entitiesPerPixel);
	}
	
	@Override
	public ByteCubeElement<T> createCubeElementInstance( final int cubeId, final int[] dim, final int offset[], final int entitiesPerPixel )
	{
		return new ByteCubeElement<T>( this, cubeId, dim, offset, entitiesPerPixel );
	}

	@Override
	public byte[] getCurrentStorageArray(Cursor<?> c) { return data.get( c.getStorageIndex() ).getCurrentStorageArray( c ); }	
}