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
 * @author Lee Kamentsky
 *
 */

package mpicbg.imglib.labeling;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgCursor;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.ImgRandomAccess;
import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.img.basictypeaccess.IntAccess;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;


/**
 * A labeling backed by a native image that takes a
 * labeling type backed by an int array.
 * 
 * @author leek
 *
 * @param <T> the type of labels assigned to pixels
 */
public class NativeImgLabeling<T extends Comparable<T>> 
	extends AbstractNativeLabeling<T, IntAccess>{

	final NativeImg<LabelingType<T>, ? extends IntAccess> img;
	public NativeImgLabeling(long[] dim, NativeImgFactory<LabelingType<T>> imgFactory) {
		this(dim, new DefaultROIStrategyFactory<T>(), imgFactory);
	}

	public NativeImgLabeling(
			long[] dim, 
			LabelingROIStrategyFactory<T> strategyFactory, 
			NativeImgFactory<LabelingType<T>> imgFactory) {
		super(dim, strategyFactory);
		this.img = imgFactory.createIntInstance(dim, 1); 
	}
	
	@Override
	public ImgRandomAccess<LabelingType<T>> randomAccess() {
		return img.randomAccess();
	}

	@Override
	public ImgRandomAccess<LabelingType<T>> randomAccess(
			OutOfBoundsFactory<LabelingType<T>, Img<LabelingType<T>>> factory) {
		return img.randomAccess(factory);
	}

	/* (non-Javadoc)
	 * @see mpicbg.imglib.labeling.AbstractNativeLabeling#setLinkedType(mpicbg.imglib.labeling.LabelingType)
	 */
	@Override
	public void setLinkedType(LabelingType<T> type) {
		super.setLinkedType(type);
		img.setLinkedType(type);
	}

	@Override
	public ImgCursor<LabelingType<T>> cursor() {
		return img.cursor();
	}

	@Override
	public ImgCursor<LabelingType<T>> localizingCursor() {
		return img.localizingCursor();
	}

	@Override
	public ImgFactory<LabelingType<T>> factory() {
		return img.factory();
	}

	@Override
	public IntAccess update(Object updater) {
		return img.update(updater);
	}
	
}
