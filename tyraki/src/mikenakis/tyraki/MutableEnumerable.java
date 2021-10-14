package mikenakis.tyraki;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.conversion.ConversionCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Mutable Enumerable.
 *
 * @author michael.gr
 */
public interface MutableEnumerable<E> extends UnmodifiableEnumerable<E>
{
	/**
	 * Down-casts to an ancestral type.
	 *
	 * @param enumerable the {@link MutableEnumerable} to down-cast.
	 * @param <T>        the type of the ancestor.
	 * @param <U>        the type of the {@link MutableEnumerable}.
	 *
	 * @return the same {@link MutableEnumerable} cast to an ancestral type.
	 */
	static <T, U extends T> MutableEnumerable<T> downCast( MutableEnumerable<U> enumerable )
	{
		@SuppressWarnings( "unchecked" )
		MutableEnumerable<T> result = (MutableEnumerable<T>)enumerable;
		return result;
	}

	/**
	 * Returns a {@link MutableEnumerator} over elements of type T.
	 *
	 * @return a {@link MutableEnumerator}.
	 */
	MutableEnumerator<E> newMutableEnumerator();

	boolean canWriteAssertion();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Removes each element of this collection that satisfies a given predicate.
	 *
	 * @param filter a predicate which returns {@code true} for elements to be removed.
	 *
	 * @return the number of elements removed.
	 */
	int removeEachIf( Predicate<? super E> filter );

	/**
	 * Extracts each element of this collection that satisfies a given predicate.
	 *
	 * @param filter a predicate which returns {@code true} for elements to be extracted.
	 *
	 * @return an {@link UnmodifiableList} containing the extracted elements.
	 */
	UnmodifiableList<E> extractEachIf( Predicate<? super E> filter );

	/**
	 * Removes the first element of this collection that satisfies a given predicate.
	 *
	 * @param filter a predicate which returns {@code true} for elements to be removed
	 *
	 * @return the element removed.
	 */
	E removeOneIf( Predicate<? super E> filter );

	/**
	 * Removes the first element of this enumerable.  (The enumerable must contain at least one element.)
	 *
	 * @return the removed element.
	 */
	E extractFirstElement();

	<T extends E> Optional<T> tryExtractOneInstanceOf( Class<T> javaClass );

	<T> MutableEnumerable<T> mutableConverted( TotalConverter<? extends T,? super E> converter );

	boolean clear();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for {@link MutableEnumerable}.
	 *
	 * @author michael.gr
	 */
	interface Defaults<E> extends MutableEnumerable<E>, UnmodifiableEnumerable.Defaults<E>
	{
		@Override default int removeEachIf( Predicate<? super E> filter )
		{
			assert filter != null : new IllegalArgumentException();
			int count = 0;
			for( MutableEnumerator<E> enumerator = newMutableEnumerator(); !enumerator.isFinished(); enumerator.moveNext() )
			{
				E element = enumerator.getCurrent();
				if( filter.test( element ) )
				{
					enumerator.deleteCurrent();
					count++;
				}
			}
			return count;
		}

		@Override default UnmodifiableList<E> extractEachIf( Predicate<? super E> filter )
		{
			List<E> legacyList = null;
			for( MutableEnumerator<E> enumerator = newMutableEnumerator(); !enumerator.isFinished(); enumerator.moveNext() )
			{
				E element = enumerator.getCurrent();
				if( filter.test( element ) )
				{
					if( legacyList == null )
						legacyList = new ArrayList<>();
					legacyList.add( element );
					enumerator.deleteCurrent();
				}
			}
			return legacyList == null ? UnmodifiableList.of() : UnmodifiableList.from( legacyList, legacyList.size() );
		}

		@Override default E removeOneIf( Predicate<? super E> filter )
		{
			assert filter != null : new IllegalArgumentException();
			for( MutableEnumerator<E> enumerator = newMutableEnumerator(); !enumerator.isFinished(); enumerator.moveNext() )
			{
				E element = enumerator.getCurrent();
				if( filter.test( element ) )
				{
					enumerator.deleteCurrent();
					return element;
				}
			}
			return null;
		}

		@Override default E extractFirstElement()
		{
			MutableEnumerator<E> enumerator = newMutableEnumerator();
			assert !enumerator.isFinished() : new NoSuchElementException();
			E current = enumerator.getCurrent();
			enumerator.deleteCurrent();
			return current;
		}

		@Override default UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
		{
			return newMutableEnumerator();
		}

		@Override default boolean isFrozen()
		{
			return false;
		}

		@Override default <T extends E> Optional<T> tryExtractOneInstanceOf( Class<T> javaClass )
		{
			for( MutableEnumerator<E> enumerator = newMutableEnumerator(); !enumerator.isFinished(); enumerator.moveNext() )
			{
				E element = enumerator.getCurrent();
				if( javaClass.isInstance( element ) )
				{
					enumerator.deleteCurrent();
					@SuppressWarnings( "unchecked" )
					T result = (T)element;
					return Optional.of( result );
				}
			}
			return Optional.empty();
		}

		@Override default <T> MutableEnumerable<T> mutableConverted( TotalConverter<? extends T,? super E> converter )
		{
			return ConversionCollections.newConvertingMutableEnumerable( this, converter );
		}

		@Override default boolean clear()
		{
			boolean modified = false;
			for( MutableEnumerator<E> enumerator = newMutableEnumerator();  !enumerator.isFinished();  enumerator.moveNext() )
			{
				enumerator.deleteCurrent();
				modified = true;
			}
			return modified;
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Default methods for decorating {@link MutableEnumerable}.
	 *
	 * @author michael.gr
	 */
	interface Decorator<E> extends Defaults<E>, UnmodifiableEnumerable.Decorator<E>
	{
		MutableEnumerable<E> getDecoratedMutableEnumerable();

		@Override default UnmodifiableEnumerable<E> getDecoratedUnmodifiableEnumerable()
		{
			return getDecoratedMutableEnumerable();
		}

		@Override default MutableEnumerator<E> newMutableEnumerator()
		{
			MutableEnumerable<E> decoree = getDecoratedMutableEnumerable();
			return decoree.newMutableEnumerator();
		}

		@Override default UnmodifiableEnumerator<E> newUnmodifiableEnumerator()
		{
			return MutableEnumerable.Defaults.super.newUnmodifiableEnumerator();
		}

		@Override default boolean isFrozen()
		{
			return false;
		}

		@Override default boolean canWriteAssertion()
		{
			return getDecoratedMutableEnumerable().canWriteAssertion();
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
		@Override public MutableEnumerable<E> getDecoratedMutableEnumerable()
		{
			return this;
		}
	}
}