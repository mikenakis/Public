package mikenakis.tyraki;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

/**
 * Freezable {@link MutableList}.
 *
 * @author michael.gr
 */
public interface FreezableList<E> extends MutableList<E>, FreezableCollection<E>
{
	/**
	 * Fixes the list, making it immutable.
	 *
	 * @return the list.  (Fluent.)
	 */
	@Override UnmodifiableList<E> frozen();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override FreezableList<E> add( E element );

	/**
	 * Default methods for {@link FreezableList}.
	 */
	interface Defaults<E> extends FreezableList<E>, Freezable.Defaults
	{
		@Override default FreezableList<E> add( E element )
		{
			int index = size();
			insertAt( index, element );
			return this;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for decorating {@link FreezableList}.
	 */
	interface Decorator<E> extends Defaults<E>, MutableList.Decorator<E>, Freezable.Decorator
	{
		FreezableList<E> getDecoratedFixableMutableList();

		@Override default MutableList<E> getDecoratedMutableList()
		{
			return getDecoratedFixableMutableList();
		}

		@Override default Freezable getDecoratedFreezable()
		{
			return getDecoratedFixableMutableList();
		}

		@Override default Freezile getDecoratedFreezile()
		{
			return getDecoratedMutableList();
		}

		@Override default FreezableList<E> add( E element )
		{
			return FreezableList.Defaults.super.add( element );
		}

		@Override default UnmodifiableList<E> frozen()
		{
			FreezableList<E> decoree = getDecoratedFixableMutableList();
			UnmodifiableList<E> result = decoree.frozen();
			assert result == decoree;
			return this;
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

		@Override public FreezableList<E> getDecoratedFixableMutableList()
		{
			return this;
		}
	}
}
