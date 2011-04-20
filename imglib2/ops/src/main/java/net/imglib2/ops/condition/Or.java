package net.imglib2.ops.condition;

import net.imglib2.cursor.LocalizableCursor;
import net.imglib2.type.numeric.RealType;

public class Or<T extends RealType<T>> implements Condition<T>
{
	private final Condition<T> condition1, condition2;
	
	public Or(final Condition<T> condition1, final Condition<T> condition2)
	{
		this.condition1 = condition1;
		this.condition2 = condition2;
	}
	
	@Override
	public boolean isSatisfied(final LocalizableCursor<T> cursor, final int[] position)
	{
		return condition1.isSatisfied(cursor, position) || condition2.isSatisfied(cursor, position); 
	}
}
