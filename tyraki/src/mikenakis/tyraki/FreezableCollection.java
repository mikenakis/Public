package mikenakis.tyraki;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Freezable {@link MutableCollection}.
 *
 * @author michael.gr
 */
public interface FreezableCollection<E> extends MutableCollection<E>, Freezable
{
	UnmodifiableCollection<E> frozen();

	interface Defaults<E> extends FreezableCollection<E>, MutableCollection.Defaults<E>, Freezable.Defaults
	{
	}

	/**
	 * Default methods for decorating {@link FreezableCollection}.
	 */
	interface Decorator<E> extends Defaults<E>, MutableCollection.Decorator<E>, Freezable.Decorator
	{
		FreezableCollection<E> getDecoratedFixableMutableCollection();

		@Override default Freezile getDecoratedFreezile()
		{
			return getDecoratedFixableMutableCollection();
		}

		@Override default MutableCollection<E> getDecoratedMutableCollection()
		{
			return getDecoratedFixableMutableCollection();
		}

		@Override default Freezable getDecoratedFreezable()
		{
			return getDecoratedFixableMutableCollection();
		}

		@Override default UnmodifiableCollection<E> frozen()
		{
			FreezableCollection<E> decoree = getDecoratedFixableMutableCollection();
			return decoree.frozen();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Canary class.
	 *
	 * This is a concrete class to make sure that if there are problems with the interface making it impossible to inherit from, they will be caught by the compiler at the
	 * earliest point possible, and not when compiling some derived class.
	 */
	@ExcludeFromJacocoGeneratedReport @SuppressWarnings( "unused" )
	final class Canary<E> implements Decorator<E>
	{
		@Override public boolean canWriteAssertion()
		{
			return true;
		}

		@Override public FreezableCollection<E> getDecoratedFixableMutableCollection()
		{
			return null;
		}
	}
}
