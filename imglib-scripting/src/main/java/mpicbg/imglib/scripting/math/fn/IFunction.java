package mpicbg.imglib.scripting.math.fn;

import java.util.Collection;
import mpicbg.imglib.cursor.Cursor;

public interface IFunction {
	/** Evaluate this function and return a result in double floating-point precision. */
	public double eval();
	/** Put any cursors in use by this function (and any nested functions) in @param cursors.*/
	public void findCursors(Collection<Cursor<?>> cursors);
}