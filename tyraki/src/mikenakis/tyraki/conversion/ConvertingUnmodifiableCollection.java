package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.UnmodifiableCollection;
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
class ConvertingUnmodifiableCollection<T, F> extends AbstractUnmodifiableCollection<T>
{
	private final UnmodifiableCollection<F> collectionToConvert;
	private final Function1<? extends T,? super F> converter;
	private final Function1<Optional<? extends F>,? super T> reverter;

	ConvertingUnmodifiableCollection( UnmodifiableCollection<F> collectionToConvert, Function1<? extends T,? super F> converter,
		Function1<Optional<? extends F>,? super T> reverter, EqualityComparator<? super T> equalityComparator )
	{
		super( equalityComparator );
		this.collectionToConvert = collectionToConvert;
		this.converter = converter;
		this.reverter = reverter;
	}

	@Override public boolean isFrozenAssertion()
	{
		return collectionToConvert.isFrozenAssertion();
	}

	@Override public final UnmodifiableEnumerator<T> newUnmodifiableEnumerator()
	{
		UnmodifiableEnumerator<F> unmodifiableEnumerator = collectionToConvert.newUnmodifiableEnumerator();
		return new ConvertingUnmodifiableEnumerator<>( unmodifiableEnumerator, ( i, e ) -> converter.invoke( e ) );
	}

	@Override public int getModificationCount()
	{
		return collectionToConvert.getModificationCount();
	}

	@Override public final int size()
	{
		return collectionToConvert.size();
	}

	@Override public final Optional<T> tryGet( T element )
	{
		assert element != null;
		Optional<? extends F> from = reverter.invoke( element );
		Optional<F> foundItem = from.flatMap( f -> collectionToConvert.tryGet( f ) );
		return foundItem.map( i -> converter.invoke( i ) );
	}
}
