package mikenakis.tyraki;

/**
 * An object which may be frozen.
 *
 * @author michael.gr
 */
public interface Freezile
{
	boolean isFrozen();

	interface Defaults extends Freezile
	{
	}

	interface Decorator extends Defaults
	{
		Freezile getDecoratedFreezile();

		@Override default boolean isFrozen()
		{
			Freezile decoree = getDecoratedFreezile();
			return decoree.isFrozen();
		}
	}
}
