package mikenakis.tyraki;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.conversion.ConversionCollections;

import java.util.function.Predicate;

/**
 * Unmodifiable Enumerator.
 *
 * @author michael.gr
 */
public interface UnmodifiableEnumerator<E>
{
	static <T, U extends T> UnmodifiableEnumerator<T> downCast( UnmodifiableEnumerator<U> enumerator )
	{
		@SuppressWarnings( "unchecked" )
		UnmodifiableEnumerator<T> result = (UnmodifiableEnumerator<T>)enumerator;
		return result;
	}

	UnmodifiableEnumerator<Object> EMPTY = new Defaults<>()
	{
		@Override public boolean isFinished()
		{
			return true;
		}

		@Override public Object current()
		{
			assert false;
			return null;
		}

		@Override public UnmodifiableEnumerator<Object> moveNext()
		{
			assert false;
			return null;
		}

		@ExcludeFromJacocoGeneratedReport @Override public String toString()
		{
			return "empty";
		}
	};

	/**
	 * Gets the empty {@link UnmodifiableEnumerator}.
	 *
	 * @param <E> the type of the items of the {@link UnmodifiableEnumerator}.
	 *
	 * @return the empty {@link UnmodifiableEnumerator}.
	 */
	static <E> UnmodifiableEnumerator<E> of()
	{
		return EMPTY.upCast();
	}

	/**
	 * Checks whether this enumerator is finished enumerating.
	 *
	 * @return {@code true} if the enumerator is finished enumerating; {@code false} otherwise.
	 */
	boolean isFinished();

	/**
	 * Gets the current element of this enumerator.
	 *
	 * @return the current element.
	 */
	E current(); //FIXME FIXME TODO perhaps ModifiableEnumerator should have a setCurrent() function?

	/**
	 * Moves to the next element.
	 */
	UnmodifiableEnumerator<E> moveNext();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Fetches the current element and moves to the next.
	 */
	E fetchCurrentAndMoveNext();

	/**
	 * Gets a new {@link UnmodifiableEnumerator} representing the elements of this {@link UnmodifiableEnumerator} converted by a given {@link Function1}.
	 *
	 * @param converter the {@link Function1} invoked to convert each element.
	 *
	 * @return a new {@link UnmodifiableEnumerator} representing the elements of this {@link UnmodifiableEnumerator} converted by the given {@link Function1}.
	 */
	<T> UnmodifiableEnumerator<T> map( Function1<T,E> converter );

	<U extends E> UnmodifiableEnumerator<U> upCast();

	UnmodifiableEnumerator<E> filter( Predicate<? super E> predicate );

	/**
	 * Default Methods for {@link UnmodifiableEnumerator}.
	 *
	 * @author michael.gr
	 */
	interface Defaults<E> extends UnmodifiableEnumerator<E>
	{
		@Override default E fetchCurrentAndMoveNext()
		{
			E element = current();
			moveNext();
			return element;
		}

		@Override default <T> UnmodifiableEnumerator<T> map( Function1<T,E> converter )
		{
			return ConversionCollections.newConvertingEnumerator( this, ( i, e ) -> converter.invoke( e ) );
		}

		default void unmodifiableEnumeratorToStringBuilder( StringBuilder stringBuilder )
		{
			if( isFinished() )
				stringBuilder.append( "finished" );
			else
				stringBuilder.append( "current: " ).append( current() );
		}

		@Override default <U extends E> UnmodifiableEnumerator<U> upCast()
		{
			@SuppressWarnings( "unchecked" )
			UnmodifiableEnumerator<U> result = (UnmodifiableEnumerator<U>)this;
			return result;
		}

		@Override default UnmodifiableEnumerator<E> filter( Predicate<? super E> predicate )
		{
			return ConversionCollections.newFilteringEnumerator( this, predicate );
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Decorator of {@link UnmodifiableEnumerator}.
	 *
	 * @author michael.gr
	 */
	interface Decorator<E> extends Defaults<E>
	{
		UnmodifiableEnumerator<E> getDecoratedUnmodifiableEnumerator();

		@Override default boolean isFinished()
		{
			UnmodifiableEnumerator<E> decoree = getDecoratedUnmodifiableEnumerator();
			return decoree.isFinished();
		}

		@Override default E current()
		{
			UnmodifiableEnumerator<E> decoree = getDecoratedUnmodifiableEnumerator();
			return decoree.current();
		}

		@Override default UnmodifiableEnumerator<E> moveNext()
		{
			UnmodifiableEnumerator<E> decoree = getDecoratedUnmodifiableEnumerator();
			decoree.moveNext();
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
		@Override public UnmodifiableEnumerator<E> getDecoratedUnmodifiableEnumerator()
		{
			throw new AssertionError();
		}
	}
}
