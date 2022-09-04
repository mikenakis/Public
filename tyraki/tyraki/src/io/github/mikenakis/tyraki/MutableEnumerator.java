package io.github.mikenakis.tyraki;

import io.github.mikenakis.coherence.Coherent;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.conversion.ConversionCollections;

import java.util.function.Predicate;

/**
 * Mutable Enumerator.
 *
 * @author michael.gr
 */
public interface MutableEnumerator<T> extends UnmodifiableEnumerator<T>
{
	/**
	 * Down-casts to an ancestral type.
	 *
	 * @param enumerator the {@link MutableEnumerator} to down-cast.
	 * @param <T>        the type of the ancestor.
	 * @param <U>        the type of the {@link MutableEnumerator}.
	 *
	 * @return the same {@link MutableEnumerator} cast to an ancestral type.
	 */
	static <T, U extends T> MutableEnumerator<T> downCast( MutableEnumerator<U> enumerator )
	{
		@SuppressWarnings( "unchecked" )
		MutableEnumerator<T> result = (MutableEnumerator<T>)enumerator;
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	void deleteCurrent();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for decorating {@link MutableEnumerator}.
	 *
	 * @author michael.gr
	 */
	interface Decorator<E> extends Defaults<E>, UnmodifiableEnumerator.Decorator<E>
	{
		@Override MutableEnumerator<E> getDecoratedUnmodifiableEnumerator();

		@Override default Coherent decoratedCoherent()
		{
			return getDecoratedUnmodifiableEnumerator();
		}

		@Override default void deleteCurrent()
		{
			MutableEnumerator<E> decoree = getDecoratedUnmodifiableEnumerator();
			decoree.deleteCurrent();
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override <C> MutableEnumerator<C> map( Function1<C,T> converter );

	MutableEnumerator<T> mutableFilter( Predicate<T> predicate );

	@Override <U extends T> MutableEnumerator<U> upCast();

	/**
	 * Default methods for {@link MutableEnumerator}.
	 *
	 * @author michael.gr
	 */
	interface Defaults<T> extends MutableEnumerator<T>, UnmodifiableEnumerator.Defaults<T>
	{
		@Override default <C> MutableEnumerator<C> map( Function1<C,T> converter )
		{
			return ConversionCollections.newConvertingMutableEnumerator( this, converter );
		}

		@Override default MutableEnumerator<T> mutableFilter( Predicate<T> predicate )
		{
			return ConversionCollections.newFilteringMutableEnumerator( this, predicate );
		}

		@Override default <U extends T> MutableEnumerator<U> upCast()
		{
			@SuppressWarnings( "unchecked" )
			MutableEnumerator<U> result = (MutableEnumerator<U>)this;
			return result;
		}
	}
}
