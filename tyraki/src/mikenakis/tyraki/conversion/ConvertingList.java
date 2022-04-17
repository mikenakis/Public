package mikenakis.tyraki.conversion;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.functional.Function1;
import mikenakis.tyraki.TotalConverterWithIndex;
import mikenakis.tyraki.UnmodifiableList;

import java.util.Optional;

/**
 * Converts items from one type to another using a {@link Function1}.
 *
 * @param <T> the type to convert to.
 * @param <F> the type to convert from.
 *
 * @author michael.gr
 */
class ConvertingList<T, F> extends AbstractList<T>
{
	private final UnmodifiableList<F> list;
	private final TotalConverterWithIndex<? extends T,? super F> converter;
	private final Function1<Optional<? extends F>,? super T> reverter;

	ConvertingList( UnmodifiableList<F> list, TotalConverterWithIndex<? extends T,? super F> converter, //
		Function1<Optional<? extends F>,? super T> reverter, EqualityComparator<? super T> equalityComparator )
	{
		super( equalityComparator );
		assert list != null;
		assert converter != null;
		assert reverter != null;
		this.list = list;
		this.converter = converter;
		this.reverter = reverter;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return list.mustBeImmutableAssertion();
	}

	@Override public final int size()
	{
		return list.size();
	}

	@Override public final Optional<T> tryGet( T element )
	{
		assert element != null;
		int index = indexOf( element );
		if( index == -1 )
			return Optional.empty();
		F foundItem = list.get( index );
		return Optional.of( converter.invoke( index, foundItem ) );
	}

	@Override public int getModificationCount()
	{
		return list.getModificationCount();
	}

	@Override public final T get( int index )
	{
		F from = list.get( index );
		return converter.invoke( index, from );
	}

	@Override public final int indexOf( T element )
	{
		Optional<? extends F> from = reverter.invoke( element );
		if( from.isEmpty() )
			return -1;
		return list.indexOf( from.get() );
	}
}
