package mikenakis.tyraki.conversion;

import mikenakis.tyraki.UnmodifiableList;

/**
 * Provides a subset view of the given {@link UnmodifiableList}.
 *
 * @param <T> the type of the elements.
 *
 * @author michael.gr
 */
final class SubList<T> extends AbstractList<T>
{
	private final UnmodifiableList<T> list;
	private final int startOffset;
	private final int endOffset;

	SubList( UnmodifiableList<T> list, int startOffset, int endOffset )
	{
		super( list.getEqualityComparator() );
		assert startOffset >= 0;
		assert endOffset >= 0;
		assert startOffset < endOffset;
		this.list = list;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}

	@Override public boolean isFrozen()
	{
		return list.isFrozen();
	}

	@Override public int size()
	{
		int end = Math.min( endOffset, list.size() );
		return Math.max( 0, end - startOffset );
	}

	@Override public int getModificationCount()
	{
		return list.getModificationCount();
	}

	@Override public T get( int index )
	{
		return list.get( startOffset + index );
	}
}
