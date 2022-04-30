package mikenakis.immutability.internal.helpers;

import java.lang.reflect.Array;
import java.util.Iterator;

public class IterableOnArrayObject implements Iterable<Object>
{
	private final Object array;
	private final int length;

	public IterableOnArrayObject( Object array )
	{
		assert array.getClass().isArray();
		this.array = array;
		length = Array.getLength( array );
	}

	@Override public Iterator<Object> iterator()
	{
		return new Iterator<>()
		{
			private int index = 0;

			@Override public boolean hasNext()
			{
				return index < length;
			}

			@Override public Object next()
			{
				return Array.get( array, index++ );
			}
		};
	}
}
