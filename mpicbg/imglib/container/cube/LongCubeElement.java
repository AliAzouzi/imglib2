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

import mpicbg.imglib.container.basictypecontainer.LongContainer;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.type.Type;

public class LongCubeElement<T extends Type<T>> extends CubeElement<LongCubeElement<T>, LongCube<T>, T> implements LongContainer<T>
{
	protected long[] data;
	
	public LongCubeElement(LongCube<T> parent, int cubeId, int[] dim, int[] offset, int entitiesPerPixel)
	{
		super( parent, cubeId, dim, offset, entitiesPerPixel );
		data = new long[ numEntities ];
	}

	@Override
	public void close(){ data = null; }

	@Override
	public long[] getCurrentStorageArray(Cursor<?> c) { return data; }
}
