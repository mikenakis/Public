package mikenakis.tyraki.conversion;

import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.MutableCollection;
import mikenakis.tyraki.MutableEnumerator;
import mikenakis.tyraki.TotalConverter;
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
class ConvertingMutableCollection<T, F> extends AbstractUnmodifiableCollection<T> implements MutableCollection.Defaults<T>
{
	private final MutableCollection<F> collectionToConvert;
	private final TotalConverter<? extends T,? super F> converter;
	private final TotalConverter<F,? super T> reverter;

	ConvertingMutableCollection( MutableCollection<F> collectionToConvert, TotalConverter<? extends T,? super F> converter, TotalConverter<F,? super T> reverter,
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
		F from = reverter.invoke( element );
		Optional<F> foundItem = collectionToConvert.tryGet( from );
		return foundItem.map( i -> converter.invoke( i ) );
	}

	@Override public boolean tryAdd( T element )
	{
		F from = reverter.invoke( element );
		return collectionToConvert.tryAdd( from );
	}

	@Override public boolean tryRemove( T element )
	{
		F from = reverter.invoke( element );
		return collectionToConvert.tryRemove( from );
	}

	@Override public boolean canWriteAssertion()
	{
		return collectionToConvert.canWriteAssertion();
	}

	@Override public MutableEnumerator<T> newMutableEnumerator()
	{
		MutableEnumerator<F> mutableEnumerator = collectionToConvert.newMutableEnumerator();
		return new ConvertingMutableEnumerator<>( mutableEnumerator, e -> converter.invoke( e ) );
	}
}
