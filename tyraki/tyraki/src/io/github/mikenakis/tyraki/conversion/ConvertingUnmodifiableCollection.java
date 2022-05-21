package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.kit.EqualityComparator;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.tyraki.UnmodifiableCollection;
import io.github.mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * Converts items from one type to another using a {@link Function1}.
 *
 * @param <T> the type to convert to
 * @param <F> the type to convert from
 *
 * @author michael.gr
 */
class ConvertingUnmodifiableCollection<T, F> extends AbstractUnmodifiableCollection<T>
{
	private final UnmodifiableCollection<F> collection;
	private final Function1<? extends T,? super F> converter;
	private final Function1<Optional<? extends F>,? super T> reverter;

	ConvertingUnmodifiableCollection( UnmodifiableCollection<F> collection, Function1<? extends T,? super F> converter,
		Function1<Optional<? extends F>,? super T> reverter, EqualityComparator<? super T> equalityComparator )
	{
		super( equalityComparator );
		this.collection = collection;
		this.converter = converter;
		this.reverter = reverter;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return collection.mustBeImmutableAssertion();
	}

	@Override public final UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<F> unmodifiableEnumerator = collection.newUnmodifiableEnumerator();
		return new ConvertingUnmodifiableEnumerator<>( unmodifiableEnumerator, ( i, e ) -> converter.invoke( e ) );
	}

	@Override public int getModificationCount()
	{
		return collection.getModificationCount();
	}

	@Override public final int size()
	{
		return collection.size();
	}

	@Override public final Optional<T> tryGet( T element )
	{
		assert element != null;
		Optional<? extends F> from = reverter.invoke( element );
		Optional<F> foundItem = from.flatMap( f -> collection.tryGet( f ) );
		return foundItem.map( i -> converter.invoke( i ) );
	}
}
