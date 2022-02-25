package mikenakis.tyraki.conversion;

import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.tyraki.UnmodifiableCollection;
import mikenakis.kit.EqualityComparator;

import java.util.Optional;
import java.util.function.Predicate;

/**
 * Filtering {@link UnmodifiableCollection}.
 *
 * @param <E> the type of elements in the Collection.
 *
 * @author michael.gr
 */
final class FilteringCollection<E> extends FilteringEnumerable<E> implements UnmodifiableCollection.Defaults<E>
{
	private final UnmodifiableCollection<E> collectionToFilter;

	FilteringCollection( UnmodifiableCollection<E> collectionToFilter, Predicate<E> predicate )
	{
		super( collectionToFilter, predicate );
		this.collectionToFilter = collectionToFilter;
	}

	@Override public boolean isFrozenAssertion()
	{
		return collectionToFilter.isFrozenAssertion();
	}

	@Override public EqualityComparator<? super E> getEqualityComparator()
	{
		return collectionToFilter.getEqualityComparator();
	}

	@Override public int size()
	{
		return countElements();
	}

	@Override public int getModificationCount()
	{
		return collectionToFilter.getModificationCount();
	}

	@Override public Optional<E> tryGet( E element )
	{
		assert element != null;
		if( !predicate.test( element ) )
			return Optional.empty();
		return collectionToFilter.tryGet( element );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " elements";
	}
}
