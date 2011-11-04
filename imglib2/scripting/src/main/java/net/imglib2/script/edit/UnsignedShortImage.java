package net.imglib2.script.edit;

import java.util.List;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.script.algorithm.fn.AlgorithmUtil;
import net.imglib2.type.numeric.integer.UnsignedShortType;

/** Create a new n-dimensional image based on an array of short. */
public class UnsignedShortImage extends ArrayImg<UnsignedShortType, ShortArray>
{
	public UnsignedShortImage(final List<Number> dim) {
		this(AlgorithmUtil.asLongArray(dim));
	}
	
	public UnsignedShortImage(final long[] dim) {
		super(new ShortArray(new short[AlgorithmUtil.size(dim)]), dim, 1);
		setLinkedType(new UnsignedShortType(this));
	}
}
