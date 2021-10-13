package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.PartialConverter;
import mikenakis.tyraki.TotalConverter;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.tyraki.UnmodifiableEnumerator;
import mikenakis.kit.EqualityComparator;

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
	private final TotalConverter<? extends T,? super F> converter;
	private final PartialConverter<? extends F,? super T> reverter;

	ConvertingUnmodifiableCollection( UnmodifiableCollection<F> collectionToConvert, TotalConverter<? extends T,? super F> converter, PartialConverter<? extends F,? super T> reverter,
		EqualityComparator<? super T> equalityComparator )
	{
		super( equalityComparator );
		this.collectionToConvert = collectionToConvert;
		this.converter = converter;
		this.reverter = reverter;
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

	@Override public final boolean isFrozen()
	{
		return false;
	}

	@Override public final int size()
	{
		return collectionToConvert.size();
	}

	@Override public final Optional<T> tryGet( T element )
	{
		assert element != null;
		Optional<? extends F> from = reverter.convert( element );
		Optional<F> foundItem = from.flatMap( f -> collectionToConvert.tryGet( f ) );
		return foundItem.map( i -> converter.invoke( i ) );
	}
}
