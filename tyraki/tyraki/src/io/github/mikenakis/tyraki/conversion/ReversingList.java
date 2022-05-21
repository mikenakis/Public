package io.github.mikenakis.tyraki.conversion;

import io.github.mikenakis.tyraki.UnmodifiableList;

import java.util.Optional;

/**
 * Provides a reversed view of the given {@link UnmodifiableList}.
 *
 * @param <T> the type of the elements.
 *
 * @author michael.gr
 */
final class ReversingList<T> extends AbstractList<T>
{
	private final UnmodifiableList<T> listToConvert;

	ReversingList( UnmodifiableList<T> listToConvert )
	{
		super( listToConvert.getEqualityComparator() );
		this.listToConvert = listToConvert;
	}

	@Override public boolean mustBeImmutableAssertion()
	{
		return listToConvert.mustBeImmutableAssertion();
	}

	@Override public int size()
	{
		return listToConvert.size();
	}

	@Override public Optional<T> tryGet( T element )
	{
		assert element != null;
		return listToConvert.tryGet( element );
	}

	@Override public int getModificationCount()
	{
		return listToConvert.getModificationCount();
	}

	@Override public T get( int index )
	{
		index = size() - 1 - index;
		return listToConvert.get( index );
	}

	@Override public int indexOf( T element )
	{
		int index = listToConvert.indexOf( element );
		if( index != -1 )
			index = size() - 1 - index;
		return index;
	}
}
