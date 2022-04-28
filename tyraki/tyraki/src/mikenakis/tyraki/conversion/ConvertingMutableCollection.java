package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.coherence.Coherence;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.UnmodifiableEnumerator;

import java.util.Optional;

/**
 * Converts items from one type to another using a {@link Function1}.
 *
 * @param <T> the type to convert to
 * @param <F> the type to convert from
 *
 * @author michael.gr
 */
class ConvertingMutableCollection<T, F> extends AbstractUnmodifiableCollection<T> implements MutableCollection.Defaults<T>
{
	private final MutableCollection<F> collection;
	private final Function1<? extends T,? super F> converter;
	private final Function1<F,? super T> reverter;

	ConvertingMutableCollection( MutableCollection<F> collection, Function1<? extends T,? super F> converter, Function1<F,? super T> reverter,
		EqualityComparator<? super T> equalityComparator )
	{
		super( equalityComparator );
		this.collection = collection;
		this.converter = converter;
		this.reverter = reverter;
	}

	@Override public Coherence coherence()
	{
		return collection.coherence();
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
		F from = reverter.invoke( element );
		Optional<F> foundItem = collection.tryGet( from );
		return foundItem.map( i -> converter.invoke( i ) );
	}

	@Override public Optional<T> tryAdd( T element )
	{
		F from = reverter.invoke( element );
		return collection.tryAdd( from ).map( t -> converter.invoke( t ) );
	}

	@Override public boolean tryRemove( T element )
	{
		F from = reverter.invoke( element );
		return collection.tryRemove( from );
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		MutableEnumerator<F> mutableEnumerator = collection.newMutableEnumerator();
		return new ConvertingMutableEnumerator<>( mutableEnumerator, converter );
	}
}
