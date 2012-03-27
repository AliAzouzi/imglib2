package net.imglib2.script.algorithm;

import net.imglib2.img.Img;
import net.imglib2.script.algorithm.fn.GenericRotate;
import net.imglib2.type.numeric.RealType;

public class Rotate180<R extends RealType<R>> extends GenericRotate<R>
{
	public Rotate180(final Img<R> img) {
		super(img, GenericRotate.Mode.R180);
	}
}
