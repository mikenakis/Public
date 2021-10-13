package mikenakis.tyraki;

/**
 * An object which can be "frozen" and therefore immutable.
 *
 * @author michael.gr
 */
public interface Freezable extends Freezile, AutoCloseable
{
	void freeze();

	interface Defaults extends Freezable, Freezile.Defaults
	{
		@Deprecated @Override default void close()
		{
			freeze();
		}
	}

	interface Decorator extends Defaults, Freezile.Decorator
	{
		Freezable getDecoratedFreezable();

		@Override default Freezile getDecoratedFreezile()
		{
			return getDecoratedFreezable();
		}

		@Override default void freeze()
		{
			Freezable decoree = getDecoratedFreezable();
			decoree.freeze();
		}
	}
}
