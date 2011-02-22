package script.imglib.color;

import java.awt.Color;
import java.util.Collection;

import script.imglib.color.fn.ColorFunction;
import script.imglib.math.fn.IFunction;

import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.type.numeric.RealType;

/** Given up to 3 channels--each represented by an {@link IFunction},
 *  this class composes them into an {@link RGBALegacyType} {@link Image}
 *  in HSB color space.
 *  
 *  Channel order: 3=H, 2=S, 1=B.
 *  
 *  Expects each channel in floats or doubles in the range [0,1].
 *  */
public final class HSB extends ColorFunction {

	private final IFunction hue, saturation, brightness;

	public HSB(final IFunction hue, final IFunction saturation, final IFunction brightness) {
		this.hue = null == hue ? empty : hue;
		this.saturation = null == saturation ? empty : saturation;
		this.brightness = null == brightness ? empty : brightness;
	}

	/** Interpret the @param img as an HSB image. */
	public HSB(final Img<? extends RealType<?>> img) {
		this(new Channel(img, 3), new Channel(img, 2), new Channel(img, 1));
	}

	/** Accepts only {@link Image}, {@link Number}, {@link IFunction} instances or null as arguments. */
	public HSB(final Object hue, final Object saturation, final Object brightness) throws Exception {
		this(wrap(hue), wrap(saturation), wrap(brightness));
	}

	/** Accepts only {@link Image}, {@link Number}, {@link IFunction} instances or null as arguments. */
	public HSB(final Object hue, final Object saturation) throws Exception {
		this(wrap(hue), wrap(saturation), empty);
	}

	/** Accepts only {@link Image}, {@link Number}, {@link IFunction} instances or null as arguments. */
	public HSB(final Object hue) throws Exception {
		this(wrap(hue), empty, empty);
	}

	/** Creates an HSB with only the given channel filled, from 1 to 4,
	 *  where HSB is really ARGB and thus A=4, R=3, G=2, B=1.
	 *  
	 *  @throws Exception If the channel < 1 or > 4. */
	public HSB(final IFunction fn, final int channel) throws IllegalArgumentException {
		this(3 == channel ? fn : empty, 2 == channel ? fn : empty, 1 == channel ? fn : empty);
		if (channel < 1 || channel > 3) throw new IllegalArgumentException("HSB: channel must be >= 1 and <= 3");
	}

	/** Creates an HSB with only the given channel filled, from 1 to 4,
	 *  where HSB is really ARGB and thus A=4, R=3, G=2, B=1.
	 *  
	 *  @param ob can be an instance of {@link Image}, {@link Number}, {@link IFunction}, or null.
	 *  
	 *  @throws Exception If the channel < 1 or > 4. */
	public HSB(final Object ob, final int channel) throws Exception, IllegalArgumentException {
		this(wrap(ob), channel);
		if (channel < 1 || channel > 4) throw new IllegalArgumentException("RGB: channel must be >= 1 and <= 3");
	}

	@Override
	public final IFunction duplicate() throws Exception {
		return new HSB(hue.duplicate(), saturation.duplicate(), brightness.duplicate());
	}

	/** Returns each HSB value packed in an {@code int} that is casted to {@code double}. */
	@Override
	public final double eval() {
		return Color.HSBtoRGB((float)hue.eval(), (float)saturation.eval(), (float)brightness.eval());
	}

	@Override
	public final void findCursors(final Collection<ImgCursor<?>> cursors) {
		hue.findCursors(cursors);
		saturation.findCursors(cursors);
		brightness.findCursors(cursors);
	}
}