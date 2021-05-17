package mikenakis.bytecode.kit;

import java.util.function.Supplier;

/**
 * A lazy initializer.
 * <p>
 * see https://github.com/apache/commons-lang/blob/master/src/main/java/org/apache/commons/lang3/concurrent/LazyInitializer.java
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class LazyInitializer<T>
{
	private final Supplier<T> supplier;
	private final Object lock = new Object();
	private volatile T payload = null;

	public LazyInitializer( Supplier<T> supplier )
	{
		assert supplier != null;
		this.supplier = supplier;
	}

	public T get()
	{
		T result = payload;
		if( result == null )
		{
			synchronized( lock )
			{
				result = payload;
				if( result == null )
				{
					result = supplier.get();
					assert result != null;
					payload = result;
				}
			}
		}
		return result;
	}
}
