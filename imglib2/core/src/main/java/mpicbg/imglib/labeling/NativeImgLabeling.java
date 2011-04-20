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

import mpicbg.imglib.Cursor;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.NativeImg;
import mpicbg.imglib.img.NativeImgFactory;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.img.basictypeaccess.IntAccess;


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
	
	/**
	 * Create a labeling backed by the default image storage factory
	 * 
	 * @param dim dimensions of the image
	 */
	public NativeImgLabeling(long [] dim) {
		this(dim, new ArrayImgFactory<LabelingType<T>>());
	}
	/**
	 * Create a labeling backed by an image from a custom factory
	 * 
	 * @param dim dimensions of the image
	 * @param imgFactory the custom factory to be used to create the backing storage 
	 */
	public NativeImgLabeling(long[] dim, NativeImgFactory<LabelingType<T>> imgFactory) {
		this(dim, new DefaultROIStrategyFactory<T>(), imgFactory);
	}

	/**
	 * Create a labeling backed by a native image with custom strategy and image factory
	 * 
	 * @param dim - dimensions of the labeling
	 * @param strategyFactory - the strategy factory that drives iteration and statistics
	 * @param imgFactory - the image factory to generate the native image
	 */
	public NativeImgLabeling(
			long[] dim, 
			LabelingROIStrategyFactory<T> strategyFactory, 
			NativeImgFactory<LabelingType<T>> imgFactory) {
		super(dim, strategyFactory);
		this.img = imgFactory.createIntInstance(dim, 1); 
	}
	
	@Override
	public RandomAccess<LabelingType<T>> randomAccess() {
		return img.randomAccess();
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
	public Cursor<LabelingType<T>> cursor() {
		return img.cursor();
	}

	@Override
	public Cursor<LabelingType<T>> localizingCursor() {
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
